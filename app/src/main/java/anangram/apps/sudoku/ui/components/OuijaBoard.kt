package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.models.Entry
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp.Companion.Hairline
import androidx.compose.ui.unit.dp

@Composable
fun OuijaBoard(viewModel: SudokuViewModel, modifier: Modifier = Modifier) {
    val selectedEntry by viewModel.selectedEntry.collectAsState(null)
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 60.dp),
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(16.dp)
    ) {
        items(viewModel.instance.sideSize) { index ->
            val item = Entry.entries[index + 1]
            val remaining = viewModel.instance.let {
                it.sideSize - it.ouijas[index + 1].count
            }
            OuijaCell(
                selected = selectedEntry == item,
                entry = item,
                remaining = remaining,
                onClicked = { viewModel.onValueClicked(item) },
            )
        }
        item {
            OuijaCell(
                selected = false,
                entry = Entry.UNASSIGNED,
                remaining = 0,
                onClicked = { viewModel.onDeleteClicked() }
            )
        }
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
            .clickable(onClick = onClicked)
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