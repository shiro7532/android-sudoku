package anangram.apps.sudoku.data.database

import anangram.apps.sudoku.data.dao.SudokuDao
import anangram.apps.sudoku.data.entities.Converters
import anangram.apps.sudoku.data.entities.SudokuPuzzleEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SudokuPuzzleEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sudokuDao(): SudokuDao
}
