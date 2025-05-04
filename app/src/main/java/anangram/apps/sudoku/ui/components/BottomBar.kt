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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomBar(
    modifier: Modifier = Modifier
) {
    Row(horizontalArrangement = Arrangement.SpaceAround, modifier = modifier.fillMaxWidth()) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Reset Game"
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Undo"
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowLeft,
                contentDescription = "Undo"
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Redo"
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.ThumbUp,
                contentDescription = "Redo"
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Pencil Mode"
            )
        }
    }
}