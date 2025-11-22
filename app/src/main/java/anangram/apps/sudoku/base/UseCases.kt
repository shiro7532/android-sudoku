package anangram.apps.sudoku.base

import kotlinx.coroutines.flow.Flow

sealed interface UseCase<in P, out Q> {
    data class Arg<out T>(val content: T)

    interface NoArgs : UseCase<Unit, Unit> {
        suspend operator fun invoke(): Either<Unit>
    }

    interface WithArgs<in P> : UseCase<P, Unit> {
        suspend operator fun invoke(arg: Arg<P>): Either<Unit>
    }

    interface WithResult<out Q> : UseCase<Unit, Q> {
        suspend operator fun invoke(): Either<Q>
    }

    interface WithArgsAndResult<in P, out Q> : UseCase<P, Q> {
        suspend operator fun invoke(arg: Arg<P>): Either<Q>
    }

    interface WithFlowResult<out Q> : UseCase<Unit, Q> {
        operator fun invoke(): Either<Flow<Q>>
    }

    interface WithArgsAndFlowResult<in P, out Q> : UseCase<P, Q> {
        operator fun invoke(arg: Arg<P>): Either<Flow<Q>>
    }

}