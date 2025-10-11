package anangram.apps.sudoku.viewmodels

import anangram.apps.sudoku.models.Entry
import anangram.apps.sudoku.models.SudokuModel
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SudokuViewModel : ViewModel(), LifecycleEventObserver {

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

    private val selectedPosition = MutableSharedFlow<Int?>(extraBufferCapacity = 10)
    private var prevPosition: Int? = null

    private val _selectedEntry = MutableSharedFlow<Entry?>(extraBufferCapacity = 10)
    val selectedEntry = _selectedEntry.asSharedFlow()
    private var prevEntry: Entry? = null

    private var _isPencil by mutableStateOf(false)
    val isPencil: Boolean
        get() = _isPencil

    private val boardUpdate = MutableSharedFlow<BoardUpdateEvent>()

    fun onCellClicked(position: Int) = viewModelScope.launch {
        selectedPosition.emit(if (position == prevPosition) null else position)
    }

    fun onValueClicked(entry: Entry) = viewModelScope.launch {
        prevPosition?.let { position ->
            val currentCell = instance.cells[position]
            if (currentCell.isFixed) return@launch
            val currentCellEntry = currentCell.state.value.entry
            if (currentCellEntry == entry) return@launch
            val update = when {
                isPencil -> BoardUpdateEvent.MarkPencil(entry, position)
                currentCellEntry == Entry.UNASSIGNED -> BoardUpdateEvent.Set(entry, position)
                else -> BoardUpdateEvent.Replace(currentCellEntry, entry, position)
            }
            boardUpdate.emit(update)
        }
    }

    fun onDeleteClicked() = viewModelScope.launch {
        prevPosition?.let { position ->
            val currentCell = instance.cells[position]
            val currentCellEntry = currentCell.state.value.entry
            boardUpdate.emit(BoardUpdateEvent.Delete(currentCellEntry, position))
        }
    }

    fun onPencilIconClicked() {
        _isPencil = !isPencil
    }

    init {
        viewModelScope.launch {
            selectedPosition.distinctUntilChanged().collect { curr ->
                prevPosition?.let { clearSelection(it) }
                curr?.let { setSelection(it) }
                prevPosition = curr
            }
        }
        viewModelScope.launch {
            _selectedEntry.distinctUntilChanged().collect { curr ->
                prevEntry?.let { unHighlightEntry(it) }
                curr?.let { highlightEntry(it) }
                prevEntry = curr
            }
        }
        viewModelScope.launch {
            boardUpdate.collect { event ->
                when (event) {
                    is BoardUpdateEvent.Set -> {
                        instance.setEntry(event.entry, event.position)
                        _selectedEntry.emit(event.entry)
                    }

                    is BoardUpdateEvent.Delete -> {
                        instance.clearContent(event.position)
                        _selectedEntry.emit(null)
                    }

                    is BoardUpdateEvent.Replace -> {
                        instance.replaceEntry(event.new, event.position)
                        _selectedEntry.emit(event.new)
                    }

                    is BoardUpdateEvent.MarkPencil -> {
                        instance.markPencil(event.entry, event.position).let {
                            _selectedEntry.emit(if (it) event.entry else null)
                        }
                    }
                }
            }
        }
    }

    private suspend fun clearSelection(position: Int) {
        val cell = instance.cells[position]
        cell.unHighlight()
        cell.state.value.entry.takeIf { it != Entry.UNASSIGNED }?.let { entry ->
            _selectedEntry.emit(null)
        }
    }

    private suspend fun setSelection(position: Int) {
        val cell = instance.cells[position]
        cell.highlightAsSelected()
        cell.state.value.entry.takeIf { it != Entry.UNASSIGNED }?.let { entry ->
            _selectedEntry.emit(entry)
        }
    }

    private suspend fun unHighlightEntry(entry: Entry) {
        if (entry == Entry.UNASSIGNED) return
        instance.getSameEntryCells(entry).forEach {
            if (prevPosition == null || instance.cells[prevPosition!!] != it)
                it.unHighlight(affectNeighbors = false)
        }
    }

    private suspend fun highlightEntry(entry: Entry) {
        if (entry == Entry.UNASSIGNED) return
        instance.getSameEntryCells(entry).forEach {
            if (prevPosition == null || instance.cells[prevPosition!!] != it)
                it.highlightAsSameValue()
        }
    }

    private val time = MutableStateFlow(0)
    val timeFlow = time.asStateFlow()
    private var timerJob: Job? = null
    private var isRunning = false

    fun startTimer() {
        if (!isRunning) {
            isRunning = true
            timerJob = viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    while (isRunning) {
                        delay(1000) // Wait for 1 second
                        time.emit(time.value + 1)
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        isRunning = false
    }

    fun resetTimer() {
        viewModelScope.launch {
            pauseTimer()
            time.emit(0)
        }
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

sealed class BoardUpdateEvent(open val position: Int) {
    data class Set(val entry: Entry, override val position: Int) : BoardUpdateEvent(position)
    data class Replace(val old: Entry, val new: Entry, override val position: Int) :
        BoardUpdateEvent(position)

    data class Delete(val entry: Entry, override val position: Int) : BoardUpdateEvent(position)
    data class MarkPencil(val entry: Entry, override val position: Int) : BoardUpdateEvent(position)
}