package anangram.apps.sudoku.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

data class Border(
    val color: Color,
    val width: Dp,
    val top: Boolean,
    val left: Boolean,
    val bottom: Boolean,
    val right: Boolean,
)
