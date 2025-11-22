package anangram.apps.sudoku.base

import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException

sealed class Either<out T> {

    init {
        checkNestedEither()
    }

    data class Success<T>(val content: T) : Either<T>()
    data class Failure(val throwable: Throwable) : Either<Nothing>()

    fun checkNestedEither(): Either<T> {
        if (this is Success) check(content !is Either<*>) { "Nested Either prohibited! $this" }
        return this
    }

    fun <R> map(f: (T) -> R): Either<R> = when (this) {
        is Success -> Success(f(content))
        is Failure -> Failure(throwable)
    }.checkNestedEither()

    fun <R> flatMap(f: (T) -> Either<R>): Either<R> = when (this) {
        is Success -> try {
            f(content)
        } catch (t: Throwable) {
            Failure(t)
        }

        is Failure -> Failure(throwable)
    }.checkNestedEither()

    fun content(): T = when (this) {
        is Success -> content
        is Failure -> throw throwable
    }

    fun contentOrNull(): T? = when (this) {
        is Success -> content
        is Failure -> null
    }

    fun unwrap(
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = when (this) {
        is Success -> onSuccess(content)
        is Failure -> onFailure(throwable)
    }

    suspend fun <T> retryEither(
        retryCount: Int = 3,
        retryDelay: Long = 1000L,
        producer: () -> Either<T>
    ): Either<T> {
        for (attempt in 1..retryCount) {
            val result = producer()
            if (result is Success || attempt == retryCount) {
                return result
            }
            delay(retryDelay)
        }
        throw IllegalStateException("Retry attempts exhausted")
    }
}

inline fun <T> Either(producer: () -> T): Either<T> = try {
    Either.Success(producer()).checkNestedEither()
} catch (e: CancellationException) {
    throw e
} catch (t: Throwable) {
    Either.Failure(t)

}.checkNestedEither()