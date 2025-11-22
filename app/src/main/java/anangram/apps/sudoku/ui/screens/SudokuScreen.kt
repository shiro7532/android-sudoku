package anangram.apps.sudoku.ui.screens

import anangram.apps.sudoku.ui.components.BottomBar
import anangram.apps.sudoku.ui.components.OuijaBoard
import anangram.apps.sudoku.ui.components.SudokuBoard
import anangram.apps.sudoku.ui.components.ThemeBottomSheet
import anangram.apps.sudoku.ui.components.TopBar
import anangram.apps.sudoku.viewmodels.SudokuUiAction
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun SudokuScreen(vm: SudokuViewModel, modifier: Modifier = Modifier) {
    val uiState by vm.uiState.collectAsState()
    if (uiState.showResetDialog) {
        AlertDialog(
            onDismissRequest = {
                vm.dispatch(SudokuUiAction.ResetRequested) // closes dialog
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.dispatch(SudokuUiAction.ResetRequestConfirmed)
                }) { Text("Sure") }
            },
            dismissButton = {
                TextButton(onClick = {
                    vm.dispatch(SudokuUiAction.ResetRequestDismissed)
                }) { Text("Cancel") }
            },
            title = { Text("Reset puzzle?") },
            text = { Text("All progress will be lost.") }
        )
    }


    ThemeBottomSheet(
        isVisible = uiState.isThemeSelectorOpen,
        selectedThemeIndex = uiState.themeIndex,
        onDismiss = {
            vm.dispatch(SudokuUiAction.ToggleThemeSelector)
        }, onThemeSelected = {
            vm.dispatch(SudokuUiAction.ThemeSelected(it))
        }
    )

    Scaffold(
        topBar = {
            TopBar(
                time = uiState.time,
                onToggleThemeSelector = {
                    vm.dispatch(SudokuUiAction.ToggleThemeSelector)
                },

                )
        },
        bottomBar = {
            BottomBar(
                undoEnabled = uiState.undoAvailable,
                redoEnabled = uiState.redoAvailable,
                pencilEnabled = uiState.pencilMode,
                onUndoClicked = { vm.dispatch(SudokuUiAction.UndoClicked) },
                onRedoClicked = { vm.dispatch(SudokuUiAction.RedoClicked) },
                onPencilClicked = { vm.dispatch(SudokuUiAction.PencilToggle) },
                onResetClicked = { vm.dispatch(SudokuUiAction.ResetRequested) },
            )
        },
        modifier = modifier.fillMaxSize()
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
                unitSize = vm.instance.unitSize,
                cells = vm.instance.cells,
                onCellClicked = { vm.dispatch(SudokuUiAction.CellClicked(it)) }
            )

            Spacer(modifier = Modifier.weight(0.5f))

            OuijaBoard(
                selectedEntry = uiState.selectedEntry,
                sideSize = uiState.sideSize,
                remaining = uiState.remaining,
                onEntryClicked = { vm.dispatch(SudokuUiAction.ValueClicked(it)) },
                onDeleteClicked = { vm.dispatch(SudokuUiAction.DeleteClicked) },
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}