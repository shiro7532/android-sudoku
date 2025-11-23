package anangram.apps.sudoku.ui.screens

import anangram.apps.sudoku.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun WonScreen(navigateHome: () -> Unit, navigateToLeaderboard: () -> Unit) {
    var showButtons by remember { mutableStateOf(false) }
    var animatedImageVector by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        animatedImageVector = true
        delay(3000)
        showButtons = true
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(showButtons) {
            Text("You Won !", style = MaterialTheme.typography.displayMedium)
        }
        Image(
            painter = rememberAnimatedVectorPainter(
                animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.anim_win_trophy),
                atEnd = animatedImageVector
            ),
            contentDescription = "Logo",
            modifier = Modifier
                .size(288.dp)
        )
        AnimatedVisibility(showButtons) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilledTonalButton(navigateHome) {
                    Text("Return to Main Menu")
                }
                OutlinedButton(navigateToLeaderboard) {
                    Text("Go to Leaderboards")
                }
            }
        }
    }
}