package anangram.apps.sudoku.network

import anangram.apps.sudoku.network.models.SudokuResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SudokuApiService {
    @GET("dosuku")
    suspend fun getMultiplePuzzles(
        @Query("query") query: String
    ): SudokuResponse
}
