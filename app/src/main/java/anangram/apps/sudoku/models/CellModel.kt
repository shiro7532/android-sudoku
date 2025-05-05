package anangram.apps.sudoku.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CellModel(
    val unitSize: Int,
    val initialValue: Value,
) {

    data class State(
        val value: Value,
        val highlightState: HighlightState,
        val pencilText: String,
    )

    var state: State by mutableStateOf(State(initialValue, HighlightState.IDLE, ""))
    val isFixed: Boolean = initialValue != Value.UNASSIGNED
    private val pencilValues = BooleanArray(unitSize * unitSize) { false }
    val pencilText: String
        get() = buildString {
            if (pencilValues.all { !it }) return@buildString
            pencilValues.forEachIndexed { index, b ->
                if (index % unitSize == 0 && index != 0) appendLine() else if (index != 0) append(
                    ' '
                )
                if (b) append(index + 1) else append(' ')
            }
        }


    fun setValue(value: Value, isPencil: Boolean = false) {
        if (isFixed) return
        // When delete is pressed, reset pencil values
        if (value == Value.UNASSIGNED) {
            state = state.copy(value = value, pencilText = "")
            pencilValues.reset()
            return
        }
        if (isPencil) {
            togglePencilValue(value)
            state = state.copy(value = Value.UNASSIGNED, pencilText = pencilText)
        } else {
            pencilValues.reset()
            state = state.copy(value = value, pencilText = pencilText)
            neighbors.forEach {
                if (it.pencilValues[value.ordinal - 1]) {
                    it.togglePencilValue(value)
                    it.state = it.state.copy(pencilText = it.pencilText)
                }
            }
        }
    }

    private fun togglePencilValue(value: Value) {
        pencilValues[value.ordinal - 1] = !pencilValues[value.ordinal - 1]
    }

    private fun BooleanArray.reset() {
        forEachIndexed { index, _ ->
            this[index] = false
        }
    }

    private lateinit var neighbors: Array<CellModel>

    fun setNeighbors(neighbors: Array<CellModel>) {
        if (this::neighbors.isInitialized) return
        this.neighbors = neighbors
    }

    fun highlightAsSelected() {
        state = state.copy(highlightState = HighlightState.SELECTED)
        neighbors.forEach {
            it.highlightAsNeighbor()
        }
    }

    fun highlightAsNeighbor() {
        state = state.copy(highlightState = HighlightState.NEIGHBOUR)
    }

    fun highlightAsSameValue() {
        state = state.copy(highlightState = HighlightState.SAME_VALUE)
    }

    fun unHighlight(affectNeighbors: Boolean = true) {
        state = state.copy(highlightState = HighlightState.IDLE)
        if (affectNeighbors)
            neighbors.forEach {
                it.unHighlight(affectNeighbors = false)
            }
    }
}

