package anangram.apps.sudoku.ui.screens

import anangram.apps.sudoku.ui.components.OuijaBoard
import anangram.apps.sudoku.ui.components.SudokuBoard
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SudokuScreen(viewModel: SudokuViewModel, modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Column {
            SudokuBoard(
                viewModel.instance, onCellClicked = {
                    viewModel.onCellClicked(it)
                }, modifier = Modifier.padding(8.dp)
            )
            OuijaBoard(
                map = viewModel.instance.ouijas,
                onValueClicked = { viewModel.onValueClicked(it) },
                onDeleteClicked = { viewModel.onDeleteClicked() },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}