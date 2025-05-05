package anangram.apps.sudoku.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
    Row(horizontalArrangement = Arrangement.SpaceAround, modifier = modifier.fillMaxWidth()) {
        IconButton(onClick = onResetClicked) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Reset Game"
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Validate"
            )
        }
        IconButton(enabled = undoEnabled, onClick = onUndoClicked) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowLeft,
                contentDescription = "Undo"
            )
        }
        IconButton(enabled = redoEnabled, onClick = onRedoClicked) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Redo"
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.ThumbUp,
                contentDescription = "Like?"
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
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Pencil Mode"
            )
        }
    }
}