package anangram.apps.sudoku.repository

import anangram.apps.sudoku.data.dao.SudokuDao
import anangram.apps.sudoku.network.SudokuApiService

interface PuzzleRepository

class PuzzleRepositoryImpl(val apiService: SudokuApiService, val dao: SudokuDao) :
    PuzzleRepository