/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.ui.character.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.marin.rickmortyencyclopedia.R
import com.marin.rickmortyencyclopedia.ui.RickMortyAppBar
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.marin.rickmortyencyclopedia.ui.theme.RickMortyEncyclopediaAppTheme


@Composable
fun EpisodeCharactersIdsScreen(
    episodeCode: String,
    charactersIds: List<Int>,
    onCharacterIdSelected: (Int) -> Unit = {},
    onNavUp: () -> Unit = {},
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            RickMortyAppBar(
                canNavBack = true,
                title = stringResource(R.string.characters_ids, episodeCode),
                onNavBack = onNavUp,
            )
        },
        content = { innerPaddings: PaddingValues ->
            // lazy column not really needed here, just in case. column is otherwise enough
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings)
            ) {

                items(charactersIds, key = { it }) { characterId ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = RickMortyEncyclopediaAppTheme.spacing.spacerMedium)
                            .clickable { onCharacterIdSelected(characterId) },
                    ) {
                        Text(
                            modifier = Modifier.padding(top = RickMortyEncyclopediaAppTheme.spacing.spacerMedium),
                            text = stringResource(R.string.character_id_entry, characterId),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = RickMortyEncyclopediaAppTheme.spacing.spacerXSmall),
                            thickness = RickMortyEncyclopediaAppTheme.sizing.sizerXXSmall
                        )
                    }
                }
            }
        }
    )
}