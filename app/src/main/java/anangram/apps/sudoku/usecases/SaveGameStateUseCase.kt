package anangram.apps.sudoku.usecases

import anangram.apps.sudoku.base.Either
import anangram.apps.sudoku.base.UseCase
import anangram.apps.sudoku.models.PuzzleSaveState
import anangram.apps.sudoku.repository.PuzzleRepository


class SaveGameStateUseCase(
    val repository: PuzzleRepository
) : UseCase.WithArgs<PuzzleSaveState> {
    override suspend fun invoke(arg: UseCase.Arg<PuzzleSaveState>): Either<Unit> {
        try {
            val game = repository.saveGameState(arg.content)
            return Either.Success(game)
        } catch (e: Exception) {
            return Either.Failure(e)
        }
    }
}