package anangram.apps.sudoku.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

data class OuijaModel(
    var selected: Boolean = false,
    val entry: Entry,
    var cells: MutableSet<CellModel>,
) {
    var count: Int by mutableIntStateOf(cells.size)
    fun addCell(cell: CellModel) {
        cells.add(cell)
        count = cells.count { it.pencilText.isEmpty() }
    }

    fun removeCell(cell: CellModel) {
        cells.remove(cell)
        count = cells.count { it.pencilText.isEmpty() }
    }

}