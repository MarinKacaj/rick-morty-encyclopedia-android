/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.ui.character.details

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.marin.rickmortyencyclopedia.ui.common.ErrorState
import com.marin.rickmortyencyclopedia.ui.common.LoadingState
import com.marin.rickmortyencyclopedia.ui.theme.RickMortyEncyclopediaAppTheme
import com.marin.rickmortyencyclopedia.ui.util.debrief
import java.io.OutputStream


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

            when (uiState) {

                is CharacterUiState.Error -> {
                    ErrorState(
                        modifier = Modifier.padding(innerPaddings),
                        errorMessage = (uiState as CharacterUiState.Error).error
                    )
                }

                CharacterUiState.Loading -> {
                    LoadingState(modifier = Modifier.padding(innerPaddings))
                }

                is CharacterUiState.Success -> {

                    val successUiState = uiState as CharacterUiState.Success

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPaddings),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = RickMortyEncyclopediaAppTheme.spacing.spacerMedium)
                        ) {
                            AsyncImage(
                                model = successUiState.data.character.image,
                                contentDescription = stringResource(
                                    R.string.character_photo,
                                    successUiState.data.character.name
                                ),
                                contentScale = ContentScale.Fit,
                                onError = {
                                    Log.e("CharacterDetailsScreenError", it.result.toString())
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(RickMortyEncyclopediaAppTheme.sizing.sizerXXLarge)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(RickMortyEncyclopediaAppTheme.spacing.spacerXSmall)
                            )
                            LabeledProperty(
                                label = stringResource(R.string.character_name),
                                property = successUiState.data.character.name,
                            )
                            LabeledProperty(
                                label = stringResource(R.string.character_status),
                                property = successUiState.data.character.status,
                            )
                            LabeledProperty(
                                label = stringResource(R.string.character_species),
                                property = successUiState.data.character.species,
                            )
                            LabeledProperty(
                                label = stringResource(R.string.character_origin_name),
                                property = successUiState.data.character.origin.name,
                            )
                            LabeledProperty(
                                label = stringResource(R.string.character_appears_in_label),
                                property = stringResource(
                                    R.string.character_appears_in_num_episodes,
                                    successUiState.data.character.episode.size,
                                ),
                            )

                            ExportButton(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = RickMortyEncyclopediaAppTheme.spacing.spacerMedium),
                                characterDebrief = successUiState.data.character.debrief(),
                                characterId = id,
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun LabeledProperty(label: String, property: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = RickMortyEncyclopediaAppTheme.spacing.spacerXSmall),
        text = label,
        style = MaterialTheme.typography.labelLarge,
    )
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = property,
        style = MaterialTheme.typography.bodyLarge,
    )
    HorizontalDivider(
        modifier = Modifier.padding(top = RickMortyEncyclopediaAppTheme.spacing.spacerXSmall),
        thickness = RickMortyEncyclopediaAppTheme.sizing.sizerXXSmall,
    )
}

@Composable
fun ExportButton(modifier: Modifier = Modifier, characterDebrief: String, characterId: Int) {

    val contentResolver = LocalContext.current.contentResolver

    val characterFileCreator = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain"),
        onResult = { uri: Uri? ->
            if (uri != null) {
                contentResolver.openOutputStream(uri)
                    ?.use { stream: OutputStream ->
                        val bytes = characterDebrief.toByteArray()
                        stream.write(bytes)
                    }
            }
        }
    )

    Button(
        modifier = modifier,
        onClick = {
            // no need for effects here as onClick takes place outside
            characterFileCreator.launch("character-info-$characterId.txt")
        },
    ) {
        Text(text = stringResource(R.string.character_info_download))
    }
}