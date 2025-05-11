/**
 * Created by Backbase RnD BV on 10/05/2025.
 */
package com.marin.rickmortyencyclopedia.data

import com.marin.rickmortyencyclopedia.model.Episodes


interface EpisodesDataSource {

    suspend fun getEpisodes(from: String): Result<Episodes>
}