package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.models.CellModel
import anangram.apps.sudoku.models.Entry
import anangram.apps.sudoku.models.HighlightState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Cell(cell: CellModel, onClicked: () -> Unit, modifier: Modifier = Modifier) {
    val state by cell.state.collectAsState()
    val backgroundColor = when {
        state.highlightState == HighlightState.SELECTED -> MaterialTheme.colorScheme.primary
        state.highlightState == HighlightState.SAME_VALUE -> MaterialTheme.colorScheme.tertiary
        cell.isFixed -> Color.LightGray.copy(alpha = 0.4f)
        state.highlightState == HighlightState.NEIGHBOUR -> MaterialTheme.colorScheme.secondary.copy(
            alpha = 0.2f
        )

        state.highlightState == HighlightState.ERROR -> MaterialTheme.colorScheme.error

        else -> Color.Transparent
    }
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(if (cell.isFixed) RoundedCornerShape(2.dp) else CircleShape)
            .clickable { onClicked() },
        color = backgroundColor,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (cell.pencilText.isEmpty())
                Text(
                    text = cell.state.value.entry.ordinal.takeIf { it > 0 }?.toString() ?: " ",
                    style = MaterialTheme.typography.bodyMedium,
                )
            else
                Text(
                    text = cell.pencilText,
                    minLines = 3,
                    maxLines = 3,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize / (cell.unitSize - 1),
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight / (cell.unitSize - 1)
                    ),
                    overflow = TextOverflow.Clip,
                    softWrap = true,
                    textAlign = TextAlign.Center,
//                    fontSize = MaterialTheme.typography.bodyMedium.fontSize/3,
                    modifier = Modifier.fillMaxSize()
                )
        }
    }
}

@Preview
@Composable
private fun CellPreview() {
    Cell(
        CellModel(3, initialEntry = Entry.UNASSIGNED), onClicked = {}
    )
}