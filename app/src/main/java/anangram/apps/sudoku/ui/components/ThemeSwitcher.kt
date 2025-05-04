package anangram.apps.sudoku.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SwitchButton(
    primary: Color,
    secondary: Color,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = {}, modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .size(60.dp)
                .drawBehind {
                    drawCircle(
                        color = primary,
                    )
                    drawCircle(
                        radius = size.width / 4,
                        color = secondary,
                    )
                }
        ) {}
    }
}


@Preview
@Composable
private fun SwitchButtonPreview() {
    MaterialTheme {
        SwitchButton(
            primary = MaterialTheme.colorScheme.primary,
            secondary = MaterialTheme.colorScheme.secondary
        )
    }

}