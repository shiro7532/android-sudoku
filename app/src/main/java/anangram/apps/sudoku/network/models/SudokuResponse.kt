package anangram.apps.sudoku.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class SudokuRequest(
    val count: Int
) {
    val query: String
        get() = "{newboard(limit:$count){grids{value,solution,difficulty},results,message}}"
}

@Serializable
data class SudokuResponse(
    @SerialName("newboard")
    val board: Board
)

@Serializable
data class Board(
    val grids: List<Grid>,
    val results: Int,
    val message: String
)

@Serializable
data class Grid(
    val value: List<List<Int>>,      // 9x9 grid, 0 means empty cell
    val solution: List<List<Int>>,   // 9x9 solved grid
    val difficulty: String
)