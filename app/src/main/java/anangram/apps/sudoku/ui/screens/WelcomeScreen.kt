package anangram.apps.sudoku.ui.screens

import anangram.apps.sudoku.R
import anangram.apps.sudoku.models.PuzzleDifficulty
import anangram.apps.sudoku.ui.components.ThemeBottomSheet
import anangram.apps.sudoku.viewmodels.WelcomeNavigation
import anangram.apps.sudoku.viewmodels.WelcomeUiAction
import anangram.apps.sudoku.viewmodels.WelcomeUiState
import anangram.apps.sudoku.viewmodels.WelcomeViewModel
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun WelcomeScreen(
    vm: WelcomeViewModel,
    navigateToGame: (id: String) -> Unit,
    navigateToLeaderboards: () -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.navigation.collect {
            when (it) {
                is WelcomeNavigation.Game -> navigateToGame(it.id)
                WelcomeNavigation.LeaderBoard -> navigateToLeaderboards()
            }
        }
    }
    when (state) {
        is WelcomeUiState.UiContent -> Content(
            state as WelcomeUiState.UiContent,
            vm::onAction
        )

        is WelcomeUiState.Error -> Error((state as WelcomeUiState.Error).message)
        WelcomeUiState.Loading -> Loading()
    }
}

@Composable
private fun Content(content: WelcomeUiState.UiContent, onAction: (WelcomeUiAction) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onAction(WelcomeUiAction.SetDifficulty(content.difficulty))
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    ThemeBottomSheet(
        isVisible = content.isThemeSelectorOpen,
        selectedThemeIndex = content.themeIndex,
        onDismiss = { onAction(WelcomeUiAction.ToggleThemeSelector) },
        onThemeSelected = { onAction(WelcomeUiAction.SetTheme(it)) }
    )
    Surface {
        Box(
            contentAlignment = Alignment.TopCenter, modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton({
                onAction(WelcomeUiAction.ToggleThemeSelector)
            }, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(painterResource(R.drawable.ic_palette), contentDescription = "Themes")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
//            Text("Sudoku Zen", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.tertiary)
                var atEnd by remember { mutableStateOf(true) }
                Image(
                    painter = rememberAnimatedVectorPainter(
                        animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.anim_sudoku_grid),
                        atEnd = atEnd
                    ),
                    contentDescription = "Logo",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .size(288.dp)
                )
                var currIndex = content.difficulty.ordinal
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(enabled = currIndex > 0, onClick = {
                        currIndex--
                        onAction(WelcomeUiAction.SetDifficulty(PuzzleDifficulty.entries[currIndex]))
                        atEnd = !atEnd
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_chevron_left),
                            contentDescription = "Previous"
                        )
                    }

                    Text(
                        PuzzleDifficulty.entries[currIndex].name.lowercase()
                            .replaceFirstChar { it.uppercase() },
                        textAlign = TextAlign.Center, modifier = Modifier.width(100.dp)
                    )
                    IconButton(enabled = currIndex < PuzzleDifficulty.entries.lastIndex, onClick = {

                        currIndex++
                        onAction(WelcomeUiAction.SetDifficulty(PuzzleDifficulty.entries[currIndex]))
                        atEnd = !atEnd
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_chevron_right),
                            contentDescription = "Next"
                        )

                    }

                }

                FilledTonalButton({
                    onAction(WelcomeUiAction.StartNewGame)
                }) {
                    Text("New Game")
                }
                if (content.inProgressGameId != null)
                    ElevatedButton({
                        onAction(WelcomeUiAction.ResumeGame)

                    }) {
                        Text("Resume Game")
                    }

                OutlinedButton({
                    onAction(WelcomeUiAction.GoToLeaderboards)
                }) {
                    Text("Leaderboards")
                }

//            ThemePaletteCarousel(ThemePresets.All, 3, {})
            }
        }
    }
}

@Composable
private fun Error(message: String, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.error) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                "Something went wrong!\n Please contact the developer.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Error Message: $message",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}


@Composable
private fun Loading(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    val color by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.surface,
        targetValue = MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        contentAlignment = Alignment.Center, modifier = modifier
            .fillMaxSize()
            .background(color)
    ) {
    }
}

