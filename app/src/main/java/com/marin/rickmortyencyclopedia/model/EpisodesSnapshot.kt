/**
 * Created by Backbase RnD BV on 10/05/2025.
 */
package com.marin.rickmortyencyclopedia.model


data class EpisodesSnapshot(
    val episodes: Episodes,
    val lastRefreshed: Long,
)