package anangram.apps.sudoku.data.database

import anangram.apps.sudoku.data.dao.SudokuDao
import anangram.apps.sudoku.data.entities.GameStateEntity
import anangram.apps.sudoku.data.entities.PuzzleDifficultyConverter
import anangram.apps.sudoku.data.entities.SudokuCellConverter
import anangram.apps.sudoku.data.entities.SudokuPuzzleEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SudokuPuzzleEntity::class, GameStateEntity::class], version = 1)
@TypeConverters(SudokuCellConverter::class, PuzzleDifficultyConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sudokuDao(): SudokuDao
}
