package anangram.apps.sudoku.di

import anangram.apps.sudoku.data.database.AppDatabase
import anangram.apps.sudoku.network.SudokuApiService
import anangram.apps.sudoku.repository.PuzzleRepository
import anangram.apps.sudoku.repository.PuzzleRepositoryImpl
import anangram.apps.sudoku.ui.theme.ThemeRepository
import anangram.apps.sudoku.usecases.GetCompletedGamesByDifficultyUseCase
import anangram.apps.sudoku.usecases.GetInProgressPuzzleUseCase
import anangram.apps.sudoku.usecases.GetNewPuzzleByDifficultyUseCase
import anangram.apps.sudoku.usecases.GetPuzzleUseCase
import anangram.apps.sudoku.usecases.PreloadPuzzlesUseCase
import anangram.apps.sudoku.usecases.SaveGameStateUseCase
import anangram.apps.sudoku.viewmodels.LeaderboardViewModel
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import anangram.apps.sudoku.viewmodels.WelcomeViewModel
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val appModule = module {
    single { ThemeRepository(get()) }
    viewModelOf(::SudokuViewModel)
    viewModelOf(::WelcomeViewModel)
    viewModelOf(::LeaderboardViewModel)
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "sudoku-db")
            .fallbackToDestructiveMigration(true)
            .build()
    }
    single { get<AppDatabase>().sudokuDao() }

    single {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl("https://sudoku-api.vercel.app/api/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            )
            .build()
    }
    single { get<Retrofit>().create(SudokuApiService::class.java) }

    single<PuzzleRepository> { PuzzleRepositoryImpl(get(), get()) }

    single { PreloadPuzzlesUseCase(get()) }
    single { GetInProgressPuzzleUseCase(get()) }
    single { GetNewPuzzleByDifficultyUseCase(get()) }
    single { GetPuzzleUseCase(get()) }
    single { SaveGameStateUseCase(get()) }
    single { GetCompletedGamesByDifficultyUseCase(get()) }
}
