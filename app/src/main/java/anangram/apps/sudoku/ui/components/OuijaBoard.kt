package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.models.Value
import anangram.apps.sudoku.viewmodels.SudokuViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        items(viewModel.instance.sideSize) { index ->
            val item = Value.entries[index + 1]
            val remaining = viewModel.instance.let {
                it.sideSize - it.ouijas[index + 1].count
            }
            OuijaCell(
                enabled = remaining > 0 && viewModel.selectedCell?.isFixed == false,
                selected = viewModel.selectedValue == item,
                value = item,
                remaining = remaining,
                onClicked = {
                    viewModel.onValueClicked(item)
                },
            )
        }
        item {
            OuijaCell(
                enabled = viewModel.selectedCell != null && viewModel.selectedCell?.isFixed == false && viewModel.selectedCell?.state?.value != Value.UNASSIGNED,
                selected = false,
                value = Value.UNASSIGNED,
                remaining = 0,
                onClicked = {
                    viewModel.onValueClicked(Value.UNASSIGNED)
                })
        }
    }


}

@Composable
fun OuijaCell(
    enabled: Boolean,
    selected: Boolean,
    value: Value,
    remaining: Int,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        when {
            value == Value.UNASSIGNED -> MaterialTheme.colorScheme.error
            selected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.tertiary
        }

    Card(
        onClick = onClicked,
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.7f)
        ),
        border = null,
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(1f)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(2.dp)
                .fillMaxSize()
        ) {
            Text(
                text = if (value == Value.UNASSIGNED) "X" else value.ordinal.toString(),
                style = MaterialTheme.typography.titleLarge
            )
            if (value != Value.UNASSIGNED)
                Text(
                    remaining.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
        }
    }
}


@Preview
@Composable
private fun OuijaCellPreview() {
    OuijaCell(false, false, Value.SIX, 4, {})
}