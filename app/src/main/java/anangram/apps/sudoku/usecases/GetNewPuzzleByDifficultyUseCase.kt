package anangram.apps.sudoku.usecases

import anangram.apps.sudoku.base.Either
import anangram.apps.sudoku.base.UseCase
import anangram.apps.sudoku.models.PuzzleDifficulty
import anangram.apps.sudoku.models.PuzzleModel
import anangram.apps.sudoku.repository.PuzzleRepository

class GetNewPuzzleByDifficultyUseCase(
    val repository: PuzzleRepository
) : UseCase.WithArgsAndResult<PuzzleDifficulty, PuzzleModel> {
    override suspend fun invoke(arg: UseCase.Arg<PuzzleDifficulty>): Either<PuzzleModel> {
        try {
            val game = repository.getNewPuzzle(arg.content)
            return Either.Success(game)
        } catch (e: Exception) {
            return Either.Failure(e)
        }
    }
}