package anangram.apps.sudoku.usecases

import anangram.apps.sudoku.base.Either
import anangram.apps.sudoku.base.UseCase
import anangram.apps.sudoku.models.PuzzleDifficulty
import anangram.apps.sudoku.models.PuzzleModel
import anangram.apps.sudoku.repository.PuzzleRepository


class GetInProgressPuzzleUseCase(
    val repository: PuzzleRepository
) : UseCase.WithArgsAndResult<PuzzleDifficulty, PuzzleModel?> {
    override suspend fun invoke(arg: UseCase.Arg<PuzzleDifficulty>): Either<PuzzleModel?> {
        try {
            val inProgressGame = repository.getPuzzleInProgress(arg.content)
            return Either.Success(inProgressGame)
        } catch (e: Exception) {
            return Either.Failure(e)
        }
    }
}