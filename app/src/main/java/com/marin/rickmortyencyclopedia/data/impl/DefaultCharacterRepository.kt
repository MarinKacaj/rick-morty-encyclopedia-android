/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.data.impl

import com.marin.rickmortyencyclopedia.data.CharacterDataSource
import com.marin.rickmortyencyclopedia.data.CharacterRepository
import com.marin.rickmortyencyclopedia.model.CharacterSnapshot


class DefaultCharacterRepository(
    val networkDataSource: CharacterDataSource,
) : CharacterRepository {

    override suspend fun getCharacter(
        id: Int,
        force: Boolean,
    ): Result<CharacterSnapshot> {
        return networkDataSource.getCharacter(id).map { character ->
            CharacterSnapshot(
                character = character,
                lastRefreshed = System.currentTimeMillis(),
            )
        }
    }
}