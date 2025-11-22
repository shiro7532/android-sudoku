package anangram.apps.sudoku.di

import anangram.apps.sudoku.data.database.AppDatabase
import anangram.apps.sudoku.network.SudokuApiService
import anangram.apps.sudoku.repository.PuzzleRepository
import anangram.apps.sudoku.repository.PuzzleRepositoryImpl
import anangram.apps.sudoku.ui.theme.ThemeRepository
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {
    single { ThemeRepository(get()) }
    viewModelOf(::SudokuViewModel)
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
            .baseUrl("https://you-do-sudoku-api.vercel.app/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
    single { get<Retrofit>().create(SudokuApiService::class.java) }

    single<PuzzleRepository> { PuzzleRepositoryImpl(get(), get()) }
}
