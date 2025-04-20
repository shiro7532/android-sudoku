package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.models.OuijaModel
import anangram.apps.sudoku.models.Value
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
fun OuijaBoard(
    map: Map<Value, OuijaModel>,
    onValueClicked: (Value) -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 60.dp),
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(16.dp)
    ) {
        items(items = map.keys.toList()) { v: Value ->
            val item = map[v]
            OuijaCell(
                model = item,
                onClicked = {
                    onValueClicked(v)
                },
            )
        }
        item {
            OuijaCell(null, onClicked = onDeleteClicked)
        }
    }


}

@Composable
fun OuijaCell(
    model: OuijaModel?,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        when {
            model == null -> MaterialTheme.colorScheme.tertiary
            model.selected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.tertiary
        }

    Card(
        onClick = onClicked,
        enabled = model?.enabled != false,
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
                text = model?.value?.ordinal?.toString() ?: "X",
                style = MaterialTheme.typography.titleLarge
            )
            if (model != null)
                Text(
                    model.cells.count().toString(),
                    style = MaterialTheme.typography.labelSmall
                )
        }
    }
}


@Preview
@Composable
private fun OuijaCellPreview() {
    OuijaCell(null, {})
}