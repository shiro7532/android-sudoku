package anangram.apps.sudoku.data.dao

import anangram.apps.sudoku.data.entities.SudokuPuzzleEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SudokuDao {
    @Query("SELECT * FROM sudoku_puzzles WHERE id = :id")
    suspend fun getPuzzleById(id: String): SudokuPuzzleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPuzzle(puzzle: SudokuPuzzleEntity)

    @Query("DELETE FROM sudoku_puzzles WHERE id = :id")
    suspend fun deletePuzzle(id: String)

    @Query("SELECT * FROM sudoku_puzzles")
    suspend fun getAllPuzzles(): List<SudokuPuzzleEntity>
}