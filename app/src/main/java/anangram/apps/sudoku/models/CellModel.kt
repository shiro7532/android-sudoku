package anangram.apps.sudoku.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CellModel(
    val initialValue: Value,
) {
    val isFixed: Boolean = initialValue != Value.UNASSIGNED

    private var _value: Value by mutableStateOf(initialValue)
    val value: Value
        get() = _value

    fun setValue(value: Value, isPencil: Boolean = false) {
        if (isFixed) return
        if (isPencil) {
            _value = Value.UNASSIGNED
            if (_pencilValues.contains(value)) {
                _pencilValues.remove(value)
            } else {
                _pencilValues.add(value)
            }
        } else {
            _value = value
            _pencilValues.clear()
            if (value != Value.UNASSIGNED) {
                _neighbor.forEach {
                    if (it.pencilValues.contains(value)) {
                        it._pencilValues.remove(value)
                    }
                    if (it.value == value) {
                        it._highlightState = HighlightState.ERROR
                        _highlightState = HighlightState.ERROR
                    }
                }
            }
        }
    }

    private val _pencilValues: MutableSet<Value> = mutableSetOf()
    val pencilValues: Set<Value>
        get() = _pencilValues

    private val _neighbor: MutableSet<CellModel> = mutableSetOf()
    val neighbours: Set<CellModel>
        get() = _neighbor

    fun addNeighbor(cell: CellModel) {
        _neighbor.add(cell)
    }

    private var _highlightState: HighlightState by mutableStateOf(HighlightState.IDLE)
    val highlightState: HighlightState
        get() = _highlightState

    fun highlightAsSelected() {
        _highlightState = HighlightState.SELECTED
        neighbours.forEach { it.highlightAsNeighbor() }
    }

    fun highlightAsNeighbor() {
        _highlightState = HighlightState.NEIGHBOUR
    }

    fun highlightAsSameValue() {
        _highlightState = HighlightState.SAME_VALUE
    }

    fun unHighlight(affectNeighbors: Boolean = true) {
        _highlightState = HighlightState.IDLE
        if (affectNeighbors)
            neighbours.forEach {
                it._highlightState = HighlightState.IDLE
            }
    }

}

