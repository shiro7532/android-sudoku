package anangram.apps.sudoku.data.dao

import anangram.apps.sudoku.data.entities.GameStateEntity
import anangram.apps.sudoku.data.entities.PuzzleWithGameState
import anangram.apps.sudoku.data.entities.SudokuPuzzleEntity
import anangram.apps.sudoku.models.PuzzleDifficulty
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface SudokuDao {
    @Query("SELECT * FROM sudoku_puzzles WHERE id = :id")
    suspend fun getPuzzleById(id: String): PuzzleWithGameState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPuzzle(puzzle: SudokuPuzzleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameState(puzzle: GameStateEntity)

    @Query("DELETE FROM sudoku_puzzles WHERE id = :id")
    suspend fun deletePuzzle(id: String)

    @Query("SELECT * FROM sudoku_puzzles")
    suspend fun getAllPuzzles(): List<SudokuPuzzleEntity>

    @Query(
        """
    SELECT difficulty, COUNT(*) as count
    FROM sudoku_puzzles p 
    LEFT JOIN game_states g ON p.id = g.puzzleId
    WHERE g.puzzleId IS NULL
    GROUP BY difficulty
"""
    )
    suspend fun getCountGroupedByDifficultyWithoutGameState(): Map<
            @MapColumn(columnName = "difficulty") PuzzleDifficulty,
            @MapColumn(columnName = "count") Int
            >

    @Transaction
    @Query(
        """
        SELECT p.*
        FROM sudoku_puzzles AS p
        INNER JOIN game_states AS g ON p.id = g.puzzleId
        WHERE p.difficulty = :difficulty
          AND g.isInProgress = 1
        LIMIT 1
    """
    )
    suspend fun getPuzzleInProgress(difficulty: PuzzleDifficulty): PuzzleWithGameState?

    @Transaction
    @Query(
        """
    SELECT p.*
    FROM sudoku_puzzles AS p
    LEFT JOIN game_states AS g ON p.id = g.puzzleId
    WHERE p.difficulty = :difficulty
      AND g.puzzleId IS NULL
    LIMIT 1
"""
    )
    suspend fun getNewGameByDifficulty(difficulty: PuzzleDifficulty): SudokuPuzzleEntity?


    @Query(
        """
    SELECT  p.*
    FROM sudoku_puzzles p 
    LEFT JOIN game_states g ON p.id = g.puzzleId
    WHERE g.puzzleId IS NOT NULL
    ORDER BY g.timeSpentSec ASC
"""
    )
    suspend fun getCompletedGames(): List<PuzzleWithGameState>

}