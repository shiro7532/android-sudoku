package anangram.apps.sudoku.models

import kotlinx.coroutines.flow.MutableStateFlow

class CellModel(
    val unitSize: Int,
    val initialEntry: Entry,
) {

    data class State(
        val entry: Entry,
        val highlightState: HighlightState,
        val pencilText: String,
    )

    val state = MutableStateFlow(State(initialEntry, HighlightState.IDLE, ""))
    val isFixed: Boolean = initialEntry != Entry.UNASSIGNED
    private val pencilValues = BooleanArray(unitSize * unitSize) { false }
    fun getEncodedPencilValue(): Int {
        var v = 0
        for (i in pencilValues.indices) {
            if (pencilValues[i]) v = v or (1 shl i)
        }
        return v
    }

    suspend fun decodePencilValue(mask: Int, size: Int = unitSize * unitSize) {
        for (i in 0 until size) {
            pencilValues[i] = (mask and (1 shl i)) != 0
        }
        state.emit(state.value.copy(pencilText = pencilText))

    }


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

    val isClean: Boolean
        get() = state.value.entry == Entry.UNASSIGNED && pencilValues.all { !it }


    suspend fun setEntry(entry: Entry) {
        if (isFixed || entry == Entry.UNASSIGNED) return

        resetPencil()
        state.emit(state.value.copy(entry = entry, pencilText = pencilText))

        /*
        TODO(Depends on Composite Events)
        neighbors.forEach {
            if (it.pencilValues[entry.ordinal - 1]) {
                it.togglePencil(entry)
                it.state.emit(it.state.value.copy(pencilText = it.pencilText))
            }
        }
        */
    }

    suspend fun clearContent() {
        if (isFixed || isClean) return
        resetPencil()
        state.emit(state.value.copy(entry = Entry.UNASSIGNED, pencilText = pencilText))
    }

    suspend fun togglePencil(entry: Entry): Boolean {
        pencilValues[entry.ordinal - 1] = !pencilValues[entry.ordinal - 1]
        state.emit(state.value.copy(entry = Entry.UNASSIGNED, pencilText = pencilText))
        return pencilValues[entry.ordinal - 1]
    }

    fun hasPencil(entry: Entry): Boolean {
        return pencilValues[entry.ordinal - 1]
    }


    private fun resetPencil() {
        pencilValues.fill(false)
    }

    private lateinit var neighbors: Array<CellModel>

    fun setNeighbors(neighbors: Array<CellModel>) {
        if (this::neighbors.isInitialized) return
        this.neighbors = neighbors
    }

    suspend fun highlight(highlightState: HighlightState, override: Boolean = false) {
        if (override || state.value.highlightState.priority < highlightState.priority) {
            state.emit(state.value.copy(highlightState = highlightState))
        }
    }

    suspend fun highlightAsSelected(override: Boolean = false) {
        highlight(HighlightState.SELECTED, override)
        neighbors.forEach {
            it.highlightAsNeighbor()
        }
    }

    suspend fun highlightAsNeighbor(override: Boolean = false) {
        highlight(HighlightState.NEIGHBOUR, override)
    }

    suspend fun highlightAsSameValue(override: Boolean = false) {
        highlight(HighlightState.SAME_VALUE, override)
    }

    suspend fun unHighlight(affectNeighbors: Boolean = true) {
        state.emit(state.value.copy(highlightState = HighlightState.IDLE))
        if (affectNeighbors)
            neighbors.forEach {
                it.unHighlight(affectNeighbors = false)
            }
    }
}

