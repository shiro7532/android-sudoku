package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.models.Border
import anangram.apps.sudoku.models.CellModel
import anangram.apps.sudoku.models.HighlightState
import anangram.apps.sudoku.models.Value
import anangram.apps.sudoku.ui.drawBorder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Cell(cell: CellModel, border: Border, onClicked: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center, modifier = modifier
            .aspectRatio(1f)
            .drawBehind {
                drawBorder(border)
            }
            .padding(4.dp)
            .clip(CircleShape)
            .clickable { onClicked() }
            .background(
                color = when (cell.highlightState) {
                    HighlightState.IDLE -> Color.Transparent
                    HighlightState.SELECTED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    HighlightState.SAME_VALUE -> MaterialTheme.colorScheme.secondaryContainer.copy(
                        alpha = 0.2f
                    )

                    HighlightState.NEIGHBOUR -> MaterialTheme.colorScheme.tertiaryContainer.copy(
                        alpha = 0.2f
                    )

                    HighlightState.ERROR -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                }
            )
    ) {
        Text(
            text = cell.value.ordinal.toString(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview
@Composable
private fun CellPreview() {
    Cell(
        CellModel(initialValue = Value.UNASSIGNED), border = Border(
            color = MaterialTheme.colorScheme.primary,
            width = 0.5.dp,
            top = false,
            left = false,
            bottom = false,
            right = false
        ), onClicked = {}
    )
}