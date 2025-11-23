package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.R
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    time: Int,
    onNavigateUp: () -> Unit,
    onToggleThemeSelector: () -> Unit,
    modifier: Modifier = Modifier
) {

    CenterAlignedTopAppBar(
        title = { Text(formatSeconds(time)) },
        navigationIcon = {
            IconButton(onClick = {
                onNavigateUp()
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Pencil Mode"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                onToggleThemeSelector()
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_palette),
                    contentDescription = "Pencil Mode"
                )
            }
        },
        modifier = modifier
    )
}

fun formatSeconds(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "${DecimalFormat("00").format(minutes)}:${DecimalFormat("00").format(remainingSeconds)}"
}