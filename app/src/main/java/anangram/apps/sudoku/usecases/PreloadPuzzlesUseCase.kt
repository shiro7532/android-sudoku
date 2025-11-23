package anangram.apps.sudoku.usecases

import anangram.apps.sudoku.base.Either
import anangram.apps.sudoku.base.UseCase
import anangram.apps.sudoku.repository.PuzzleRepository


class PreloadPuzzlesUseCase(
    val repository: PuzzleRepository
) : UseCase.NoArgs {
    override suspend fun invoke(): Either<Unit> {
        try {
            repository.fetchPuzzles()
            return Either.Success(Unit)
        } catch (e: Exception) {
            return Either.Failure(e)
        }
    }
}