package anangram.apps.sudoku.models

import kotlin.math.sqrt

data class SudokuModel(
    val size: Int,
    val input: Map<Int, Int>,
    val output: Map<Int, Int>
) {

    val cells = List(size * size) { i ->
        CellModel(Value.entries[input[i] ?: 0])
    }

    val ouijas = Value.entries.subList(0, size + 1).map { value ->
        OuijaModel(
            value = value,
            cells = if (value == Value.UNASSIGNED) mutableSetOf() else cells.filter { it.value == value }
                .toMutableSet()
        )
    }

    val rows: List<List<CellModel>> = cells.chunked(size)
    val cols: List<List<CellModel>> = List(size) { col ->
        cells.filterIndexed { index, _ -> index % size == col }
    }
    val boxSize = sqrt(size.toDouble()).toInt()
    val boxes = List(size) { box ->
        cells.filterIndexed { index, _ ->
            val row = index / size
            val col = index % size
            val boxRow = row / boxSize
            val boxCol = col / boxSize
            boxRow * boxSize + boxCol == box
        }
    }

    init {
        fillNeighbors(rows)
        fillNeighbors(cols)
        fillNeighbors(boxes)
    }

    private fun fillNeighbors(group: List<List<CellModel>>) {
        group.forEach { item ->
            item.forEach { cell ->
                item.filter { it != cell }.forEach { cell.addNeighbor(it) }
            }
        }
    }


}
