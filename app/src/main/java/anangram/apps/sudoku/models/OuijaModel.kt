package anangram.apps.sudoku.models

data class OuijaModel(
    val enabled: Boolean = false,
    val selected: Boolean = false,
    val value: Value,
    val cells: Set<Int>,
) {
    fun addCell(position: Int): OuijaModel {
        return this.copy(cells = cells.plus(position))
    }

    fun removeCell(position: Int): OuijaModel {
        return this.copy(cells = cells.minus(position))
    }

}