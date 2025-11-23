package anangram.apps.sudoku.viewmodels

import anangram.apps.sudoku.models.PuzzleDifficulty
import anangram.apps.sudoku.models.PuzzleModel
import anangram.apps.sudoku.usecases.GetCompletedGamesByDifficultyUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface LeaderboardUiState {
    data object Loading : LeaderboardUiState
    data class Content(
        val selectedDifficulty: PuzzleDifficulty,
        val items: List<Pair<Long, Int>>
    ) : LeaderboardUiState

    data class Error(val message: String) : LeaderboardUiState
}

sealed interface LeaderboardUiAction {
    data object ClickNavigateUp : LeaderboardUiAction
    data class SelectDifficulty(val difficulty: PuzzleDifficulty) : LeaderboardUiAction
}

sealed interface LeaderboardNavigation {
    data object NavigateUp : LeaderboardNavigation
}

class LeaderboardViewModel(
    getCompletedGamesByDifficultyUseCase: GetCompletedGamesByDifficultyUseCase
) : ViewModel(
) {
    private val _state = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<LeaderboardNavigation>()
    val navigation = _navigation.asSharedFlow()

    private lateinit var content: Map<PuzzleDifficulty, List<PuzzleModel>>

    init {
        viewModelScope.launch {
            getCompletedGamesByDifficultyUseCase().unwrap(
                onSuccess = {
                    content = it
                    _state.update {
                        LeaderboardUiState.Content(
                            PuzzleDifficulty.EASY,
                            getTimestampAndTimer(PuzzleDifficulty.EASY)
                        )
                    }
                }, onFailure = {

                }
            )

        }
    }

    fun onAction(action: LeaderboardUiAction) {
        when (action) {
            LeaderboardUiAction.ClickNavigateUp -> viewModelScope.launch {
                _navigation.emit(LeaderboardNavigation.NavigateUp)
            }

            is LeaderboardUiAction.SelectDifficulty -> {
                val currState = state.value
                if (currState is LeaderboardUiState.Content) {
                    _state.update {
                        currState.copy(
                            selectedDifficulty = action.difficulty,
                            items = getTimestampAndTimer(action.difficulty)
                        )
                    }
                }

            }
        }
    }

    private fun getTimestampAndTimer(difficulty: PuzzleDifficulty): List<Pair<Long, Int>> {
        return content[difficulty]
            .orEmpty()
            .mapNotNull { it.saveState?.let { state -> state.saveTimestamp to state.timeInSec } }
    }

}