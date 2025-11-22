package anangram.apps.sudoku.network

import anangram.apps.sudoku.network.models.SudokuResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SudokuApiService {
    @GET("api")
    suspend fun getRandomPuzzle(): SudokuResponse

    @POST("api")
    suspend fun getPuzzleByDifficulty(@Body body: Map<String, @Serializable Any>): SudokuResponse
}
