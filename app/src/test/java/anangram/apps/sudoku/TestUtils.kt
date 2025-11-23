package anangram.apps.sudoku

import anangram.apps.sudoku.models.PuzzleModel

object TestUtils {
    /**
     * Creates a simple PuzzleModel with 9 cells (size = 9) which is safe for tests.
     * id - unique id string
     */
    fun makePuzzle(id: String): PuzzleModel {
        // 9×9 Sudoku → size = 81
        val output = (0 until 81).associateWith { 0 }
        val input = emptyMap<Int, Int>()
        return PuzzleModel(id = id, input = input, output = output)
    }

}
