package anangram.apps.sudoku.models

import anangram.apps.sudoku.viewmodels.BoardUpdateEvent
import kotlin.math.sqrt

data class PuzzleSaveState(
    val gameId: String,
    val isInProgress: Boolean = true,
    val timeInSec: Int,
    val saveTimestamp: Long,
    val cellSaveStates: List<CellSaveState>
)

data class PuzzleModel(
    val id: String,
    val input: Map<Int, Int>,
    val output: Map<Int, Int>,
    val saveState: PuzzleSaveState? = null
) {
    val size = output.size
    val sideSize = sqrt(size.toDouble()).toInt()
    val unitSize = sqrt(sideSize.toDouble()).toInt()

    // ---- CELLS --------------------------------------------------------------

    val cells = Array(size) { i ->
        CellModel(unitSize, Entry.entries[input[i] ?: 0])
    }

    // ---- OUIJA SETS: Fast, clean, entry-indexed sets ------------------------

    /**
     * ouijaSets contains(cell) tells us which cells currently have that final value.
     * Index 0 = UNASSIGNED.
     */
    private val ouijaSets: Array<MutableSet<CellModel>> =
        Array(sideSize + 1) { mutableSetOf<CellModel>() }

    fun getSameEntryCells(entry: Entry): Set<CellModel> =
        ouijaSets[entry.ordinal]

    // ---- ROWS, COLS, BOXES --------------------------------------------------

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
        val boxRow = (box / unitSize) * unitSize
        val boxCol = (box % unitSize) * unitSize
        Array(sideSize) { pos ->
            cells[(boxRow + pos / unitSize) * sideSize + (boxCol + pos % unitSize)]
        }
    }

    // ---- NEIGHBOR SETUP -----------------------------------------------------

    private fun fillNeighbors() {
        cells.forEachIndexed { index, cell ->
            cell.setNeighbors(getNeighbors(index))
        }
    }

    private fun getNeighbors(pos: Int): Array<CellModel> {
        return buildSet {
            addAll(rows[pos / sideSize])
            addAll(cols[pos % sideSize])
            addAll(
                boxes[
                    (pos / sideSize) / unitSize * unitSize +
                            (pos % sideSize) / unitSize
                ]
            )
            remove(cells[pos])
        }.toTypedArray()
    }

    init {
        // Add each cell's initial value into the ouija sets
        cells.forEach { cell ->
            val entry = cell.state.value.entry
            if (entry != Entry.UNASSIGNED) {
                ouijaSets[entry.ordinal].add(cell)
            }
        }
        fillNeighbors()
        saveState?.let { saves ->
            saves.cellSaveStates.forEach { save ->
                cells[save.position].takeIf { !it.isFixed }?.let { cell ->
                    cell.loadSaveState(save)
                    val entry = cell.state.value.entry
                    if (entry != Entry.UNASSIGNED) {
                        ouijaSets[entry.ordinal].add(cell)
                    }
                }
            }
        }
    }

    // ---- EVENT HANDLING -----------------------------------------------------

    suspend fun handleEvent(
        event: BoardUpdateEvent
    ): Pair<UndoRedoManager.EntryState, UndoRedoManager.EntryState> {

        val before = getCellEntryState(event.position)

        when (event) {
            is BoardUpdateEvent.Delete -> clearContent(event.position)
            is BoardUpdateEvent.Set ->
                if (event.inPencil)
                    togglePencil(event.entry, event.position)
                else
                    setEntry(event.entry, event.position)
        }

        val after = getCellEntryState(event.position)
        return before to after
    }

    private fun getCellEntryState(pos: Int): UndoRedoManager.EntryState {
        val cell = cells[pos]
        return UndoRedoManager.EntryState(
            entry = cell.state.value.entry,
            pencilValue = cell.getEncodedPencilValue()
        )
    }

    suspend fun setEntryState(position: Int, entryState: UndoRedoManager.EntryState) {
        val cell = cells[position]

        // Reset cell content first
        clearOuijaEntry(cell)
        cell.clearContent()

        // Set entry
        cell.setEntry(entryState.entry)

        // Add to ouija if not unassigned
        if (entryState.entry != Entry.UNASSIGNED) {
            ouijaSets[entryState.entry.ordinal].add(cell)
        }

        // Restore pencils (if any)
        cell.decodePencilValue(entryState.pencilValue)
    }

    // ---- ENTRY / PENCIL OPERATIONS -----------------------------------------

    private suspend fun setEntry(entry: Entry, pos: Int) {
        val cell = cells[pos]
        val old = cell.state.value.entry

        // Remove from old ouija set
        if (old != Entry.UNASSIGNED) {
            ouijaSets[old.ordinal].remove(cell)
        }

        // Clear pencils and content
        cell.clearContent()

        // Set entry
        cell.setEntry(entry)

        // Add to ouija
        if (entry != Entry.UNASSIGNED) {
            ouijaSets[entry.ordinal].add(cell)
        }
    }

    suspend fun clearContent(pos: Int) {
        val cell = cells[pos]
        val oldEntry = cell.state.value.entry

        // Remove from ouija
        if (oldEntry != Entry.UNASSIGNED) {
            ouijaSets[oldEntry.ordinal].remove(cell)
        }

        cell.clearContent()
    }

    private suspend fun togglePencil(entry: Entry, pos: Int) {
        // Pencil marks should never affect ouija sets.
        cells[pos].togglePencil(entry)
    }

    private fun clearOuijaEntry(cell: CellModel) {
        val old = cell.state.value.entry
        if (old != Entry.UNASSIGNED) {
            ouijaSets[old.ordinal].remove(cell)
        }
    }
}
