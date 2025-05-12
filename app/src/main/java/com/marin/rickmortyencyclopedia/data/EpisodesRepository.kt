/**
 * Created by Backbase RnD BV on 10/05/2025.
 */
package com.marin.rickmortyencyclopedia.data

import com.marin.rickmortyencyclopedia.model.EpisodesSnapshot
import kotlinx.coroutines.flow.Flow


interface EpisodesRepository {

    val episodesSnapshotFlow: Flow<Result<EpisodesSnapshot>>

    suspend fun nextPage(from: String)

    suspend fun refreshAll(first: String): Result<Unit>
}