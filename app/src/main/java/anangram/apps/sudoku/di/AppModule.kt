package anangram.apps.sudoku.di

import anangram.apps.sudoku.ui.theme.ThemeRepository
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { ThemeRepository(get()) }
    viewModelOf(::SudokuViewModel)
}
