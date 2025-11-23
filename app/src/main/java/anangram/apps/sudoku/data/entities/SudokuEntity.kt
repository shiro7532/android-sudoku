package anangram.apps.sudoku.data.entities

import anangram.apps.sudoku.models.PuzzleDifficulty
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "sudoku_puzzles")
data class SudokuPuzzleEntity(
    @PrimaryKey val id: String, // unique puzzle id, e.g. UUID or puzzle hash
    val difficulty: PuzzleDifficulty,
    val puzzle: String, // puzzle state as a string or serialized array
    val solution: String, // solution string or array
)

class PuzzleDifficultyConverter {

    @TypeConverter
    fun fromDifficulty(value: PuzzleDifficulty): Int {
        return value.ordinal
    }

    @TypeConverter
    fun toDifficulty(value: Int): PuzzleDifficulty {
        return PuzzleDifficulty.entries[value]
    }

}