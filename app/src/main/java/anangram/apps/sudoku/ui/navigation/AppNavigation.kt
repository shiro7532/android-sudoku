package anangram.apps.sudoku.ui.navigation

import anangram.apps.sudoku.ui.screens.SudokuScreen
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    NavHost(
        navController = rememberNavController(),
        startDestination = "sudoku",
        modifier = modifier
    ) {
        composable("sudoku") {
            val viewModel: SudokuViewModel = koinViewModel()
            SudokuScreen(vm = viewModel)
            val lifeCycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifeCycleOwner) {
                lifeCycleOwner.lifecycle.addObserver(viewModel)
                onDispose {
                    lifeCycleOwner.lifecycle.removeObserver(viewModel)
                }
            }
        }
    }
}