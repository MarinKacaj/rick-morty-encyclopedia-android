/**
 * Created by Backbase RnD BV on 10/05/2025.
 */
package com.marin.rickmortyencyclopedia.model

import kotlinx.serialization.Serializable

@Serializable
data class Episodes(
    val info: EpisodesInfo,
    val results: List<Episode>,
)