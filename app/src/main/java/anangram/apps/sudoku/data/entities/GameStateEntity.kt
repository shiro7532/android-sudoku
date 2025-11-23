package anangram.apps.sudoku.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Entity(tableName = "game_states")
data class GameStateEntity(
    @PrimaryKey val puzzleId: String, // foreign key to SudokuPuzzleEntity
    val isInProgress: Boolean,
    val timeSpentSec: Int,
    val saveTimestamp: Long,
    val cells: List<SudokuCell>  // nullable or default value
)

@Serializable
data class SudokuCell(
    val position: Int,
    val value: Int,
    val pencilState: Int
)

class SudokuCellConverter {
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