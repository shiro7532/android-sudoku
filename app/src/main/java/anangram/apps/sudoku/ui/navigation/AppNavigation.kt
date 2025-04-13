package anangram.apps.sudoku.ui.navigation

import anangram.apps.sudoku.ui.screens.SudokuScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    NavHost(
        navController = rememberNavController(),
        startDestination = "sudoku",
        modifier = modifier
    ) {
        composable("sudoku") {
            SudokuScreen(viewModel = viewModel())
        }
    }
}