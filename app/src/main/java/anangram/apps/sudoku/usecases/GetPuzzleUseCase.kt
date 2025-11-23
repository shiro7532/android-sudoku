package anangram.apps.sudoku.usecases

import anangram.apps.sudoku.base.Either
import anangram.apps.sudoku.base.UseCase
import anangram.apps.sudoku.models.PuzzleModel
import anangram.apps.sudoku.repository.PuzzleRepository


class GetPuzzleUseCase(
    val repository: PuzzleRepository
) : UseCase.WithArgsAndResult<String, PuzzleModel> {
    override suspend fun invoke(arg: UseCase.Arg<String>): Either<PuzzleModel> {
        try {
            val game = repository.getPuzzle(arg.content)
            return Either.Success(game)
        } catch (e: Exception) {
            return Either.Failure(e)
        }
    }
}