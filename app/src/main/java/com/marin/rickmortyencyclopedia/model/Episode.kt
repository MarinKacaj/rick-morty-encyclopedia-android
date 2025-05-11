/**
 * Created by Backbase RnD BV on 09/05/2025.
 */
package com.marin.rickmortyencyclopedia.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Episode(
    val id: Int,
    val name: String,
    @SerialName("air_date")
    val airDate: String,
    @SerialName("episode")
    val code: String,
    val characters: List<String>,
    val url: String,
    val created: String,
)