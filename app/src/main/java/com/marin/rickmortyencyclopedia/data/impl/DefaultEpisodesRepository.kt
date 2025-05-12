/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.data.impl

import com.marin.rickmortyencyclopedia.data.ApiException
import com.marin.rickmortyencyclopedia.data.EpisodesDataSource
import com.marin.rickmortyencyclopedia.data.EpisodesRepository
import com.marin.rickmortyencyclopedia.model.Episodes
import com.marin.rickmortyencyclopedia.model.EpisodesInfo
import com.marin.rickmortyencyclopedia.model.EpisodesSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.ConcurrentHashMap


class DefaultEpisodesRepository(
    val networkDataSource: EpisodesDataSource,
) : EpisodesRepository {

    private val inMemoryCache: MutableMap<String, EpisodesSnapshot> = ConcurrentHashMap()

    private val _episodesSnapshotFlow: MutableSharedFlow<Result<EpisodesSnapshot>> =
        MutableSharedFlow<Result<EpisodesSnapshot>>() // no replay
    override val episodesSnapshotFlow: Flow<Result<EpisodesSnapshot>> = _episodesSnapshotFlow

    override suspend fun nextPage(
        from: String,
    ) {
        val cachedSnapshot: EpisodesSnapshot? = inMemoryCache[from]
        if (cachedSnapshot == null) {
            networkDataSource.getEpisodes(from).map { episodes ->
                EpisodesSnapshot(
                    episodes = episodes,
                    lastRefreshed = System.currentTimeMillis(),
                )
            }.onSuccess { episodesSnapshot ->
                inMemoryCache[from] = episodesSnapshot
                _episodesSnapshotFlow.emit(Result.success(episodesSnapshot))
            }.onFailure {
                _episodesSnapshotFlow.emit(Result.failure(it))
            }
        } else {
            _episodesSnapshotFlow.emit(Result.success(cachedSnapshot))
        }
    }

    override suspend fun refreshAll(first: String): Result<Unit> {
        val localCache: LinkedHashMap<String, EpisodesSnapshot> = LinkedHashMap()
        var nextPage: String? = first
        var lastInfo: EpisodesInfo? = null
        var result: Result<Unit> = Result.success(Unit)
        while (nextPage != null && result.isSuccess) {
            networkDataSource.getEpisodes(nextPage).map { episodes ->
                EpisodesSnapshot(
                    episodes = episodes,
                    lastRefreshed = System.currentTimeMillis(),
                )
            }.onSuccess { episodesSnapshot ->
                localCache[nextPage] = episodesSnapshot
                lastInfo = episodesSnapshot.episodes.info
                nextPage = episodesSnapshot.episodes.info.next
            }.onFailure {
                result = Result.failure(it)
            }
        }
        if (result.isSuccess && lastInfo != null) {
            inMemoryCache.clear()
            inMemoryCache.putAll(localCache)
            _episodesSnapshotFlow.emit(
                Result.success(
                    EpisodesSnapshot(
                        lastRefreshed = System.currentTimeMillis(),
                        episodes = Episodes(
                            info = lastInfo,
                            results = localCache.entries.map { it.value.episodes.results }.flatten()
                        )
                    )
                )
            )
        } else {
            result = Result.failure(ApiException("Could not fetch episodes"))
        }
        return result
    }
}