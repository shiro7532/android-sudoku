package anangram.apps.sudoku.viewmodels

import anangram.apps.sudoku.base.UseCase
import anangram.apps.sudoku.models.CellSaveState
import anangram.apps.sudoku.models.Entry
import anangram.apps.sudoku.models.PuzzleModel
import anangram.apps.sudoku.models.PuzzleSaveState
import anangram.apps.sudoku.models.UndoRedoManager
import anangram.apps.sudoku.ui.theme.ThemeRepository
import anangram.apps.sudoku.usecases.GetPuzzleUseCase
import anangram.apps.sudoku.usecases.SaveGameStateUseCase
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SudokuUiState {
    data object Loading : SudokuUiState
    data class UiContent(
        val unitSize: Int,
        val remaining: Map<Entry, Int>,
        val selectedCell: Int? = null,
        val selectedEntry: Entry? = null,
        val pencilMode: Boolean = false,
        val undoAvailable: Boolean = false,
        val redoAvailable: Boolean = false,
        val time: Int = 0,
        val showResetDialog: Boolean = false,
        val themeIndex: Int = 0,
        val isThemeSelectorOpen: Boolean = false
    ) : SudokuUiState

    data class Error(val message: String) : SudokuUiState

}

sealed interface SudokuUiAction {
    data class CellClicked(val position: Int) : SudokuUiAction
    data class ValueClicked(val entry: Entry) : SudokuUiAction
    object ClickDelete : SudokuUiAction
    object ClickUndo : SudokuUiAction
    object ClickRedo : SudokuUiAction
    object TogglePencil : SudokuUiAction
    object RequestReset : SudokuUiAction
    object DismissResetRequest : SudokuUiAction
    object ConfirmResetRequest : SudokuUiAction
    object ClickNavigateBack : SudokuUiAction
    object ToggleThemeSelector : SudokuUiAction
    data class SelectTheme(val index: Int) : SudokuUiAction
}

sealed interface SudokuNavigation {
    data object Won : SudokuNavigation
    data object NavigateUp : SudokuNavigation
}

class SudokuViewModel(
    val themeRepository: ThemeRepository,
    savedStateHandle: SavedStateHandle,
    getPuzzleUseCase: GetPuzzleUseCase,
    val saveGameStateUseCase: SaveGameStateUseCase
) : ViewModel(), LifecycleEventObserver {

    lateinit var instance: PuzzleModel
    private var gameOver = false

    init {
        val puzzleId: String = savedStateHandle.get<String>("puzzleId") ?: error("puzzleId missing")
        viewModelScope.launch {
            getPuzzleUseCase(UseCase.Arg(puzzleId)).unwrap(
                onSuccess = {
                    instance = it
                    _state.update {
                        SudokuUiState.UiContent(
                            instance.unitSize,
                            computeRemaining(),
                            time = instance.saveState?.timeInSec ?: 0
                        )
                    }
                },
                onFailure = {
                    _state.update { SudokuUiState.Error("") }
                }
            )
        }

    }


    private val _state = MutableStateFlow<SudokuUiState>(SudokuUiState.Loading)
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<SudokuNavigation>()
    val navigation = _navigation.asSharedFlow()


    private val undoRedoManager = UndoRedoManager()

    private fun reduce(reducer: (SudokuUiState.UiContent) -> SudokuUiState.UiContent) {
        val currState = state.value
        if (currState is SudokuUiState.UiContent)
            _state.value = reducer(currState)
    }

    fun onAction(action: SudokuUiAction) {
        when (action) {
            is SudokuUiAction.CellClicked -> onCellClicked(action.position)
            is SudokuUiAction.ValueClicked -> onValueClicked(action.entry)
            SudokuUiAction.ClickDelete -> onDeleteClicked()
            SudokuUiAction.ClickUndo -> undo()
            SudokuUiAction.ClickRedo -> redo()
            SudokuUiAction.TogglePencil -> togglePencil()
            SudokuUiAction.RequestReset -> showResetDialog()
            SudokuUiAction.DismissResetRequest -> clearResetDialog()
            SudokuUiAction.ConfirmResetRequest -> resetPuzzle()
            is SudokuUiAction.SelectTheme -> selectTheme(action.index)
            SudokuUiAction.ToggleThemeSelector -> toggleThemeSelector()
            SudokuUiAction.ClickNavigateBack -> saveStateAndNavigateUp()
        }
    }

    private fun onContent(content: SudokuUiState.UiContent.() -> Unit) {
        val currState = state.value
        if (currState is SudokuUiState.UiContent) {
            with(currState) {
                content()
            }
        }
    }

    private fun onCellClicked(position: Int) = onContent {
        viewModelScope.launch {
            val prev = selectedCell
            val newPos = if (prev == position) null else position

            reduce { it.copy(selectedCell = newPos) }

            // update highlight via model
            if (prev != null) {
                instance.cells[prev].unHighlight()
                val prevEntry = instance.cells[prev].state.value.entry
                if (prevEntry != Entry.UNASSIGNED) {
                    instance.getSameEntryCells(prevEntry).forEach {
                        it.unHighlight()
                    }
                }
            }

            newPos?.let { newPosition ->
                instance.cells[newPosition].highlightAsSelected()
                val newEntry = instance.cells[newPosition].state.value.entry
                if (newEntry != Entry.UNASSIGNED) {
                    instance.getSameEntryCells(newEntry).forEach {
                        it.highlightAsSameValue()
                    }
                }
            }

            // update selectedEntry based on cell content
            val entry = newPos?.let { instance.cells[it].state.value.entry }
            reduce { it.copy(selectedEntry = entry.takeIf { e -> e != Entry.UNASSIGNED }) }
        }
    }

    private fun onValueClicked(entry: Entry) = onContent {
        viewModelScope.launch {
            val pos = selectedCell ?: return@launch
            val isPencil = pencilMode

            val (before, after) = instance.handleEvent(
                BoardUpdateEvent.Set(entry, pos, isPencil)
            )

            before.entry.takeIf { it != Entry.UNASSIGNED }?.let { prevEntry ->
                instance.getSameEntryCells(prevEntry).forEach { it.unHighlight() }
            }
            after.entry.takeIf { it != Entry.UNASSIGNED }?.let { newEntry ->
                instance.getSameEntryCells(newEntry).forEach { it.highlightAsSameValue() }
            }

            undoRedoManager.registerMove(
                UndoRedoManager.Move.Set(pos, isPencil, before, after)
            )

            reduce {
                it.copy(
                    selectedEntry = if (!isPencil) entry else
                        entry.takeIf { instance.cells[pos].hasPencil(entry) },
                    undoAvailable = undoRedoManager.undoAvailable,
                    redoAvailable = undoRedoManager.redoAvailable,
                    remaining = computeRemaining()

                )
            }
        }
    }

    private fun onDeleteClicked() = onContent {
        viewModelScope.launch {
            val pos = selectedCell ?: return@launch
            val isPencil = pencilMode

            val (before, after) = instance.handleEvent(
                BoardUpdateEvent.Delete(pos, isPencil)
            )

            before.entry.takeIf { it != Entry.UNASSIGNED }?.let { prevEntry ->
                instance.getSameEntryCells(prevEntry).forEach { it.unHighlight() }
            }
            after.entry.takeIf { it != Entry.UNASSIGNED }?.let { newEntry ->
                instance.getSameEntryCells(newEntry).forEach { it.highlightAsSameValue() }
            }
            undoRedoManager.registerMove(
                UndoRedoManager.Move.Clear(pos, isPencil, before, after)
            )

            reduce {
                it.copy(
                    selectedEntry = null,
                    undoAvailable = undoRedoManager.undoAvailable,
                    redoAvailable = undoRedoManager.redoAvailable,
                    remaining = computeRemaining()
                )
            }
        }
    }

    private fun undo() = viewModelScope.launch {
        val (pos, state) = undoRedoManager.undo()
        instance.setEntryState(pos, state)

        // Highlight cells & update selectedEntry
        forceSelectCell(pos)   // <-- use this

        // Update remaining, undo/redo, etc
        reduce {
            it.copy(
                undoAvailable = undoRedoManager.undoAvailable,
                redoAvailable = undoRedoManager.redoAvailable,
                remaining = computeRemaining()
            )
        }
    }

    private fun redo() = viewModelScope.launch {
        val (pos, state) = undoRedoManager.redo()
        instance.setEntryState(pos, state)

        forceSelectCell(pos)   // <-- use this

        reduce {
            it.copy(
                undoAvailable = undoRedoManager.undoAvailable,
                redoAvailable = undoRedoManager.redoAvailable,
                remaining = computeRemaining()
            )
        }
    }

    private fun forceSelectCell(pos: Int) = onContent {
        viewModelScope.launch {
            val prev = selectedCell
            if (prev != null && prev != pos) {
                instance.cells[prev].unHighlight()
            }

            instance.cells[pos].highlightAsSelected()

            val entry = instance.cells[pos].state.value.entry
            reduce {
                it.copy(
                    selectedCell = pos,
                    selectedEntry = entry.takeIf { e -> e != Entry.UNASSIGNED }
                )
            }
        }
    }

    private fun togglePencil() {
        reduce { it.copy(pencilMode = !it.pencilMode) }
    }

    private fun computeRemaining(): Map<Entry, Int> {
        val max = instance.sideSize
        val remaining = Entry.entries
            .filter { it != Entry.UNASSIGNED }
            .associateWith { entry ->
                max - instance.getSameEntryCells(entry).size
            }
        if (remaining.values.all { it == 0 }) {
            val errorCells = buildSet {
                instance.output.forEach { (position, value) ->
                    val expected = Entry.entries[value]
                    val actual = instance.cells[position].state.value.entry
                    if (expected != actual)
                        add(instance.cells[position])
                }
            }
            viewModelScope.launch {

                if (errorCells.isEmpty()) {
                    gameOver = true
                    saveGameState()
                    _navigation.emit(SudokuNavigation.Won)

                } else {
                    errorCells.forEach {
                        it.highlightAsError(true)
                    }
                }
            }
        }
        return remaining
    }

    private fun showResetDialog() {
        reduce { it.copy(showResetDialog = true) }
    }

    private fun clearResetDialog() {
        reduce { it.copy(showResetDialog = false) }
    }

    private fun resetPuzzle() = onContent {
        viewModelScope.launch {

            // 1. Clear selection & highlights
            selectedCell?.let { pos ->
                instance.cells[pos].unHighlight()
            }

            // 2. Clear all cell state in the model
            instance.cells.forEach { cell ->
                cell.clearContent()
            }

            // 3. Clear undo/redo stacks
            undoRedoManager.clear()

            // 4. Reset UiState
            reduce {
                it.copy(
                    selectedCell = null,
                    selectedEntry = null,
                    pencilMode = false,
                    undoAvailable = false,
                    redoAvailable = false,
                    remaining = computeRemaining(),
                    showResetDialog = false
                )
            }
        }
    }

    private fun toggleThemeSelector() {
        reduce { it.copy(isThemeSelectorOpen = !it.isThemeSelectorOpen) }
    }

    private fun saveStateAndNavigateUp() {
        viewModelScope.launch {
            saveGameState()
            _navigation.emit(SudokuNavigation.NavigateUp)
        }
    }

    private fun selectTheme(index: Int) {
        viewModelScope.launch {
            themeRepository.setTheme(index)
        }
        reduce {
            it.copy(
                themeIndex = index,
            )
        }
    }

    private suspend fun saveGameState() {
        val currState = state.value
        if (currState is SudokuUiState.UiContent) {
            val time = currState.time
            val cellSaveStates =
                instance.cells.mapIndexed { index, cell ->
                    CellSaveState(
                        index,
                        cell.state.value.entry,
                        cell.getEncodedPencilValue()
                    )
                }
            val puzzleSaveState = PuzzleSaveState(
                gameId = instance.id,
                isInProgress = !gameOver,
                timeInSec = time,
                saveTimestamp = System.currentTimeMillis(),
                cellSaveStates = cellSaveStates
            )
            saveGameStateUseCase(UseCase.Arg(puzzleSaveState)).unwrap(
                onSuccess = {
                    Log.d(
                        "saveGameStateUseCase",
                        "Game ${instance.id} saved successfully"
                    )
                },
                onFailure = {
                    Log.d(
                        "saveGameStateUseCase",
                        "Game ${instance.id} save failed",
                        it
                    )
                },
            )

        }
    }

    private var timerJob: Job? = null
    private var isRunning = false

    fun startTimer() {
        if (!isRunning) {
            isRunning = true
            timerJob = viewModelScope.launch(Dispatchers.Main) {
                while (isRunning) {
                    delay(1000)
                    val currState = state.value
                    if (currState is SudokuUiState.UiContent)
                        _state.update { currState.copy(time = currState.time + 1) }
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        isRunning = false
    }


    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event
    ) {
        if (!gameOver) {
            when (event) {
                Lifecycle.Event.ON_START -> {
                    Log.d("TimerViewModel", "App entering foreground")
                    startTimer()
                }

                Lifecycle.Event.ON_STOP -> {
                    Log.d("TimerViewModel", "App entering background")
                    pauseTimer()
                    viewModelScope.launch {
                        saveGameState()
                    }
                }

                else -> {}
            }
        }
    }
}

sealed class BoardUpdateEvent(open val position: Int, open val inPencil: Boolean) {
    data class Set(val entry: Entry, override val position: Int, override val inPencil: Boolean) :
        BoardUpdateEvent(position, inPencil)

    data class Delete(override val position: Int, override val inPencil: Boolean) :
        BoardUpdateEvent(position, inPencil)
}