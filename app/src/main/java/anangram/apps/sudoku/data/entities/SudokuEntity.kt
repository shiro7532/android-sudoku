package anangram.apps.sudoku.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Entity(tableName = "sudoku_puzzles")
data class SudokuPuzzleEntity(
    @PrimaryKey val id: String, // unique puzzle id, e.g. UUID or puzzle hash
    val difficulty: String,
    val puzzle: String, // puzzle state as a string or serialized array
    val solution: String, // solution string or array

    @Embedded val gameState: GameState? // nullable nested object
)

data class GameState(
    val timeSpentSec: Int, // time spent on this game
    val pencilStateCompressed: Int // represent boolean[9] as an int in [0,512)
)

@Serializable
data class SudokuCell(
    val position: Int,
    val value: Int,
    val pencilState: List<Boolean>
)

class Converters {
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    @TypeConverter
    fun fromSudokuCellList(value: List<SudokuCell>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toSudokuCellList(value: String?): List<SudokuCell>? {
        return value?.let { json.decodeFromString(it) }
    }
}