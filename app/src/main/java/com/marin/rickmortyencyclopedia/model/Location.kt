/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.model

import kotlinx.serialization.Serializable


@Serializable
data class Location(
    val name: String,
    val url: String,
)