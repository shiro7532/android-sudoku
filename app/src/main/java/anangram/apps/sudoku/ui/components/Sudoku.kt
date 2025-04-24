package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.models.CellModel
import anangram.apps.sudoku.models.Value
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp.Companion.Hairline
import androidx.compose.ui.unit.dp

@Composable
fun SudokuBoard(
    unitSize: Int,
    cells: Array<CellModel>,
    onCellClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    val sideSize = unitSize * unitSize
    val cellSize = sideSize * sideSize
    LazyVerticalGrid(
        columns = GridCells.Fixed(sideSize), modifier = modifier
            .drawBehind {
                val majorOffset = 60f
                val minorOffset = 30f
                val minorPathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(size.height / sideSize - minorOffset, minorOffset),
                    phase = -minorOffset / 2
                )
                val majorPathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(size.height / unitSize - majorOffset, majorOffset),
                    phase = -majorOffset / 2
                )
                for (i in 1 until sideSize) {
                    drawLine(
                        color = borderColor,
                        start = Offset(size.width * i / sideSize, 0f),
                        end = Offset(size.width * i / sideSize, size.height),
                        pathEffect = if (i % unitSize == 0) majorPathEffect else minorPathEffect,
                        strokeWidth = if (i % unitSize == 0) 1.dp.toPx() else Hairline.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, size.height * i / sideSize),
                        end = Offset(size.width, size.height * i / sideSize),
                        pathEffect = if (i % unitSize == 0) majorPathEffect else minorPathEffect,
                        strokeWidth = if (i % unitSize == 0) 1.dp.toPx() else Hairline.toPx(),
                        cap = StrokeCap.Round

                    )
                }
            }) {
        items(cellSize) { index ->
            Cell(
                cell = cells[index],
                onClicked = { onCellClicked(index) },
            )
        }
    }

}

@Preview
@Composable
private fun SudokuModel() {
    MaterialTheme {
        SudokuBoard(
            unitSize = 9,
            cells = Array(81) { CellModel(3, Value.UNASSIGNED) },
            onCellClicked = {})
    }
}