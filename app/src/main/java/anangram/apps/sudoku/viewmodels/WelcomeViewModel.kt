package anangram.apps.sudoku.viewmodels

import anangram.apps.sudoku.base.UseCase
import anangram.apps.sudoku.models.PuzzleDifficulty
import anangram.apps.sudoku.ui.theme.ThemeRepository
import anangram.apps.sudoku.usecases.GetInProgressPuzzleUseCase
import anangram.apps.sudoku.usecases.GetNewPuzzleByDifficultyUseCase
import anangram.apps.sudoku.usecases.PreloadPuzzlesUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface WelcomeUiState {
    data object Loading : WelcomeUiState
    data class UiContent(
        val difficulty: PuzzleDifficulty,
        val inProgressGameId: String?,
        val isThemeSelectorOpen: Boolean,
        val themeIndex: Int
    ) : WelcomeUiState

    data class Error(val message: String) : WelcomeUiState
}

sealed interface WelcomeUiAction {
    data class SetDifficulty(val difficulty: PuzzleDifficulty) : WelcomeUiAction
    data object StartNewGame : WelcomeUiAction
    data object ResumeGame : WelcomeUiAction
    data object GoToLeaderboards : WelcomeUiAction
    data object ToggleThemeSelector : WelcomeUiAction
    data class SetTheme(val index: Int) : WelcomeUiAction
}

sealed interface WelcomeNavigation {
    data class Game(val id: String) : WelcomeNavigation
    data object LeaderBoard : WelcomeNavigation
}

class WelcomeViewModel(
    val themeRepository: ThemeRepository,
    preloadPuzzlesUseCase: PreloadPuzzlesUseCase,
    val getInProgressPuzzleUseCase: GetInProgressPuzzleUseCase,
    val getNewPuzzleUseCase: GetNewPuzzleByDifficultyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<WelcomeUiState>(WelcomeUiState.Loading)
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<WelcomeNavigation>()
    val navigation = _navigation.asSharedFlow()

    init {
        viewModelScope.launch {
            preloadPuzzlesUseCase().unwrap(
                onSuccess = {
                    viewModelScope.launch {
                        val initDifficulty = PuzzleDifficulty.EASY
                        val inProgressGame =
                            getInProgressPuzzleUseCase(UseCase.Arg(initDifficulty)).contentOrNull()
                        val currentThemeIndex = themeRepository.themeIndex.first()
                        _state.emit(
                            WelcomeUiState.UiContent(
                                PuzzleDifficulty.EASY,
                                inProgressGame?.id,
                                false,
                                currentThemeIndex,
                            )
                        )
                    }

                },
                onFailure = {
                    _state.update { WelcomeUiState.Error("Failed to load puzzles from server!") }

                }
            )
        }
    }

    fun onAction(action: WelcomeUiAction) {
        when (action) {
            is WelcomeUiAction.SetDifficulty -> {
                viewModelScope.launch {
                    val hasInProgress =
                        getInProgressPuzzleUseCase(UseCase.Arg(action.difficulty)).contentOrNull()
                    val currentState = _state.value
                    if (currentState is WelcomeUiState.UiContent) {
                        _state.value = reduce(
                            content = currentState.copy(
                                difficulty = action.difficulty,
                                inProgressGameId = hasInProgress?.id
                            ),
                            action = action
                        )
                    }
                }
            }

            is WelcomeUiAction.SetTheme -> {
                viewModelScope.launch { themeRepository.setTheme(action.index) }
                val currentState = _state.value
                if (currentState is WelcomeUiState.UiContent) {
                    _state.value = reduce(currentState, action)
                }
            }

            is WelcomeUiAction.StartNewGame -> {
                val currentState = _state.value
                if (currentState is WelcomeUiState.UiContent) {
                    viewModelScope.launch {
                        val newGame =
                            getNewPuzzleUseCase(UseCase.Arg(currentState.difficulty)).contentOrNull()
                        newGame?.let {
                            _navigation.emit(WelcomeNavigation.Game(newGame.id))
                        } ?: run {
                            _state.emit(WelcomeUiState.Error("Unable to load new game"))
                        }

                    }
                }
            }

            WelcomeUiAction.ResumeGame -> viewModelScope.launch {
                val currentState = _state.value
                if (currentState is WelcomeUiState.UiContent) {
                    currentState.inProgressGameId?.let {
                        _navigation.emit(WelcomeNavigation.Game(it))
                    }
                }
            }

            WelcomeUiAction.GoToLeaderboards -> viewModelScope.launch {
                _navigation.emit(WelcomeNavigation.LeaderBoard)
            }

            else -> {
                val currentState = _state.value
                if (currentState is WelcomeUiState.UiContent) {
                    _state.value = reduce(currentState, action)
                }
            }
        }
    }


    private fun reduce(
        content: WelcomeUiState.UiContent,
        action: WelcomeUiAction
    ): WelcomeUiState {
        return when (action) {
            is WelcomeUiAction.SetDifficulty -> {
                content.copy(difficulty = action.difficulty)
            }

            WelcomeUiAction.ToggleThemeSelector -> content.copy(isThemeSelectorOpen = !content.isThemeSelectorOpen)

            is WelcomeUiAction.SetTheme -> {
                content.copy(themeIndex = action.index)
            }

            else -> content
        }
    }
}