/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.ui.character.details

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.marin.rickmortyencyclopedia.R
import com.marin.rickmortyencyclopedia.ui.RickMortyAppBar


@Composable
fun CharacterDetailsScreen(
    id: Int,
    viewModel: CharacterDetailsViewModel = viewModel(
        factory = CharacterDetailsViewModel.Factory,
        extras = MutableCreationExtras(
            (LocalViewModelStoreOwner.current as? HasDefaultViewModelProviderFactory)?.defaultViewModelCreationExtras
                ?: CreationExtras.Empty
        ).apply {
            val args = bundleOf(CharacterDetailsViewModel.PARAM_CHARACTER_ID to id)
            set(DEFAULT_ARGS_KEY, args)
        }
    ),
    onNavUp: () -> Unit = {},
) {

    val uiState: CharacterUiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            Box(modifier = Modifier.padding(innerPaddings)) {
                when (uiState) {
                    is CharacterUiState.Error -> {
                        Text(text = "Error") // todo from common
                    }
                    CharacterUiState.Loading -> {
                        Text(text = "Loading") // todo from common
                    }
                    is CharacterUiState.Success -> {
                        val successUiState = uiState as CharacterUiState.Success
                        AsyncImage(
                            model = successUiState.data.character.image,
                            contentDescription = stringResource(R.string.character_photo, successUiState.data.character.name),
                            contentScale = ContentScale.Fit,
                            onError = {
                                Log.e("CharacterDetailsScreenError", it.result.toString())
                            },
                            modifier = Modifier.clip(CircleShape).size(56.dp) // todo DS
                        )
                    }
                }
            }
        }
    )
}