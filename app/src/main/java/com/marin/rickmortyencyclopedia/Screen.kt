/**
 * Created by Backbase RnD BV on 09/05/2025.
 */
package com.marin.rickmortyencyclopedia

import kotlinx.serialization.Serializable


sealed interface Screen {

    @Serializable
    data object Episodes : Screen

    @Serializable
    data class Characters(
        val charactersIds: List<Int>,
        val episodeCode: String,
    ) : Screen

    @Serializable
    data class CharacterDetails(val characterId: Int)
}