/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.data

import com.marin.rickmortyencyclopedia.model.Character


interface CharacterDataSource {

    suspend fun getCharacter(id: Int): Result<Character>
}