/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.ui.character.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marin.rickmortyencyclopedia.R
import com.marin.rickmortyencyclopedia.ui.RickMortyAppBar


@Composable
fun CharacterDetailsScreen(
    id: Int,
    viewModel: CharacterDetailsViewModel = viewModel(
        factory = CharacterDetailsViewModel.Factory,
        extras = MutableCreationExtras().apply {
            val args = bundleOf(CharacterDetailsViewModel.PARAM_CHARACTER_ID to id)
            set(DEFAULT_ARGS_KEY, args)
        }
    ),
    onNavUp: () -> Unit = {},
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            RickMortyAppBar(
                modifier = Modifier.fillMaxWidth(), canNavBack = true,
                title = stringResource(R.string.character_id_title, id),
                onNavBack = onNavUp,
            )
        },
        content = { innerPaddings ->
            Text(modifier = Modifier.padding(innerPaddings), text = "This is the id $id")
        }
    )
}