package anangram.apps.sudoku.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class PuzzleWithGameState(
    @Embedded val puzzle: SudokuPuzzleEntity,


    @Relation(
        parentColumn = "id",
        entityColumn = "puzzleId"
    )
    val gameState: GameStateEntity?
)
