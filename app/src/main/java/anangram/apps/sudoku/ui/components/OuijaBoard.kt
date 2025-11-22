package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.models.Entry
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp.Companion.Hairline
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OuijaBoard(
    selectedEntry: Entry?,
    sideSize: Int,
    remaining: Map<Entry, Int>,
    onEntryClicked: (Entry) -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center,
        maxItemsInEachRow = 5   // Adjust this to fit your preferred layout
    ) {
        repeat(sideSize) { index ->
            val item = Entry.entries[index + 1]

            OuijaCell(
                entry = item,
                selected = selectedEntry == item,
                remaining = remaining.getValue(item),
                onClicked = { onEntryClicked(item) },
                modifier = Modifier.size(72.dp)
            )
        }

        // DELETE cell at the end
        DeleteOuijaCell(
            enabled = selectedEntry != null,
            onClicked = { onDeleteClicked() },
            modifier = Modifier.size(72.dp)
        )
    }
}

@Composable
fun OuijaCell(
    selected: Boolean,
    entry: Entry,
    remaining: Int,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        when {
            selected -> MaterialTheme.colorScheme.tertiary
            else -> Color.Transparent
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .fillMaxSize()
            .clip(CircleShape)
            .border(Hairline, MaterialTheme.colorScheme.primary, CircleShape)
            .background(backgroundColor)
            .clickable(enabled = remaining > 0, onClick = onClicked)
            .alpha(if (remaining > 0) 1f else 0.4f)
    ) {
        Text(
            text = if (entry == Entry.UNASSIGNED) "X" else entry.ordinal.toString(),
            color = contentColorFor(backgroundColor),
            style = MaterialTheme.typography.titleLarge
        )
        if (entry != Entry.UNASSIGNED)
            Text(
                remaining.toString(),
                color = contentColorFor(backgroundColor),
                style = MaterialTheme.typography.labelSmall
            )
    }
}

@Preview
@Composable
private fun OuijaCellPreview() {
    OuijaCell(false, Entry.SIX, 4, {})
}

@Composable
fun DeleteOuijaCell(
    enabled: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        when {
            enabled -> MaterialTheme.colorScheme.error
            else -> Color.Transparent
        }

    Surface(
        shape = CircleShape,
        color = backgroundColor,
        border = BorderStroke(Hairline, MaterialTheme.colorScheme.error),
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .clickable(enabled = enabled, onClick = onClicked)
                .padding(8.dp)
                .alpha(if (enabled) 1f else 0.4f)

        ) {
            Text(
                text = "X",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview
@Composable
private fun DeleteOuijaCellPreview() {
    DeleteOuijaCell(false, {})
}