/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.data.impl

import com.marin.rickmortyencyclopedia.data.EpisodesNetworkDataSource
import com.marin.rickmortyencyclopedia.data.EpisodesRepository
import com.marin.rickmortyencyclopedia.model.EpisodesSnapshot


class DefaultEpisodesRepository(
    val networkDataSource: EpisodesNetworkDataSource,
) : EpisodesRepository {

    override suspend fun getEpisodes(
        from: String,
        force: Boolean,
    ): Result<EpisodesSnapshot> {
        return networkDataSource.getEpisodes(from).map { episodes ->
            EpisodesSnapshot(
                episodes = episodes,
                lastRefreshed = System.currentTimeMillis(),
            )
        }
    }
}