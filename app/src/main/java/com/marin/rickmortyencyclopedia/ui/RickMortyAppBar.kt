/**
 * Created by Backbase RnD BV on 09/05/2025.
 */
package com.marin.rickmortyencyclopedia.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.marin.rickmortyencyclopedia.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RickMortyAppBar(
    modifier: Modifier = Modifier,
    canNavBack: Boolean = false,
    title: String = "Rick and Morty"
) {

    TopAppBar(
        modifier = modifier, // todo reconsider
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if (canNavBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    )
}