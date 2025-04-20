package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.models.Border
import anangram.apps.sudoku.models.CellModel
import anangram.apps.sudoku.models.HighlightState
import anangram.apps.sudoku.models.Value
import anangram.apps.sudoku.ui.drawBorder
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

@Composable
fun CellBox(
    cells: List<CellModel>,
    border: Border,
    onCellClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = cells.chunked(sqrt(cells.size.toDouble()).toInt())
    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawBorder(border)
            }) {
        rows.forEachIndexed { rowIndex, row ->
            Row {
                row.forEachIndexed { colIndex, cell ->
                    Cell(
                        cell,
                        border = Border(
                            color = MaterialTheme.colorScheme.primary,
                            width = 0.1.dp,
                            top = rowIndex != 0,
                            left = colIndex != 0,
                            bottom = false,
                            right = false
                        ),
                        onClicked = { onCellClicked(cell.position) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CellBoxPreview() {
    MaterialTheme {
        CellBox(
            List(9) {
                CellModel(
                    unitSize = 3,
                    position = 4,
                    value = Value.UNASSIGNED,
                    highlightState = HighlightState.IDLE,
                    isFixed = false
                )
            },
            Border(color = MaterialTheme.colorScheme.primary, width = 2.dp, true, true, true, true),
            onCellClicked = {},
        )

    }

}