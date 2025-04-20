package anangram.apps.sudoku.models


data class CellModel(
    val position: Int,
    val unitSize: Int,
    val isFixed: Boolean,
    val value: Value,
    val pencilValues: Set<Value> = emptySet(),
    val highlightState: HighlightState = HighlightState.IDLE,
) {

    val neighbor: IntArray = IntArray((unitSize - 1) * (3 * unitSize + 1))

    init {
        val sideSize = unitSize * unitSize
        val size = sideSize * sideSize
        var index = 0
        val row = position / sideSize

        for (i in row * sideSize until (row + 1) * sideSize) {
            if (i != position) {
                neighbor[index] = i
                index++
            }
        }
        println(neighbor.contentToString())
        println(index)
        val column = position % sideSize
        for (i in column until size step sideSize) {
            if (i != position) {
                neighbor[index] = i
                index++
            }
        }
        val boxStartRow = (row / unitSize) * unitSize
        val boxStartCol = (column / unitSize) * unitSize
        for (i in boxStartRow until boxStartRow + unitSize) {
            for (j in boxStartCol until boxStartCol + unitSize) {
                if (i != row && j != column) {
                    neighbor[index++] = i * sideSize + j
                }
            }
        }
        println(index)

    }


    fun updateValue(value: Value, isPencil: Boolean = false): CellModel {
        if (isFixed) return this
        var newValue: Value
        var newPencilValues: Set<Value>
        if (isPencil) {
            newValue = Value.UNASSIGNED
            newPencilValues = if (pencilValues.contains(value)) {
                pencilValues.minus(value)
            } else {
                pencilValues.plus(value)
            }
        } else {
            newValue = value
            newPencilValues = emptySet()
            // TODO: Handle removing pencil values
//            if (value != Value.UNASSIGNED) {
//                _neighbor.forEach {
//                    if (it.pencilValues.contains(value)) {
//                        it._pencilValues.remove(value)
//                    }
//                    if (it.value == value) {
//                        it._highlightState = HighlightState.ERROR
//                        _highlightState = HighlightState.ERROR
//                    }
//                }
//            }
        }
        return this.copy(value = newValue, pencilValues = newPencilValues)
    }

    fun highlight(state: HighlightState): CellModel {
        return this.copy(highlightState = state)
    }

}

