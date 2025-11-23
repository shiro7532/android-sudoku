package anangram.apps.sudoku.ui.screens

import anangram.apps.sudoku.R
import anangram.apps.sudoku.models.PuzzleDifficulty
import anangram.apps.sudoku.viewmodels.LeaderboardNavigation
import anangram.apps.sudoku.viewmodels.LeaderboardUiAction
import anangram.apps.sudoku.viewmodels.LeaderboardUiState
import anangram.apps.sudoku.viewmodels.LeaderboardViewModel
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

@Composable
fun LeaderboardScreen(vm: LeaderboardViewModel, navigateUp: () -> Unit) {
    val state by vm.state.collectAsState()
    LaunchedEffect(Unit) {
        vm.navigation.collect {
            when (it) {
                is LeaderboardNavigation.NavigateUp -> navigateUp()
            }
        }
    }
    when (state) {
        is LeaderboardUiState.Content -> Content(
            state as LeaderboardUiState.Content,
            vm::onAction
        )

        is LeaderboardUiState.Error -> Error((state as LeaderboardUiState.Error).message)
        LeaderboardUiState.Loading -> Loading()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(content: LeaderboardUiState.Content, onAction: (LeaderboardUiAction) -> Unit) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Leaderboards")
                },
                navigationIcon = {
                    IconButton({ onAction(LeaderboardUiAction.ClickNavigateUp) }) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Navigate Back"
                        )
                    }
                }
            )
        },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            SingleChoiceSegmentedButtonRow {
                PuzzleDifficulty.entries.forEachIndexed { index, d ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = PuzzleDifficulty.entries.size
                        ),
                        onClick = { onAction(LeaderboardUiAction.SelectDifficulty(d)) },
                        selected = content.selectedDifficulty == d,
                        icon = { null }
                    ) {
                        Text(d.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (content.items.isEmpty())
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {

                            Text("No records created yet!", modifier = Modifier.alpha(0.6f))
                        }
                    }
                else
                    items(content.items) {
                        Card {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(formatTimestamp(it.first))
                                Text(formatSeconds(it.second))
                            }
                        }
                    }
            }
        }
    }
}

private fun formatTimestamp(
    timestamp: Long, // epoch millis
): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp

    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }

    fun isSameDay(a: Calendar, b: Calendar): Boolean =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

    return when {
        isSameDay(calendar, today) -> "Today"
        isSameDay(calendar, yesterday) -> "Yesterday"
        else -> "%02d %s %d".format(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
            calendar.get(Calendar.YEAR)
        )
    }
}

private fun formatSeconds(sec: Int): String {
    val minutes = sec / 60
    val seconds = sec % 60
    return buildString {
        if (minutes > 0) append("$minutes min ")
        append("$seconds sec")
    }.trim()
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

