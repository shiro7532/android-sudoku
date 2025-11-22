package anangram.apps.sudoku.network.models

import kotlinx.serialization.Serializable

@Serializable
data class SudokuResponse(
    val difficulty: String,
    val puzzle: String,
    val solution: String
)