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

    private var _selectedCell: CellModel? by mutableStateOf(null)
    val selectedCell: CellModel?
        get() = _selectedCell

    private var _selectedValue: Value? by mutableStateOf(null)
    val selectedValue: Value?
        get() = _selectedValue

    private var _isPencil by mutableStateOf(false)
    val isPencil: Boolean
        get() = _isPencil

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
        selectedCell?.takeUnless { isPencil }?.let { cell ->
            instance.ouijas.forEach {
                it.removeCell(cell)
            }
        }
        _selectedCell?.takeUnless { it.isFixed }?.let {
            it.setValue(value, isPencil)
            instance.ouijas[value.ordinal].apply {
                if (cells.contains(it)) removeCell(it) else addCell(it)
            }
        }
        if (!isPencil) value.highlight()
    }

    private fun deleteValue() {
        _selectedCell?.takeUnless { (it.state.value == Value.UNASSIGNED && it.pencilText.isEmpty()) || it.isFixed }
            ?.let {
                instance.ouijas.forEach { ouija ->
                    ouija.removeCell(it)
                }
                it.setValue(Value.UNASSIGNED)
                _selectedValue?.ordinal?.let { index ->
                    instance.ouijas[index].removeCell(it)
                }
                _selectedValue?.unHighlight()
                _selectedValue = null
            }
    }

    fun onPencilIconClicked() {
        _isPencil = !isPencil
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