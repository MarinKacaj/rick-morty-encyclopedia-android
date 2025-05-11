/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.ui.character.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marin.rickmortyencyclopedia.data.CharacterRepository
import com.marin.rickmortyencyclopedia.model.CharacterSnapshot
import com.marin.rickmortyencyclopedia.ui.episode.EpisodesUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class CharacterUiState {

    data object Loading : CharacterUiState()

    data class Error(val error: String) : CharacterUiState()

    data class Success(val data: CharacterSnapshot) : CharacterUiState()
}


class CharacterViewModel(
    val id: Int,
    val characterRepository: CharacterRepository,
) : ViewModel() {

    private val initialUiState = CharacterUiState.Loading

    private val _uiState: MutableStateFlow<CharacterUiState> = MutableStateFlow(initialUiState)
    val uiState: StateFlow<CharacterUiState> = _uiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L), // 5000ms = 5s => reasonable timeout suggested in docs and Google blogs
        initialUiState,
    )

    init {

        viewModelScope.launch {

            withContext(Dispatchers.IO) { delay(3000L) } // todo undo

            val result = characterRepository.getCharacter(id = id)

            val characterSnapshot: CharacterSnapshot? = result.getOrNull()
            if (result.isSuccess && characterSnapshot != null) {
                _uiState.update {
                    CharacterUiState.Success(data = characterSnapshot)
                }
            } else {
                _uiState.update {
                    CharacterUiState.Error(error = result.toString())
                }
            }
        }
    }
}