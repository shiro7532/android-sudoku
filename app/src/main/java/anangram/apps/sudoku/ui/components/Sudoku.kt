package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.models.Border
import anangram.apps.sudoku.models.SudokuModel
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SudokuBoard(
    instance: SudokuModel,
    onCellClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyVerticalGrid(columns = GridCells.Fixed(instance.unitSize), modifier = modifier) {
        items(instance.boxes.size) { index ->
            CellBox(
                cells = instance.boxes[index].map { instance.cellsMap[it]!! }, border = Border(
                    color = MaterialTheme.colorScheme.primary,
                    width = 0.5.dp,
                    top = index / instance.unitSize != 0,
                    left = false,
                    bottom = false,
                    right = index % instance.unitSize != instance.unitSize - 1,
                ),
                onCellClicked = onCellClicked
            )
        }
    }

}

@Preview
@Composable
private fun SudokuModel() {
    MaterialTheme {
        SudokuBoard(
            SudokuModel(9, emptyMap(), emptyMap()),
            onCellClicked = {})
    }
}