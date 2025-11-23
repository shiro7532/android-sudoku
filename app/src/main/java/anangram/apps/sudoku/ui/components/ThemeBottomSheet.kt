package anangram.apps.sudoku.ui.components

import anangram.apps.sudoku.R
import anangram.apps.sudoku.ui.theme.SudokuTheme
import anangram.apps.sudoku.ui.theme.ThemePreset
import anangram.apps.sudoku.ui.theme.ThemePresets
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ThemeBottomSheet(
    isVisible: Boolean,
    selectedThemeIndex: Int,
    onDismiss: () -> Unit,
    onThemeSelected: (Int) -> Unit
) {
    if (!isVisible) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                "Pick your theme",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            ThemePaletteCarousel(
                ThemePresets.All,
                selectedThemeIndex,
                onThemeSelected,
                modifier = Modifier.height(100.dp)
            )
        }
    }
}

fun LazyListState.isItemFullyVisible(index: Int): Boolean {
    val item = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index } ?: return false

    val start = item.offset
    val end = item.offset + item.size

    return start >= 0 && end <= layoutInfo.viewportEndOffset
}

@Composable
fun ThemePaletteCarousel(
    palettes: List<ThemePreset>,
    selectedIndex: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyListState()
    LaunchedEffect(selectedIndex) {
        if (!listState.isItemFullyVisible(selectedIndex))
            listState.animateScrollToItem(selectedIndex)
    }

    FlowRow(
        verticalArrangement = Arrangement.Center,
        itemVerticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {

        palettes.forEachIndexed { index, item ->
            ThemePaletteItem(
                item,
                index == selectedIndex,
                { onClick(index) },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
private fun ThemePaletteItem(
    preset: ThemePreset,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = when (isSystemInDarkTheme()) {
        true -> preset.dark
        false -> preset.light
    }
    val size by animateDpAsState(
        targetValue = if (selected) 75.dp else 50.dp,
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = modifier
                .size(size)
                .clip(MaterialTheme.shapes.small)
                .background(scheme.primaryContainer)
                .border(1.dp, scheme.primary, MaterialTheme.shapes.small)
                .clickable(enabled = !selected, onClick = onClick)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.4f)
                    .align(Alignment.BottomEnd)
                    .clip(
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 0.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .background(scheme.primary)
            )

            TickReveal(
                selected,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.7f)
            )

        }
        Text(
            preset.name,
            style = MaterialTheme.typography.labelSmall,
            color = scheme.primary
        )
    }
}

@Composable
fun TickReveal(
    selected: Boolean,
    modifier: Modifier = Modifier,
    duration: Int = 600 // in ms
) {

    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(duration),
        label = "tick_reveal"
    )

    Icon(
        painter = painterResource(R.drawable.ic_check),
        contentDescription = "Selected",
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .drawWithContent {
                val widthToShow = size.width * progress

                // Reveal only (0f → widthToShow)
                clipRect(
                    left = 0f,
                    top = 0f,
                    right = widthToShow,
                    bottom = size.height
                ) {
                    this@drawWithContent.drawContent()
                }
            }
    )
}

@Preview
@Composable
private fun TickRevealPreview() {
    TickReveal(true)
}


@Preview
@Composable
private fun ThemeSelectorPreview() {
    SudokuTheme(0) {
        ThemePaletteItem(ThemePresets.Peach, false, {})
    }
}