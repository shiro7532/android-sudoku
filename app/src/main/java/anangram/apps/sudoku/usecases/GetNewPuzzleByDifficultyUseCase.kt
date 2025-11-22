package anangram.apps.sudoku.usecases

import anangram.apps.sudoku.base.Either
import anangram.apps.sudoku.base.UseCase
import anangram.apps.sudoku.models.GameDifficulty
import anangram.apps.sudoku.models.SudokuModel
import anangram.apps.sudoku.repository.PuzzleRepository

class GetNewPuzzleByDifficultyUseCase(
    val repository: PuzzleRepository
) : UseCase.WithArgsAndResult<GameDifficulty, SudokuModel> {
    override suspend fun invoke(arg: UseCase.Arg<GameDifficulty>): Either<SudokuModel> {
        TODO("Not yet implemented")
    }
}