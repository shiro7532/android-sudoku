package anangram.apps.sudoku.ui.screens

import anangram.apps.sudoku.ui.components.BottomBar
import anangram.apps.sudoku.ui.components.OuijaBoard
import anangram.apps.sudoku.ui.components.SudokuBoard
import anangram.apps.sudoku.ui.components.TopBar
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SudokuScreen(viewModel: SudokuViewModel, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = modifier.fillMaxSize()) {
        TopBar(viewModel.timeFlow)
        Box(contentAlignment = Alignment.Center) {
            Column {
                SudokuBoard(
                    unitSize = viewModel.instance.unitSize,
                    cells = viewModel.instance.cells,
                    onCellClicked = {
                        viewModel.onCellClicked(it)
                    }, modifier = Modifier.padding(8.dp)
                )
                OuijaBoard(viewModel = viewModel, modifier = Modifier.padding(8.dp))
            }
        }
        BottomBar(
            undoEnabled = false,
            onUndoClicked = { },
            redoEnabled = false,
            onRedoClicked = { },
            onResetClicked = { },
            pencilEnabled = viewModel.isPencil,
            onPencilClicked = viewModel::onPencilIconClicked,
        )
    }
}