package anangram.apps.sudoku.viewmodels

import anangram.apps.sudoku.models.CellModel
import anangram.apps.sudoku.models.HighlightState
import anangram.apps.sudoku.models.SudokuModel
import anangram.apps.sudoku.models.Value
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SudokuViewModel : ViewModel() {

    val instance = SudokuModel(
        3, mapOf(
            1 to 4, 3 to 3, 80 to 1, 40 to 7, 8 to 7
        ), emptyMap()
    )

    private var _selectedCell: Int by mutableIntStateOf(-1)
    val selectedCell: Int
        get() = _selectedCell

    private var _selectedValue: Value? by mutableStateOf(null)
    val selectedValue: Value?
        get() = _selectedValue

    fun onCellClicked(position: Int) {
        val cell = instance.cellsMap[position]!!
        when (_selectedCell) {
            -1 -> selectNewCell(cell)
            position -> deselectCell(cell)
            else -> {
                deselectCell(cell)
                selectNewCell(cell)
            }
        }
    }

    private fun selectNewCell(cell: CellModel) {
        cell.highlight(state = HighlightState.SELECTED).let {
            _selectedCell = it.position
            instance.cellsMap[it.position] = it
            it.value.highlight()
        }
    }

    private fun deselectCell(cell: CellModel) {
        cell.highlight(state = HighlightState.IDLE).let {
            _selectedCell = it.position
            instance.cellsMap[cell.position] = it
            it.value.unHighlight()
        }
    }

    private fun Value.highlight() {
        instance.ouijas[this]?.cells?.forEach { position ->
            instance.cellsMap[position]?.takeUnless { it.position == _selectedCell }?.let {
                instance.cellsMap[position] = it.highlight(state = HighlightState.SAME_VALUE)
            }
        }
//        if (this == Value.UNASSIGNED) return
        _selectedValue = this
//        instance.ouijas[this.ordinal].cells.forEach {
//            if (it.isFixed || it != selectedCell)
//                it.highlightAsSameValue()
//        }
    }

    private fun Value.unHighlight() {
        instance.ouijas[this]?.cells?.forEach { position ->
            instance.cellsMap[position]?.takeUnless { it.position == _selectedCell }?.let {
                instance.cellsMap[position] = it.highlight(state = HighlightState.IDLE)
            }
        }
//        if (this == Value.UNASSIGNED) return
//        instance.ouijas[this.ordinal].cells.forEach {
//            if (it != selectedCell)
//                it.unHighlight(affectNeighbors = false)
//        }
        _selectedValue = null
    }

    fun onValueClicked(value: Value) {

        when {
            _selectedValue == null -> setValue(value)
            _selectedCell == -1 -> updateHighlightedValue(value)
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
        instance.cellsMap[selectedCell]?.takeUnless { it.isFixed }?.let {
            instance.cellsMap[selectedCell] = it.updateValue(value)
            instance.ouijas[value] = instance.ouijas[value]!!.addCell(it.position)
        }
        value.highlight()
    }

    fun deleteValue() {
        instance.cellsMap[selectedCell]?.takeUnless { it.value == Value.UNASSIGNED || it.isFixed }
            ?.let { cell ->
                instance.cellsMap[selectedCell] = cell.updateValue(Value.UNASSIGNED)
                selectedValue?.let {
                    instance.ouijas[it] = instance.ouijas[it]!!.removeCell(cell.position)

                }
                _selectedValue?.unHighlight()
                _selectedValue = null
            }
    }

    fun onDeleteClicked() {
        deleteValue()
    }
}