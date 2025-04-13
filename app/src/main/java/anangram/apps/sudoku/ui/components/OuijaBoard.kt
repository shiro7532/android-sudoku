package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.models.Value
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OuijaBoard(viewModel: SudokuViewModel, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 60.dp),
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(16.dp)
    ) {
        items(viewModel.instance.size) { index ->
            OuijaCell(value = Value.entries[index + 1], remaining = 4, onClicked = {
                viewModel.onValueClicked(Value.entries[index + 1])
            })
        }
        item {
            OuijaCell(value = Value.UNASSIGNED, remaining = 0, onClicked = {
                viewModel.onValueClicked(Value.UNASSIGNED)
            })
        }
    }


}

@Composable
fun OuijaCell(value: Value, remaining: Int, onClicked: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(),
        modifier = modifier
            .clickable(onClick = onClicked)
//            .background(
//                if (value == Value.UNASSIGNED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
//            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(8.dp)
                .aspectRatio(1f)
                .clip(CircleShape)

        ) {
            Column {
                Text(
                    text = if (value == Value.UNASSIGNED) "X" else value.ordinal.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                if (value != Value.UNASSIGNED)
                    Text(
                        remaining.toString(),
                        style = MaterialTheme.typography.labelMedium
                    )
            }
        }
    }
}


@Preview
@Composable
private fun OuijaCellPreview() {
    OuijaCell(Value.SIX, 4, {})
}