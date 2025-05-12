/**
 * Created by Backbase RnD BV on 09/05/2025.
 */
package com.marin.rickmortyencyclopedia.ui.episode

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.marin.rickmortyencyclopedia.RickMortyEncyclopediaApp
import com.marin.rickmortyencyclopedia.data.AppContainer
import com.marin.rickmortyencyclopedia.data.EpisodesRepository
import com.marin.rickmortyencyclopedia.model.Episode
import com.marin.rickmortyencyclopedia.model.EpisodesSnapshot
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class EpisodesUiState {

    data object Loading : EpisodesUiState()

    data class Error(val error: String) : EpisodesUiState()

    data class Success(val data: EpisodesSnapshot, val isEndOfList: Boolean) : EpisodesUiState()
}

class EpisodesViewModel(
    val episodesRepository: EpisodesRepository,
    @get:VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val errorLogger: (tag: String, message: String) -> Unit = { _, _ -> },
) : ViewModel() {

    private val initialUiState = EpisodesUiState.Loading

    val uiState: StateFlow<EpisodesUiState> =
        episodesRepository.episodesSnapshotFlow.runningFold(initialUiState) { currentUiState: EpisodesUiState, result: Result<EpisodesSnapshot> ->
            when (currentUiState) {
                is EpisodesUiState.Success -> {
                    val episodesSnapshot = result.getOrNull()
                    if (result.isSuccess // self-explanatory
                        && episodesSnapshot != null // just in case we got null as success (shouldn't happen)
                    ) {

                        // easy lookups in new episodes to check if any has been updated
                        // why?
                        // the background worker could have updated any episode(s) as we load new pages
                        val idNewEpisodes: MutableMap<Int, Episode> =
                            episodesSnapshot.episodes.results.associate { it.id to it }
                                .toMutableMap()

                        val combined =
                            currentUiState.data.episodes.results + episodesSnapshot.episodes.results
                        val mapOfIds = combined.groupBy { it.id }
                        val fullyJoinedEpisodes: List<Episode> =
                            mapOfIds.entries.map { entry: Map.Entry<Int, List<Episode>> ->
                                // setting as a val for clarity
                                val id: Int = entry.key
                                // there's at least one, otherwise how did we get the id first place?
                                val existingEpisode = entry.value.first()
                                // is there any episode withe same id in the new list?
                                val newEpisodeWithId: Episode? = idNewEpisodes[id]
                                // update episode if in the new list or use existing otherwise
                                newEpisodeWithId ?: existingEpisode
                            }

                        currentUiState.copy(
                            data = episodesSnapshot.copy(
                                episodes = episodesSnapshot.episodes.copy(
                                    results = fullyJoinedEpisodes
                                ),
                            ),
                            isEndOfList = episodesSnapshot.episodes.info.next == null,
                        )
                    } else {
                        // nothing to change in the UI here as new page was not received
                        // an eventual second attempt by the user will trigger fetching the next page again
                        currentUiState
                    }
                }

                is EpisodesUiState.Error -> {
                    errorLogger(
                        TAG,
                        "Trying to get next page while current UI state is $currentUiState",
                    )
                    currentUiState
                }

                is EpisodesUiState.Loading -> {
                    val episodesSnapshot: EpisodesSnapshot? = result.getOrNull()
                    if (result.isSuccess && episodesSnapshot != null) {
                        EpisodesUiState.Success(
                            data = episodesSnapshot,
                            isEndOfList = episodesSnapshot.episodes.info.next == null,
                        )
                    } else {
                        EpisodesUiState.Error(error = result.toString())
                    }
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L), // 5000ms = 5s => reasonable timeout suggested in docs and Google blogs
            initialUiState,
        )

    init {
        viewModelScope.launch {

            episodesRepository.nextPage(
                from = "${AppContainer.BASE_API_URL}/episode"
            )
        }
    }


    fun loadNextEpisodes() {

        viewModelScope.launch {

            val currentUiState = uiState.value
            if (currentUiState is EpisodesUiState.Success && currentUiState.data.episodes.info.next != null) {
                episodesRepository.nextPage(currentUiState.data.episodes.info.next)
            } else {
                errorLogger(
                    TAG,
                    "Trying to get next page while current UI state is $currentUiState",
                )
            }
        }
    }

    companion object {

        private const val TAG = "EpisodesViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as RickMortyEncyclopediaApp)
                val episodesRepository = app.appContainer.episodesRepository
                val errorLogger = app.appContainer.errorLogger
                EpisodesViewModel(episodesRepository, errorLogger)
            }
        }
    }
}