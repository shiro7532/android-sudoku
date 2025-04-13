package anangram.apps.sudoku.ui

import anangram.apps.sudoku.models.Border
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope

fun DrawScope.drawBorder(border: Border) {
    val strokeWidth = border.width.toPx()
    val widthPx = size.width
    val heightPx = size.height
    val offset = 20f

    if (border.top) drawLine(
        color = border.color,
        start = Offset(offset, 0f),
        end = Offset(widthPx - offset, 0f),
        strokeWidth = strokeWidth
    )
    if (border.bottom) drawLine(
        color = border.color,
        start = Offset(offset, heightPx),
        end = Offset(widthPx - offset, heightPx),
        strokeWidth = strokeWidth
    )
    if (border.left) drawLine(
        color = border.color,
        start = Offset(0f, offset),
        end = Offset(0f, heightPx - offset),
        strokeWidth = strokeWidth
    )
    if (border.right) drawLine(
        color = border.color,
        start = Offset(widthPx, offset),
        end = Offset(widthPx, heightPx - offset),
        strokeWidth = strokeWidth
    )
}