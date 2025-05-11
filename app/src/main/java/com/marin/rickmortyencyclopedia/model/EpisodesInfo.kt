/**
 * Created by Backbase RnD BV on 10/05/2025.
 */
package com.marin.rickmortyencyclopedia.model

import kotlinx.serialization.Serializable


@Serializable
data class EpisodesInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)