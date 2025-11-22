package anangram.apps.sudoku.models

class UndoRedoManager {
    data class EntryState(
        val entry: Entry,
        val pencilValue: Int
    )

    sealed class Move(
        open val position: Int,
        open val inPencil: Boolean,
        open val before: EntryState,
        open val after: EntryState,
    ) {
        data class Set(
            override val position: Int,
            override val inPencil: Boolean,
            override val before: EntryState,
            override val after: EntryState,
        ) : Move(position, inPencil, before, after)

        data class Clear(
            override val position: Int,
            override val inPencil: Boolean,
            override val before: EntryState,
            override val after: EntryState,
        ) : Move(position, inPencil, before, after)


        fun inverse(): Move = when (this) {
            is Clear -> Set(
                before = this.after,
                inPencil = this.inPencil,
                after = this.before,
                position = this.position
            )

            is Set -> Clear(
                before = this.after,
                inPencil = this.inPencil,
                after = this.before,
                position = this.position
            )
        }

    }


    private val undoStack = ArrayDeque<Move>()
    val undoAvailable
        get() = undoStack.isNotEmpty()

    private val redoStack = ArrayDeque<Move>()
    val redoAvailable
        get() = redoStack.isNotEmpty()

    private fun ArrayDeque<Move>.push(item: Move) = addLast(item)
    private fun ArrayDeque<Move>.pop(): Move = removeLast()

    fun undo(): Pair<Int, EntryState> {
        val event = undoStack.pop()
        val inverse = event.inverse()
        redoStack.push(event)
        return inverse.position to inverse.after
    }

    fun redo(): Pair<Int, EntryState> {
        val item = redoStack.pop()
        undoStack.push(item)
        return item.position to item.after
    }

    fun registerMove(move: Move) {
        undoStack.push(move)
        redoStack.clear()
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }

}