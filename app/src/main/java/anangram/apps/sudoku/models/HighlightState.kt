package anangram.apps.sudoku.models

enum class HighlightState(val priority: Int) {
    IDLE(0),
    SELECTED(3),
    SAME_VALUE(1),
    NEIGHBOUR(2),
    ERROR(4)
}