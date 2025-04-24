package anangram.apps.sudoku.models

data class SudokuModel(
    val unitSize: Int,

    val input: Map<Int, Int>,
    val output: Map<Int, Int>
) {
    val sideSize = unitSize * unitSize
    val size = sideSize * sideSize

    val cells = Array(size) { i ->
        CellModel(unitSize, Value.entries[input[i] ?: 0])
    }

    val ouijas = Value.entries.subList(0, sideSize + 1).map { value ->
        OuijaModel(
            value = value,
            cells = if (value == Value.UNASSIGNED) mutableSetOf() else cells.filter { it.state.value == value }
                .toMutableSet()
        )
    }

    val rows = Array(sideSize) { row ->
        Array(sideSize) { col ->
            cells[row * sideSize + col]
        }
    }

    val cols = Array(sideSize) { col ->
        Array(sideSize) { row ->
            cells[row * sideSize + col]
        }
    }
    val boxes = Array(sideSize) { box ->
        val boxStartRow = (box / unitSize) * unitSize
        val boxStartCol = (box % unitSize) * unitSize
        Array(sideSize) { position ->
            cells[(boxStartRow + position / unitSize) * sideSize + boxStartCol + position % unitSize]
        }
    }

    init {
        fillNeighbors()
    }

    private fun fillNeighbors() {
        cells.forEachIndexed { index, it ->
            it.setNeighbors(getNeighbors(index))
        }
    }

    private fun getNeighbors(position: Int): Array<CellModel> {
        return buildSet<CellModel> {
            addAll(rows[position / sideSize])
            addAll(cols[position % sideSize])
            addAll(boxes[(position / sideSize) / unitSize * unitSize + (position % sideSize) / unitSize])
            remove(cells[position])
        }.toTypedArray()
    }


}
