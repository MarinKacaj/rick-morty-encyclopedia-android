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
import com.marin.rickmortyencyclopedia.model.EpisodesSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    private val _uiState: MutableStateFlow<EpisodesUiState> = MutableStateFlow(initialUiState)
    val uiState: StateFlow<EpisodesUiState> = _uiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L), // 5000ms = 5s => reasonable timeout suggested in docs and Google blogs
        initialUiState,
    )

    init {
        viewModelScope.launch {

            val result = episodesRepository.getEpisodes(
                from = "${AppContainer.BASE_API_URL}/episode"
            )

            val episodesSnapshot: EpisodesSnapshot? = result.getOrNull()
            if (result.isSuccess && episodesSnapshot != null) {
                _uiState.update {
                    EpisodesUiState.Success(
                        data = episodesSnapshot,
                        isEndOfList = episodesSnapshot.episodes.info.next == null,
                    )
                }
            } else {
                _uiState.update {
                    EpisodesUiState.Error(error = result.toString())
                }
            }
        }
    }


    fun loadNextEpisodes() {

        viewModelScope.launch {

            val currentUiState: EpisodesUiState = uiState.value

            when (currentUiState) {

                is EpisodesUiState.Success -> {
                    val nextPage: String? = currentUiState.data.episodes.info.next
                    if (nextPage == null) {
                        _uiState.update {
                            currentUiState.copy(isEndOfList = true)
                        }
                    } else {

                        val result = episodesRepository.getEpisodes(nextPage)
                        val episodesSnapshot = result.getOrNull()

                        if (result.isSuccess && episodesSnapshot != null) {
                            _uiState.update {
                                currentUiState.copy(
                                    data = episodesSnapshot.copy(
                                        episodes = episodesSnapshot.episodes.copy(
                                            results = currentUiState.data.episodes.results + episodesSnapshot.episodes.results
                                        ),
                                    ),
                                    isEndOfList = episodesSnapshot.episodes.info.next == null,
                                )
                            }
                        }
                    }
                }

                else -> errorLogger(
                    TAG,
                    "Trying to get next page while current UI state is $currentUiState"
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