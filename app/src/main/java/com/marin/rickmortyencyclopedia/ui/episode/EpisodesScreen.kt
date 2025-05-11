/**
 * Created by Backbase RnD BV on 09/05/2025.
 */
package com.marin.rickmortyencyclopedia.ui.episode

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marin.rickmortyencyclopedia.R
import com.marin.rickmortyencyclopedia.ui.RickMortyAppBar
import com.marin.rickmortyencyclopedia.ui.common.ErrorState
import com.marin.rickmortyencyclopedia.ui.common.LoadingState
import com.marin.rickmortyencyclopedia.ui.theme.RickMortyEncyclopediaAppTheme
import com.marin.rickmortyencyclopedia.ui.util.DateTimeReformatter
import kotlinx.coroutines.flow.filter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


@Composable
fun EpisodesScreen(
    viewModel: EpisodesViewModel = viewModel(factory = EpisodesViewModel.Factory),
    onEpisodeSelected: (code: String, charactersIds: List<Int>) -> Unit = { _, _ -> },
) {

    val uiState: EpisodesUiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            RickMortyAppBar(canNavBack = false, title = stringResource(R.string.episodes))
        },
        content = { innerPaddings: PaddingValues ->

            when (uiState) {
                is EpisodesUiState.Error -> {
                    ErrorState(
                        modifier = Modifier.padding(innerPaddings),
                        errorMessage = (uiState as EpisodesUiState.Error).error,
                    )
                }

                is EpisodesUiState.Success -> {

                    val successUiState = (uiState as EpisodesUiState.Success)
                    val listState = rememberLazyListState()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPaddings),
                        state = listState,
                    ) {

                        item(key = "last-update-timestamp") {
                            LastUpdate(lastUpdate = successUiState.data.lastRefreshed)
                        }

                        items(
                            items = successUiState.data.episodes.results,
                            key = { episode -> episode.id }
                        ) { episode ->
                            EpisodeItem(
                                id = episode.id,
                                airDate = DateTimeReformatter.reformat(episode.airDate),
                                name = episode.name,
                                code = episode.code,
                                onEpisodeSelected = {
                                    onEpisodeSelected(
                                        episode.code,
                                        episode.characters.mapNotNull { // just to be on the safe side
                                            it.toUri().pathSegments.lastOrNull()?.toInt()
                                        })
                                },
                            )
                        }

                        if (successUiState.isEndOfList) {
                            item(key = "end-of-list") {
                                EndOfListItem()
                            }
                        } else {
                            item(key = "scroll-loading") {
                                EndOfListLoadingItem()
                            }
                        }
                    }

                    SelfLoadingListManager(listState) {
                        viewModel.loadNextEpisodes()
                    }
                }

                EpisodesUiState.Loading -> {
                    LoadingState(modifier = Modifier.padding(innerPaddings))
                }
            }
        }
    )
}

@Composable
fun LastUpdate(
    modifier: Modifier = Modifier,
    zoneIdentifier: String = ZoneId.systemDefault().id,
    lastUpdate: Long,
) {
    val zoneId = ZoneId.of(zoneIdentifier)
    val localLastUpdate = Instant.ofEpochMilli(lastUpdate).atZone(zoneId).toLocalDateTime()
    val formattedLastUpdate = DateTimeFormatter.ofLocalizedDateTime(
        FormatStyle.FULL
    ).withZone(zoneId).format(localLastUpdate)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(RickMortyEncyclopediaAppTheme.spacing.spacerMedium),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(
                R.string.last_update,
                formattedLastUpdate,
            ),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
fun EpisodeItem(
    modifier: Modifier = Modifier,
    id: Int,
    airDate: String,
    name: String,
    code: String,
    onEpisodeSelected: (Int) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(RickMortyEncyclopediaAppTheme.spacing.spacerMedium)
            .clickable { onEpisodeSelected(id) },
        verticalArrangement = Arrangement.spacedBy(
            RickMortyEncyclopediaAppTheme.spacing.spacerXXSmall
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Icon(
                modifier = Modifier
                    .wrapContentSize(),
                imageVector = Icons.Default.DateRange,
                contentDescription = stringResource(R.string.episode_air_date_a11y),
            )
            Text(
                text = airDate,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = code,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(RickMortyEncyclopediaAppTheme.spacing.spacerXSmall)
        )
        HorizontalDivider(thickness = RickMortyEncyclopediaAppTheme.sizing.sizerXXSmall)
    }
}

@Composable
fun EndOfListItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(R.string.end_of_list))
    }
}

@Composable
fun EndOfListLoadingItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.wrapContentSize(),
        )
    }
}

@Composable
fun SelfLoadingListManager(
    listState: LazyListState,
    buffer: Int = 3,
    onLoadMore: () -> Unit = {},
) {

    val loadMore: State<Pair<Int, Int>> = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val numItemsInLayout = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            Pair(lastVisibleItemIndex, numItemsInLayout - buffer)
        }
    }

    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore.value }.filter { it.first > it.second }.collect { onLoadMore() }
    }
}