package com.anhnn.emoji_merge.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Thiết kế chỉ có một tông tối, và điểm nhấn phải là lime/magenta của app,
// nên bỏ hẳn dynamic color lẫn nhánh light theme.
private val NeonScheme = darkColorScheme(
    primary = Lime,
    onPrimary = OnLime,
    primaryContainer = Lime,
    onPrimaryContainer = OnLime,
    secondary = Magenta,
    onSecondary = TextPrimary,
    secondaryContainer = MagentaEnd,
    onSecondaryContainer = TextPrimary,
    tertiary = Cyan,
    onTertiary = OnCyan,
    background = Ink,
    onBackground = TextPrimary,
    surface = Ink,
    onSurface = TextPrimary,
    surfaceVariant = GlassFill,
    onSurfaceVariant = TextMuted,
    surfaceContainerLowest = InkDeep,
    surfaceContainerLow = PanelBottom,
    surfaceContainer = PanelTop,
    surfaceContainerHigh = GlassFill,
    surfaceContainerHighest = GlassStroke,
    outline = GlassStroke,
    outlineVariant = GlassStrokeStrong,
    error = Danger,
    onError = TextPrimary,
    errorContainer = PanelTopDanger,
    onErrorContainer = Danger,
    scrim = InkDialog,
)

@Composable
fun EmojimergeTheme(content: @Composable () -> Unit) {
    // Nền luôn tối nên icon thanh trạng thái luôn là màu sáng.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = NeonScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
