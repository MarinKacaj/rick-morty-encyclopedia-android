/**
 * Created by Backbase RnD BV on 10/05/2025.
 */
package com.marin.rickmortyencyclopedia.data

import com.marin.rickmortyencyclopedia.model.EpisodesSnapshot


interface EpisodesRepository {

    suspend fun getEpisodes(from: String, force: Boolean = false): Result<EpisodesSnapshot>
}