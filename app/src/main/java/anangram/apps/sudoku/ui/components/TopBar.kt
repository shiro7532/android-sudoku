package anangram.apps.sudoku.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import java.text.DecimalFormat

@Composable
fun TopBar(
    time: Flow<Int>,
    modifier: Modifier = Modifier
) {
    val gameTime by time.collectAsStateWithLifecycle(0)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier.fillMaxWidth()
    ) {
        SwitchButton(
            primary = MaterialTheme.colorScheme.primary,
            secondary = MaterialTheme.colorScheme.secondary
        )


        Text(formatSeconds(gameTime), style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Pencil Mode"
            )
        }

    }
}

fun formatSeconds(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "${DecimalFormat("00").format(minutes)}:${DecimalFormat("00").format(remainingSeconds)}"
}