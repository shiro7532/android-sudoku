package anangram.apps.sudoku.viewmodels

import anangram.apps.sudoku.models.Entry
import anangram.apps.sudoku.models.SudokuModel
import anangram.apps.sudoku.models.UndoRedoManager
import anangram.apps.sudoku.ui.theme.ThemeRepository
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SudokuUiState(
    val sideSize: Int,
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
)

sealed interface SudokuUiAction {
    data class CellClicked(val position: Int) : SudokuUiAction
    data class ValueClicked(val entry: Entry) : SudokuUiAction
    object DeleteClicked : SudokuUiAction
    object UndoClicked : SudokuUiAction
    object RedoClicked : SudokuUiAction
    object PencilToggle : SudokuUiAction
    object ResetRequested : SudokuUiAction
    object ResetRequestDismissed : SudokuUiAction
    object ResetRequestConfirmed : SudokuUiAction
    object ToggleThemeSelector : SudokuUiAction
    data class ThemeSelected(val index: Int) : SudokuUiAction
}

class SudokuViewModel(
    val themeRepository: ThemeRepository
) : ViewModel(), LifecycleEventObserver {

    val instance = SudokuModel(
        3, mapOf(
            0 to 5,
            1 to 3,
            4 to 7,
            9 to 6,
            12 to 1,
            13 to 9,
            14 to 5,
            19 to 9,
            20 to 8,
            25 to 6,
            27 to 8,
            31 to 6,
            35 to 3,
            36 to 4,
            39 to 8,
            41 to 3,
            44 to 1,
            45 to 7,
            49 to 2,
            53 to 6,
            55 to 6,
            60 to 2,
            61 to 8,
            66 to 4,
            67 to 1,
            68 to 9,
            71 to 5,
            76 to 8,
            79 to 7,
            80 to 9
        ), emptyMap()
    )

    private val _uiState = MutableStateFlow(SudokuUiState(instance.sideSize, computeRemaining()))
    val uiState = _uiState.asStateFlow()

    private val undoRedoManager = UndoRedoManager()

    private fun reduce(reducer: (SudokuUiState) -> SudokuUiState) {
        _uiState.value = reducer(_uiState.value)
    }

    fun dispatch(action: SudokuUiAction) {
        when (action) {
            is SudokuUiAction.CellClicked -> onCellClicked(action.position)
            is SudokuUiAction.ValueClicked -> onValueClicked(action.entry)
            SudokuUiAction.DeleteClicked -> onDeleteClicked()
            SudokuUiAction.UndoClicked -> undo()
            SudokuUiAction.RedoClicked -> redo()
            SudokuUiAction.PencilToggle -> togglePencil()
            SudokuUiAction.ResetRequested -> showResetDialog()
            SudokuUiAction.ResetRequestDismissed -> clearResetDialog()
            SudokuUiAction.ResetRequestConfirmed -> resetPuzzle()
            is SudokuUiAction.ThemeSelected -> selectTheme(action.index)
            SudokuUiAction.ToggleThemeSelector -> toggleThemeSelector()
        }
    }

    private fun onCellClicked(position: Int) = viewModelScope.launch {
        val prev = uiState.value.selectedCell
        val newPos = if (prev == position) null else position

        reduce { it.copy(selectedCell = newPos) }

        // update highlight via model
        if (prev != null) instance.cells[prev].unHighlight()
        newPos?.let { instance.cells[it].highlightAsSelected() }

        // update selectedEntry based on cell content
        val entry = newPos?.let { instance.cells[it].state.value.entry }
        reduce { it.copy(selectedEntry = entry.takeIf { e -> e != Entry.UNASSIGNED }) }
    }

    private fun onValueClicked(entry: Entry) = viewModelScope.launch {
        val pos = uiState.value.selectedCell ?: return@launch
        val isPencil = uiState.value.pencilMode

        val (before, after) = instance.handleEvent(
            BoardUpdateEvent.Set(entry, pos, isPencil)
        )

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

    private fun onDeleteClicked() = viewModelScope.launch {
        val pos = uiState.value.selectedCell ?: return@launch
        val isPencil = uiState.value.pencilMode

        val (before, after) = instance.handleEvent(
            BoardUpdateEvent.Delete(pos, isPencil)
        )
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

    private fun forceSelectCell(pos: Int) = viewModelScope.launch {
        val prev = uiState.value.selectedCell
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

    private fun togglePencil() {
        reduce { it.copy(pencilMode = !it.pencilMode) }
    }

    private fun computeRemaining(): Map<Entry, Int> {
        val max = instance.sideSize
        return Entry.entries
            .filter { it != Entry.UNASSIGNED }
            .associateWith { entry ->
                max - instance.getSameEntryCells(entry).size
            }
    }

    private fun showResetDialog() {
        reduce { it.copy(showResetDialog = true) }
    }

    private fun clearResetDialog() {
        reduce { it.copy(showResetDialog = false) }
    }

    private fun resetPuzzle() = viewModelScope.launch {

        // 1. Clear selection & highlights
        uiState.value.selectedCell?.let { pos ->
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

    private fun toggleThemeSelector() {
        reduce { it.copy(isThemeSelectorOpen = !it.isThemeSelectorOpen) }
    }

    private fun selectTheme(index: Int) {
        viewModelScope.launch {
            themeRepository.setTheme(index)
        }
        reduce {
            it.copy(
                themeIndex = index,
//                isThemeSelectorOpen = false
            )
        }

        // Save to datastore if needed...
    }

    private var timerJob: Job? = null
    private var isRunning = false

    fun startTimer() {
        if (!isRunning) {
            isRunning = true
            timerJob = viewModelScope.launch(Dispatchers.Main) {
                while (isRunning) {
                    delay(1000)
                    _uiState.update { it.copy(time = it.time + 1) }
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
        when (event) {
            Lifecycle.Event.ON_START -> {
                Log.d("TimerViewModel", "App entering foreground")
                startTimer()
            }

            Lifecycle.Event.ON_STOP -> {
                Log.d("TimerViewModel", "App entering background")
                pauseTimer()
            }

            else -> {}
        }
    }
}

sealed class BoardUpdateEvent(open val position: Int, open val inPencil: Boolean) {
    data class Set(val entry: Entry, override val position: Int, override val inPencil: Boolean) :
        BoardUpdateEvent(position, inPencil)

    data class Delete(override val position: Int, override val inPencil: Boolean) :
        BoardUpdateEvent(position, inPencil)
}