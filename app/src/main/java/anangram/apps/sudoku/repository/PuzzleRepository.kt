package anangram.apps.sudoku.repository

import anangram.apps.sudoku.data.dao.SudokuDao
import anangram.apps.sudoku.data.entities.GameStateEntity
import anangram.apps.sudoku.data.entities.PuzzleWithGameState
import anangram.apps.sudoku.data.entities.SudokuCell
import anangram.apps.sudoku.data.entities.SudokuPuzzleEntity
import anangram.apps.sudoku.models.CellSaveState
import anangram.apps.sudoku.models.Entry
import anangram.apps.sudoku.models.PuzzleDifficulty
import anangram.apps.sudoku.models.PuzzleModel
import anangram.apps.sudoku.models.PuzzleNotFoundException
import anangram.apps.sudoku.models.PuzzleSaveState
import anangram.apps.sudoku.network.SudokuApiService
import anangram.apps.sudoku.network.models.SudokuRequest
import anangram.apps.sudoku.network.models.SudokuResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

interface PuzzleRepository {
    suspend fun fetchPuzzles()
    suspend fun getPuzzleInProgress(difficulty: PuzzleDifficulty): PuzzleModel?
    suspend fun getNewPuzzle(difficulty: PuzzleDifficulty): PuzzleModel
    suspend fun getPuzzle(id: String): PuzzleModel
    suspend fun saveGameState(save: PuzzleSaveState)
    suspend fun getCompletedGamesGroupedByDifficulty(): Map<PuzzleDifficulty, List<PuzzleModel>>
}

class PuzzleRepositoryImpl(val apiService: SudokuApiService, val dao: SudokuDao) :
    PuzzleRepository {
    override suspend fun fetchPuzzles() {
        withContext(Dispatchers.IO) {
            val availablePuzzles = dao.getCountGroupedByDifficultyWithoutGameState()
            while (availablePuzzles.values.isEmpty() || availablePuzzles.values.any { it < 1 }) {
                val response = apiService.getMultiplePuzzles(SudokuRequest(1).query)
                val entityObject = response.mapResponseToEntity()
                entityObject.forEach {
                    dao.insertPuzzle(it)
                }
            }
        }
    }

    override suspend fun getPuzzleInProgress(difficulty: PuzzleDifficulty): PuzzleModel? {
        return withContext(Dispatchers.IO) {
            dao.getPuzzleInProgress(difficulty)?.puzzle?.mapEntityToModel()
        }
    }

    override suspend fun getNewPuzzle(difficulty: PuzzleDifficulty): PuzzleModel {
        return withContext(Dispatchers.IO) {
            dao.getNewGameByDifficulty(difficulty)?.mapEntityToModel()
                ?: throw PuzzleNotFoundException()
        }
    }

    override suspend fun getPuzzle(id: String): PuzzleModel {
        return withContext(Dispatchers.IO) {
            dao.getPuzzleById(id)?.mapEntityToModel() ?: throw PuzzleNotFoundException()
        }
    }

    override suspend fun saveGameState(save: PuzzleSaveState) {
        return withContext(Dispatchers.IO) {
            save.mapModelToEntity().let {
                dao.insertGameState(it)
            }
        }
    }

    override suspend fun getCompletedGamesGroupedByDifficulty(): Map<PuzzleDifficulty, List<PuzzleModel>> {
        return withContext(Dispatchers.IO) {
            dao.getCompletedGames()
                .groupBy { it.puzzle.difficulty }
                .mapValues { entry -> entry.value.map { it.mapEntityToModel() } }
        }
    }
}

private fun SudokuResponse.mapResponseToEntity(): List<SudokuPuzzleEntity> {
    return board.grids.map { grid ->
        val puzzleString = flattenGridToString(grid.value)
        val solutionString = flattenGridToString(grid.solution)
        val difficultyEnum =
            PuzzleDifficulty.valueOf(grid.difficulty.uppercase()) // case-insensitive match

        SudokuPuzzleEntity(
            id = puzzleString.sha256(),
            difficulty = difficultyEnum,
            puzzle = puzzleString,
            solution = solutionString,
        )
    }
}

private fun SudokuPuzzleEntity.mapEntityToModel(): PuzzleModel {
    return PuzzleModel(
        id = id,
        buildMap {
            puzzle.forEachIndexed { index, ch ->
                if (ch.isDigit() && ch.digitToInt() != 0) put(
                    index,
                    ch.digitToInt()
                )
            }
        },
        buildMap {
            solution.forEachIndexed { index, ch ->
                if (ch.isDigit() && ch.digitToInt() != 0) put(
                    index,
                    ch.digitToInt()
                )
            }
        },
    )
}


private fun PuzzleWithGameState.mapEntityToModel(): PuzzleModel {
    return PuzzleModel(
        id = puzzle.id,
        buildMap {
            puzzle.puzzle.forEachIndexed { index, ch ->
                if (ch.isDigit() && ch.digitToInt() != 0) put(
                    index,
                    ch.digitToInt()
                )
            }
        },
        buildMap {
            puzzle.solution.forEachIndexed { index, ch ->
                if (ch.isDigit() && ch.digitToInt() != 0) put(
                    index,
                    ch.digitToInt()
                )
            }
        },
        saveState = gameState?.let {
            PuzzleSaveState(
                gameState.puzzleId,
                gameState.isInProgress,
                gameState.timeSpentSec,
                gameState.saveTimestamp,
                cellSaveStates = gameState.cells.map {
                    CellSaveState(
                        position = it.position,
                        entry = Entry.entries[it.value],
                        pencil = it.pencilState
                    )
                }
            )
        })
}

private fun PuzzleSaveState.mapModelToEntity(): GameStateEntity {
    return GameStateEntity(
        gameId,
        isInProgress,
        timeInSec,
        saveTimestamp,
        cellSaveStates.map {
            SudokuCell(
                it.position,
                it.entry.ordinal,
                it.pencil
            )
        })
}

// Helper to flatten 9x9 Int grid to a single String like "000500300..." row-wise
private fun flattenGridToString(grid: List<List<Int>>): String {
    return grid.flatten().joinToString(separator = "") { it.toString() }
}

fun String.sha256(): String {
    val bytes = this.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.joinToString("") { "%02x".format(it) }
}