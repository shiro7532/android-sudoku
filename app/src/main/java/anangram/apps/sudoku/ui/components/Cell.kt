package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.models.Border
import anangram.apps.sudoku.models.CellModel
import anangram.apps.sudoku.models.HighlightState
import anangram.apps.sudoku.models.Value
import anangram.apps.sudoku.ui.drawBorder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
    val backgroundColor = when {
        cell.highlightState == HighlightState.SAME_VALUE -> MaterialTheme.colorScheme.tertiary
        cell.isFixed -> Color.LightGray.copy(alpha = 0.4f)
        cell.highlightState == HighlightState.SELECTED -> MaterialTheme.colorScheme.primary
        cell.highlightState == HighlightState.NEIGHBOUR -> MaterialTheme.colorScheme.secondary.copy(
            alpha = 0.2f
        )

        cell.highlightState == HighlightState.ERROR -> MaterialTheme.colorScheme.error

        else -> Color.Transparent
    }
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .drawBehind { drawBorder(border) }
            .padding(4.dp)
            .clip(if (cell.isFixed) RoundedCornerShape(2.dp) else CircleShape)
            .clickable { onClicked() },
        color = backgroundColor,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = cell.value.ordinal.takeIf { it > 0 }?.toString() ?: " ",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
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