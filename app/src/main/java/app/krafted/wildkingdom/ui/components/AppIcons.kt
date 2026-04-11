package app.krafted.wildkingdom.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AppIcons {
    val Bookmark: ImageVector by lazy {
        ImageVector.Builder(
            name = "Bookmark",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(17f, 3f)
                horizontalLineTo(7f)
                curveTo(5.9f, 3f, 5f, 3.9f, 5f, 5f)
                verticalLineToRelative(16f)
                lineToRelative(7f, -7f)
                lineToRelative(7f, 7f)
                verticalLineTo(5f)
                curveTo(19f, 3.9f, 18.1f, 3f, 17f, 3f)
                close()
            }
        }.build()
    }

    val BookmarkBorder: ImageVector by lazy {
        ImageVector.Builder(
            name = "BookmarkBorder",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(17f, 3f)
                horizontalLineTo(7f)
                curveTo(5.9f, 3f, 5f, 3.9f, 5f, 5f)
                verticalLineToRelative(16f)
                lineToRelative(7f, -7f)
                lineToRelative(7f, 7f)
                verticalLineTo(5f)
                curveTo(19f, 3.9f, 18.1f, 3f, 17f, 3f)
                close()
                moveTo(17f, 18f)
                lineToRelative(-5f, -4.5f)
                lineTo(7f, 18f)
                verticalLineTo(5f)
                horizontalLineToRelative(10f)
                verticalLineToRelative(13f)
                close()
            }
        }.build()
    }

    val ChevronRight: ImageVector by lazy {
        ImageVector.Builder(
            name = "ChevronRight",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 6f)
                lineTo(8.59f, 7.41f)
                lineTo(13.17f, 12f)
                lineToRelative(-4.58f, 4.59f)
                lineTo(10f, 18f)
                lineToRelative(6f, -6f)
                close()
            }
        }.build()
    }

    val Quiz: ImageVector by lazy {
        ImageVector.Builder(
            name = "Quiz",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19f, 5f)
                horizontalLineToRelative(-2f)
                verticalLineTo(3f)
                horizontalLineTo(7f)
                verticalLineToRelative(2f)
                horizontalLineTo(5f)
                curveTo(3.9f, 5f, 3f, 5.9f, 3f, 7f)
                verticalLineToRelative(1f)
                curveToRelative(0f, 2.5f, 1.9f, 4.6f, 4.3f, 4.9f)
                curveToRelative(0.8f, 1.1f, 2.1f, 1.9f, 3.7f, 2.1f)
                verticalLineTo(18f)
                horizontalLineTo(7f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(10f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(-3f)
                curveToRelative(1.6f, -0.2f, 2.9f, -1f, 3.7f, -2.1f)
                curveToRelative(2.4f, -0.3f, 4.3f, -2.4f, 4.3f, -4.9f)
                verticalLineTo(7f)
                curveTo(21f, 5.9f, 20.1f, 5f, 19f, 5f)
                close()
                moveTo(5f, 8f)
                verticalLineTo(7f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(3.8f)
                curveTo(5.9f, 10.3f, 5f, 9.2f, 5f, 8f)
                close()
                moveTo(19f, 8f)
                curveToRelative(0f, 1.2f, -0.9f, 2.3f, -2f, 2.8f)
                verticalLineTo(7f)
                horizontalLineToRelative(2f)
                verticalLineTo(8f)
                close()
            }
        }.build()
    }
}
