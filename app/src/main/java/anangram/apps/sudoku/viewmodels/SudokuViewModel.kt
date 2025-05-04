package anangram.apps.sudoku.viewmodels

import anangram.apps.sudoku.models.CellModel
import anangram.apps.sudoku.models.SudokuModel
import anangram.apps.sudoku.models.Value
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SudokuViewModel : ViewModel(), LifecycleEventObserver {

    val instance = SudokuModel(
        3, mapOf(
            0 to 4,
            8 to 5,
            9 to 3,
            18 to 7,
            26 to 2,
            32 to 6,
            36 to 8,
            43 to 4,
            45 to 1,
            54 to 6,
            56 to 3,
            58 to 7,
            60 to 5,
            63 to 2,
            70 to 1,
            80 to 4,
        ), emptyMap()
    )

    private var _selectedCell: CellModel? by mutableStateOf(null)
    val selectedCell: CellModel?
        get() = _selectedCell

    private var _selectedValue: Value? by mutableStateOf(null)
    val selectedValue: Value?
        get() = _selectedValue

    fun onCellClicked(position: Int) {
        val cell = instance.cells[position]
        when {
            _selectedCell == null -> selectNewCell(cell)
            _selectedCell == cell -> deselectCell(cell)
            else -> {
                _selectedCell?.let { deselectCell(it) }
                selectNewCell(cell)
            }
        }
    }

    private fun selectNewCell(cell: CellModel) {
        _selectedValue?.unHighlight()
        cell.highlightAsSelected()
        _selectedCell = cell
        cell.state.value.highlight()
    }

    private fun deselectCell(cell: CellModel) {
        cell.unHighlight()
        cell.state.value.unHighlight()
        _selectedCell = null
    }

    private fun Value.highlight() {
        if (this == Value.UNASSIGNED) return
        _selectedValue = this
        instance.ouijas[this.ordinal].cells.forEach {
            if (it.isFixed || it != selectedCell)
                it.highlightAsSameValue()
        }
    }

    private fun Value.unHighlight() {
        if (this == Value.UNASSIGNED) return
        instance.ouijas[this.ordinal].cells.forEach {
            if (it != selectedCell)
                it.unHighlight(affectNeighbors = false)
        }
        _selectedValue = null
    }

    fun onValueClicked(value: Value) {

        when {
            value == Value.UNASSIGNED -> deleteValue()
            _selectedValue == null -> setValue(value)
            _selectedCell == null -> updateHighlightedValue(value)
            _selectedValue != value -> {
                deleteValue()
                setValue(value)
            }
        }
    }

    private fun updateHighlightedValue(value: Value) {
        val prevValue = _selectedValue
        _selectedValue?.unHighlight()
        if (value != prevValue) {
            value.highlight()
        }
    }

    private fun setValue(value: Value) {
        _selectedCell?.takeUnless { it.isFixed }?.let {
            it.setValue(value)
            instance.ouijas[value.ordinal].addCell(it)
        }
        value.highlight()
    }

    private fun deleteValue() {
        _selectedCell?.takeUnless { it.state.value == Value.UNASSIGNED || it.isFixed }?.let {
            it.setValue(Value.UNASSIGNED)
            _selectedValue?.ordinal?.let { index ->
                instance.ouijas[index].removeCell(it)
            }
            _selectedValue?.unHighlight()
            _selectedValue = null
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
                        Log.d("TimerViewModel", "seconds updated : $time")
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