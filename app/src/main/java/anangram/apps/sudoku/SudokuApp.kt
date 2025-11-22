package anangram.apps.sudoku

import anangram.apps.sudoku.di.appModule
import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class SudokuApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SudokuApp)
            modules(appModule)
        }
    }
}
