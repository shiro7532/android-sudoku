package anangram.apps.sudoku.viewmodels

import anangram.apps.sudoku.models.CellModel
import anangram.apps.sudoku.models.SudokuModel
import anangram.apps.sudoku.models.Value
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SudokuViewModel : ViewModel() {

    val instance = SudokuModel(
        3, mapOf(
            1 to 4, 3 to 3, 80 to 1, 40 to 7, 8 to 7
        ), emptyMap()
    )

    private var _selectedCell: CellModel? by mutableStateOf(null)
    val selectedCell: CellModel?
        get() = _selectedCell

    private var _selectedValue: Value? by mutableStateOf(null)
    val selectedValue: Value?
        get() = _selectedValue

//    private val _valueCellMap = Value.entries.filter { it != Value.UNASSIGNED }
//        .associateWith { key -> instance.cells.filter { it.value == key }.toMutableSet() }
//        .toMutableMap()

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
}