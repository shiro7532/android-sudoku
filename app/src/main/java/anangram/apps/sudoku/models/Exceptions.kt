package anangram.apps.sudoku.models

class PuzzleNotFoundException(
    message: String = "No puzzle available for the requested difficulty"
) : Exception(message)