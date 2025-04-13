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
        9, mapOf(
            1 to 4, 3 to 3, 80 to 1, 40 to 7, 8 to 7
        ), emptyMap()
    )

    private var _selectedCell: CellModel? by mutableStateOf(null)
    private var _selectedValue: Value? by mutableStateOf(null)
    private val _valueCellMap = Value.entries.filter { it != Value.UNASSIGNED }
        .associateWith { key -> instance.cells.filter { it.value == key }.toMutableSet() }
        .toMutableMap()

//    val countMap: Map<Value, Int> = _valueCellMap.mapValues { it.value.size }

    fun onCellClicked(cell: CellModel) {
        when {
            _selectedCell == null -> selectNewCell(cell)
            _selectedCell == cell -> deselectCell(cell)
            else -> selectDifferentCell(cell)
        }
    }

    private fun selectNewCell(cell: CellModel) {
        _selectedValue?.unHighlight()
        cell.highlightAsSelected()
        cell.value.highlight()
        _selectedCell = cell
    }

    private fun deselectCell(cell: CellModel) {
        cell.unHighlight()
        cell.value.unHighlight()
        _selectedCell = null
    }

    private fun selectDifferentCell(cell: CellModel) {
        _selectedCell?.unHighlight()
        if (cell.value != _selectedCell?.value) {
            _selectedCell?.value?.unHighlight()
            cell.value.highlight()
        }
        cell.highlightAsSelected()
        _selectedCell = cell
    }

    private fun Value.highlight() {
        if (this == Value.UNASSIGNED) return
        _selectedValue = this
        _valueCellMap[this]?.forEach {
            it.highlightAsSameValue()
        }
    }

    private fun Value.unHighlight() {
        if (this == Value.UNASSIGNED) return
        _valueCellMap[this]?.forEach {
            it.unHighlight()
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
        value.highlight()
        _selectedCell?.takeUnless { it.isFixed }?.let {
            it.setValue(value)
            _valueCellMap[_selectedValue]?.add(it)
        }
    }

    private fun deleteValue() {
        _selectedCell?.takeUnless { it.value == Value.UNASSIGNED || it.isFixed }?.let {
            it.setValue(Value.UNASSIGNED)
            _valueCellMap[_selectedValue]?.remove(it)
            _selectedValue?.unHighlight()
        }
    }
}