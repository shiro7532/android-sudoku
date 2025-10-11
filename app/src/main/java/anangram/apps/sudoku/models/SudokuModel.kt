package anangram.apps.sudoku.models

data class SudokuModel(
    val unitSize: Int,

    val input: Map<Int, Int>,
    val output: Map<Int, Int>
) {
    val sideSize = unitSize * unitSize
    val size = sideSize * sideSize

    val cells = Array(size) { i ->
        CellModel(unitSize, Entry.entries[input[i] ?: 0])
    }

    val ouijas = Entry.entries.subList(0, sideSize + 1).map { value ->
        OuijaModel(
            entry = value,
            cells = if (value == Entry.UNASSIGNED) mutableSetOf() else cells.filter { it.state.value.entry == value }
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
        return buildSet {
            addAll(rows[position / sideSize])
            addAll(cols[position % sideSize])
            addAll(boxes[(position / sideSize) / unitSize * unitSize + (position % sideSize) / unitSize])
            remove(cells[position])
        }.toTypedArray()
    }

    fun getSameEntryCells(entry: Entry) = ouijas.first { it.entry == entry }.cells

    suspend fun setEntry(entry: Entry, position: Int) {
        val cell = cells[position]
        removeCellFromOuijaBoard(cell)
        cell.setEntry(entry)
        ouijas[entry.ordinal].addCell(cell)
    }

    suspend fun replaceEntry(entry: Entry, position: Int) {
        clearContent(position)
        setEntry(entry, position)
    }


    suspend fun clearContent(position: Int) {
        val cell = cells[position]
        if (cell.isClean) return
        removeCellFromOuijaBoard(cell)
        cell.clearContent()
    }

    suspend fun markPencil(entry: Entry, position: Int): Boolean {
        val cell = cells[position]
        val isAdded = cell.togglePencil(entry)
        if (isAdded)
            ouijas[entry.ordinal].addCell(cell)
        else
            ouijas[entry.ordinal].removeCell(cell)
        return isAdded
    }

    private fun removeCellFromOuijaBoard(cell: CellModel) = ouijas.forEach {
        it.removeCell(cell)
    }

}
