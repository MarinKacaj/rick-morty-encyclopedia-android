/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.ui.character.details

import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.marin.rickmortyencyclopedia.RickMortyEncyclopediaApp
import com.marin.rickmortyencyclopedia.data.CharacterRepository
import com.marin.rickmortyencyclopedia.model.CharacterSnapshot
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


class CharacterDetailsViewModel(
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

    companion object {

        const val PARAM_CHARACTER_ID = "param.character.id"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {

                val app = (this[APPLICATION_KEY] as RickMortyEncyclopediaApp)
                val characterRepository = app.appContainer.characterRepository

                val id = checkNotNull(this[DEFAULT_ARGS_KEY]).getInt(PARAM_CHARACTER_ID)
                CharacterDetailsViewModel(id, characterRepository)
            }
        }
    }
}