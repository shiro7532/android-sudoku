package anangram.apps.sudoku.usecases

import anangram.apps.sudoku.base.Either
import anangram.apps.sudoku.base.UseCase
import anangram.apps.sudoku.models.PuzzleDifficulty
import anangram.apps.sudoku.models.PuzzleModel
import anangram.apps.sudoku.repository.PuzzleRepository

class GetCompletedGamesByDifficultyUseCase(
    val repository: PuzzleRepository
) : UseCase.WithResult<Map<PuzzleDifficulty, List<PuzzleModel>>> {
    override suspend fun invoke(): Either<Map<PuzzleDifficulty, List<PuzzleModel>>> {
        try {
            val game = repository.getCompletedGamesGroupedByDifficulty()
            return Either.Success(game)
        } catch (e: Exception) {
            return Either.Failure(e)
        }
    }
}