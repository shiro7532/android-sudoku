package anangram.apps.sudoku.models

import androidx.compose.runtime.mutableStateMapOf

data class SudokuModel(
    val unitSize: Int,
    val input: Map<Int, Int>,
    val output: Map<Int, Int>
) {
    val sideSize = unitSize * unitSize
//    val size = sideSize * sideSize

    val cellsMap = mutableStateMapOf<Int, CellModel>(
        *(0 until sideSize * sideSize).map {
            it to CellModel(
                unitSize = unitSize,
                position = it,
                isFixed = input[it] != null,
                value = input[it]?.let { Value.entries[it] } ?: Value.UNASSIGNED,
            )
        }.toTypedArray()
    )

    val boxes = Array<IntArray>(sideSize) { index ->
        val boxRow = index / unitSize
        val boxCol = index % unitSize
        val startRow = boxRow * unitSize
        val startCol = boxCol * unitSize
        val array = IntArray(sideSize) { 0 }
        for (i in 0 until unitSize) {
            for (j in 0 until unitSize) {
                val row = startRow + i
                val col = startCol + j
                array[i * unitSize + j] = row * sideSize + col
            }
        }
        array
    }

    val ouijas = mutableStateMapOf<Value, OuijaModel>(
        *Value.entries.subList(1, sideSize + 1).map { value ->
            value to OuijaModel(
                enabled = false,
                value = value,
                cells = if (value == Value.UNASSIGNED) emptySet() else input.filter { it.value == value.ordinal }.values.toSet()
            )
        }.toTypedArray()
    )


}
