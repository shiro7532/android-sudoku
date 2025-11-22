package anangram.apps.sudoku.usecases

import anangram.apps.sudoku.base.Either
import anangram.apps.sudoku.base.UseCase
import anangram.apps.sudoku.models.GameDifficulty
import anangram.apps.sudoku.repository.PuzzleRepository


class HasInProgressPuzzleUseCase(
    val repository: PuzzleRepository
) : UseCase.WithArgsAndResult<GameDifficulty, Boolean> {
    override suspend fun invoke(arg: UseCase.Arg<GameDifficulty>): Either<Boolean> {
        TODO("Not yet implemented")
    }
}