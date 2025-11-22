package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun BottomBar(
    undoEnabled: Boolean,
    onUndoClicked: () -> Unit,
    redoEnabled: Boolean,
    onRedoClicked: () -> Unit,
    onResetClicked: () -> Unit,
    pencilEnabled: Boolean,
    onPencilClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = modifier.fillMaxWidth()) {
        IconButton(onClick = onResetClicked) {
            Icon(
                painter = painterResource(R.drawable.ic_refresh),
                contentDescription = "Reset Game"
            )
        }
        IconButton(enabled = undoEnabled, onClick = onUndoClicked) {
            Icon(
                painter = painterResource(R.drawable.ic_undo),
                contentDescription = "Undo"
            )
        }
        IconButton(enabled = redoEnabled, onClick = onRedoClicked) {
            Icon(
                painter = painterResource(R.drawable.ic_redo),
                contentDescription = "Redo"
            )
        }
        val pencilContainerColor =
            if (pencilEnabled) MaterialTheme.colorScheme.primary else Color.Transparent
        IconButton(
            colors = IconButtonDefaults.iconButtonColors().copy(
                containerColor = pencilContainerColor,
                contentColor = contentColorFor(pencilContainerColor)
            ),
            onClick = onPencilClicked
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = "Pencil Mode"
            )
        }
    }
}