package anangram.apps.sudoku.ui.screens

import anangram.apps.sudoku.models.CellModel
import anangram.apps.sudoku.ui.components.BottomBar
import anangram.apps.sudoku.ui.components.OuijaBoard
import anangram.apps.sudoku.ui.components.SudokuBoard
import anangram.apps.sudoku.ui.components.ThemeBottomSheet
import anangram.apps.sudoku.ui.components.TopBar
import anangram.apps.sudoku.viewmodels.SudokuNavigation
import anangram.apps.sudoku.viewmodels.SudokuUiAction
import anangram.apps.sudoku.viewmodels.SudokuUiState
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SudokuScreen(
    vm: SudokuViewModel,
    navigateUp: () -> Unit,
    navigateToWon: () -> Unit,
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(Unit) {
        vm.navigation.collect {
            when (it) {
                SudokuNavigation.NavigateUp -> navigateUp()
                SudokuNavigation.Won -> navigateToWon()
            }
        }
    }
    when (state) {
        is SudokuUiState.UiContent -> Content(
            state as SudokuUiState.UiContent,
            vm.instance.cells,
            vm::onAction
        )

        is SudokuUiState.Error -> Error((state as SudokuUiState.Error).message)
        SudokuUiState.Loading -> Loading()
    }

}

@Composable
private fun Content(
    state: SudokuUiState.UiContent,
    cells: Array<CellModel>,
    onAction: (SudokuUiAction) -> Unit
) {
    if (state.showResetDialog) {
        AlertDialog(
            onDismissRequest = {
                onAction(SudokuUiAction.RequestReset) // closes dialog
            },
            confirmButton = {
                TextButton(onClick = {
                    onAction(SudokuUiAction.ConfirmResetRequest)
                }) { Text("Sure") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onAction(SudokuUiAction.DismissResetRequest)
                }) { Text("Cancel") }
            },
            title = { Text("Reset puzzle?") },
            text = { Text("All progress will be lost.") }
        )
    }


    ThemeBottomSheet(
        isVisible = state.isThemeSelectorOpen,
        selectedThemeIndex = state.themeIndex,
        onDismiss = {
            onAction(SudokuUiAction.ToggleThemeSelector)
        }, onThemeSelected = {
            onAction(SudokuUiAction.SelectTheme(it))
        }
    )

    Scaffold(
        topBar = {
            TopBar(
                time = state.time,
                onNavigateUp = { onAction(SudokuUiAction.ClickNavigateBack) },
                onToggleThemeSelector = { onAction(SudokuUiAction.ToggleThemeSelector) },
            )
        },
        bottomBar = {
            BottomBar(
                undoEnabled = state.undoAvailable,
                redoEnabled = state.redoAvailable,
                pencilEnabled = state.pencilMode,
                onUndoClicked = { onAction(SudokuUiAction.ClickUndo) },
                onRedoClicked = { onAction(SudokuUiAction.ClickRedo) },
                onPencilClicked = { onAction(SudokuUiAction.TogglePencil) },
                onResetClicked = { onAction(SudokuUiAction.RequestReset) },
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        // Your original column content INSIDE Scaffold content window
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Spacer(modifier = Modifier.weight(1f))

            SudokuBoard(
                unitSize = state.unitSize,
                cells = cells,
                onCellClicked = { onAction(SudokuUiAction.CellClicked(it)) }
            )

            Spacer(modifier = Modifier.weight(0.5f))

            OuijaBoard(
                selectedEntry = state.selectedEntry,
                sideSize = state.unitSize * state.unitSize,
                remaining = state.remaining,
                onEntryClicked = { onAction(SudokuUiAction.ValueClicked(it)) },
                onDeleteClicked = { onAction(SudokuUiAction.ClickDelete) },
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun Error(message: String, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.error) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                "Something went wrong!\n Please contact the developer.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Error Message: $message",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}


@Composable
private fun Loading(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    val color by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.surface,
        targetValue = MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        contentAlignment = Alignment.Center, modifier = modifier
            .fillMaxSize()
            .background(color)
    ) {
    }
}

