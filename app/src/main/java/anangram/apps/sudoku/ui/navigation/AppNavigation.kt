package anangram.apps.sudoku.ui.navigation

import anangram.apps.sudoku.ui.screens.LeaderboardScreen
import anangram.apps.sudoku.ui.screens.SudokuScreen
import anangram.apps.sudoku.ui.screens.WelcomeScreen
import anangram.apps.sudoku.ui.screens.WonScreen
import anangram.apps.sudoku.viewmodels.LeaderboardViewModel
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import anangram.apps.sudoku.viewmodels.WelcomeViewModel
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
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = modifier
    ) {
        composable("welcome") {
            val viewModel: WelcomeViewModel = koinViewModel()
            WelcomeScreen(vm = viewModel, { puzzleId ->
                navController.navigate("sudoku/$puzzleId")
            }, {
                navController.navigate("leaderboard")
            })
        }
        composable("sudoku/{puzzleId}") { backStackEntry ->
            val viewModel: SudokuViewModel = koinViewModel()
            SudokuScreen(
                vm = viewModel,
                navigateUp = { navController.navigateUp() },
                navigateToWon = {
                    navController.navigate("won") {
                        popUpTo("welcome")
                    }
                })

            val lifeCycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifeCycleOwner) {
                lifeCycleOwner.lifecycle.addObserver(viewModel)
                onDispose {
                    lifeCycleOwner.lifecycle.removeObserver(viewModel)
                }
            }
        }
        composable("leaderboard") {
            val viewModel: LeaderboardViewModel = koinViewModel()
            LeaderboardScreen(viewModel) {
                navController.navigateUp()
            }
        }
        composable("won") {
            WonScreen({
                navController.popBackStack("welcome", false)
            }, {
                navController.navigate("leaderboard") {
                    popUpTo("welcome")
                }
            })
        }
    }
}