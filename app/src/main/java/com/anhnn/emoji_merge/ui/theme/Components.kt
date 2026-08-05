package com.anhnn.emoji_merge.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ================================================================== *
 *  Khung màn hình
 * ================================================================== */

/**
 * Nền gradient riêng của từng màn + chừa chỗ thanh trạng thái.
 * Mọi màn bắt đầu bằng đây thay vì Scaffold, vì thiết kế không dùng
 * bề mặt Material nào ở tầng gốc.
 */
@Composable
fun NeonScreen(
    backdrop: ScreenBackdrop,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Ink)
            .background(backdrop.brush),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            content = content,
        )
    }
}

/** Đệm cho thanh điều hướng hệ thống — dùng ở cuối màn có nút neo đáy. */
@Composable
fun NavBarSpacer() = Spacer(Modifier.windowInsetsPadding(WindowInsets.navigationBars))

/**
 * Top bar: nút back bo tròn 42dp bên trái, tiêu đề Baloo canh giữa kèm dòng
 * phụ tuỳ chọn, một ô hành động bên phải. Hai bên rộng bằng nhau nên tiêu đề
 * luôn nằm đúng giữa màn.
 */
@Composable
fun NeonTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 22.dp, end = 22.dp, top = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.widthIn(min = 42.dp)) {
            if (onBack != null) {
                GlassIconButton(glyph = "‹", contentDescription = "Quay lại", onClick = onBack)
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                subtitle()
            }
        }
        Box(Modifier.widthIn(min = 42.dp), contentAlignment = Alignment.CenterEnd) {
            trailing?.invoke()
        }
    }
}

/** Ô vuông kính mờ chứa một ký tự hoặc emoji. */
@Composable
fun GlassIconButton(
    glyph: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    glyphSize: Int = 18,
) {
    Box(
        modifier = modifier
            .size(size)
            .glass(MaterialTheme.shapes.small)
            .pressable(onClick = onClick, label = contentDescription),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = glyph, fontSize = glyphSize.sp, color = TextPrimary)
    }
}

/* ================================================================== *
 *  Bề mặt
 * ================================================================== */

/** Kính mờ: nền trắng rất nhạt + viền 1dp. Bề mặt mặc định của thiết kế. */
fun Modifier.glass(
    shape: Shape,
    fill: Color = GlassFill,
    stroke: Color = GlassStroke,
    strokeWidth: Dp = 1.dp,
): Modifier = this
    .clip(shape)
    .background(fill)
    .border(strokeWidth, stroke, shape)

/** Panel gradient tím dùng cho thân dialog và các khối cần nhấn mạnh. */
@Composable
fun GradientPanel(
    modifier: Modifier = Modifier,
    top: Color = PanelTop,
    bottom: Color = PanelBottom,
    stroke: Color = GlassStrokeStrong,
    shape: Shape = PanelShape,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(shape)
            .background(linearGradientDeg(top, bottom, degrees = 180.0))
            .border(1.dp, stroke, shape),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

/* ================================================================== *
 *  Nút "chunky" — chi tiết đặc trưng nhất của thiết kế
 * ================================================================== */

/**
 * Bảng màu nút: gradient mặt, màu khối đổ bóng cứng bên dưới, và màu chữ.
 * Đặt tên theo vai trò để không đụng tên với các hằng màu trong [Color.kt].
 */
enum class ChunkyStyle(
    internal val top: Color,
    internal val bottom: Color,
    internal val shadow: Color,
    internal val content: Color,
) {
    /** Hành động chính — lime. Mỗi màn chỉ nên có một. */
    Primary(Lime, LimeEnd, LimeShadow, OnLime),

    /** Hành động phụ nổi bật — magenta. */
    Accent(Magenta, MagentaEnd, MagentaShadow, TextPrimary),

    /** Khu vực Mini Games — cyan. */
    Cool(Cyan, CyanEnd, CyanShadow, OnCyan),

    /** Xoá, thoát — đỏ. */
    Destructive(Danger, DangerEnd, DangerShadow, TextPrimary),
}

private val ChunkyDepth = 5.dp

/**
 * Nút có khối đổ bóng cứng phía dưới và lún xuống khi nhấn.
 * [height] là chiều cao mặt nút; khối bóng cộng thêm [ChunkyDepth] bên dưới.
 */
@Composable
fun ChunkyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ChunkyStyle = ChunkyStyle.Primary,
    enabled: Boolean = true,
    height: Dp = 54.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    Box(modifier = modifier.height(height + ChunkyDepth)) {
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(if (enabled) style.shadow else GlassStroke),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = if (pressed && enabled) ChunkyDepth else 0.dp)
                .fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(
                    if (enabled) linearGradientDeg(style.top, style.bottom)
                    else SolidColor(GlassFillSoft)
                )
                .pressable(onClick = onClick, enabled = enabled, interaction = interaction),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = textStyle,
                color = if (enabled) style.content else TextFaint,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Nút phụ dạng kính mờ, cùng tổng chiều cao với [ChunkyButton] để xếp cạnh nhau. */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 54.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    contentColor: Color = TextPrimary,
) {
    Box(modifier = modifier.height(height + ChunkyDepth)) {
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(height)
                .glass(shape, GlassFill, GlassStrokeStrong)
                .pressable(onClick = onClick, label = text),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/* ================================================================== *
 *  Thẻ điều hướng
 * ================================================================== */

/**
 * Thẻ lớn nền gradient ở Home. Chiều cao do nội dung quyết định: khối bóng
 * dùng [BoxScope.matchParentSize] rồi dịch xuống, nên không cần đo tay.
 */
@Composable
fun GradientNavCard(
    emoji: String,
    title: String,
    subtitle: String,
    top: Color,
    bottom: Color,
    shadow: Color,
    onContent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val depth = 8.dp

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            Modifier
                .matchParentSize()
                .padding(top = depth)
                .clip(CardShape)
                .background(shadow)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = depth)
                .offset(y = if (pressed) depth else 0.dp)
                .clip(CardShape)
                .background(linearGradientDeg(top, bottom))
                .pressable(onClick = onClick, interaction = interaction, label = title)
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, fontSize = 30.sp)
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = onContent,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = onContent.copy(alpha = 0.78f),
                )
            }
            Text(text = "›", fontSize = 22.sp, color = onContent.copy(alpha = 0.6f))
        }
    }
}

/* ================================================================== *
 *  Ô emoji
 * ================================================================== */

/**
 * Ô vuông chứa emoji hoặc ảnh. Khi chọn thì viền dày 3dp màu accent và nền
 * nhuộm cùng màu — đủ rõ để nhận ra giữa lưới dày.
 */
@Composable
fun EmojiTile(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    accent: Color = Lime,
    shape: Shape = MaterialTheme.shapes.large,
    square: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val base = modifier
        .then(if (square) Modifier.aspectRatio(1f) else Modifier)
        .clip(shape)
        // Lớp nền tối đặt trước lớp tint: ô có thể nằm trên panel hồng/cyan sáng,
        // nếu tint accent phủ trực tiếp lên đó thì màu ra nâu đục.
        .background(PanelBottom.copy(alpha = 0.62f))
        .background(if (selected) accent.copy(alpha = 0.20f) else GlassFillSoft)
        .border(
            width = if (selected) 3.dp else 1.dp,
            color = if (selected) accent else GlassStroke,
            shape = shape,
        )
    Box(
        modifier = if (onClick != null) base.pressable(onClick = onClick) else base,
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** Số thứ tự chọn, góc trên phải ô emoji. */
@Composable
fun BoxScope.OrderBadge(order: Int, accent: Color = Lime) {
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(6.dp)
            .size(20.dp)
            .clip(PillShape)
            .background(accent),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = order.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = OnLime,
        )
    }
}

/* ================================================================== *
 *  Chip, nhãn, đồng hồ
 * ================================================================== */

/** Chip vuông kính mờ; khi chọn thì nhuộm màu accent. */
@Composable
fun NeonChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Lime,
    fontSize: Int = 20,
) {
    Box(
        modifier = modifier
            .size(46.dp)
            .clip(MaterialTheme.shapes.small)
            .background(if (selected) accent.copy(alpha = 0.18f) else GlassFillSoft)
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) accent else GlassStroke,
                MaterialTheme.shapes.small,
            )
            .pressable(onClick = onClick, label = text),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, fontSize = fontSize.sp)
    }
}

/**
 * Nhãn khu vực: tiêu đề Baloo + dòng tiếng Anh mờ bên cạnh.
 * [centered] dùng khi nhãn nằm trong dialog — mọi thứ ở đó canh giữa.
 */
@Composable
fun SectionLabel(
    vi: String,
    en: String,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = if (centered) {
            Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        } else {
            Arrangement.spacedBy(8.dp)
        },
    ) {
        Text(text = vi, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Text(
            text = en,
            style = MaterialTheme.typography.labelSmall,
            color = TextFaint,
            modifier = Modifier.padding(bottom = 1.dp),
        )
    }
}

/** Hai dòng song ngữ canh giữa: tiếng Việt rõ hơn, tiếng Anh mờ bên dưới. */
@Composable
fun BilingualBody(vi: String, en: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = vi,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = TextAlign.Center,
        )
        Text(
            text = en,
            style = MaterialTheme.typography.bodyMedium,
            color = TextFaint,
            textAlign = TextAlign.Center,
        )
    }
}

/** Đồng hồ đếm ngược; đậm màu cảnh báo khi còn ít thời gian. */
@Composable
fun TimerChip(secondsLeft: Int, modifier: Modifier = Modifier) {
    val urgent = secondsLeft <= 5
    Box(
        modifier = modifier
            .widthIn(min = 64.dp)
            .height(42.dp)
            .clip(MaterialTheme.shapes.small)
            .background(Danger.copy(alpha = if (urgent) 0.28f else 0.18f))
            .border(1.dp, Danger.copy(alpha = 0.45f), MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "⏱ %02d".format(secondsLeft),
            style = MaterialTheme.typography.titleSmall,
            color = if (urgent) Danger else TimerText,
        )
    }
}

/* ================================================================== *
 *  Dialog
 * ================================================================== */

/**
 * Thân dialog: panel gradient bo 32dp, icon lớn, tiêu đề Baloo, nội dung
 * song ngữ, rồi hàng nút ở đáy.
 */
@Composable
fun NeonDialogBody(
    title: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    top: Color = PanelTop,
    bottom: Color = PanelBottom,
    stroke: Color = GlassStrokeStrong,
    body: (@Composable ColumnScope.() -> Unit)? = null,
    buttons: @Composable RowScope.() -> Unit,
) {
    GradientPanel(
        modifier = modifier.fillMaxWidth(),
        top = top,
        bottom = bottom,
        stroke = stroke,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            icon?.invoke()
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            body?.invoke(this)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                content = buttons,
            )
        }
    }
}

/** Ô icon bo tròn mềm đặt trên tiêu đề dialog. */
@Composable
fun DialogIcon(emoji: String, tint: Color) {
    Box(
        modifier = Modifier
            .size(74.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(tint.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, fontSize = 34.sp)
    }
}

/* ================================================================== *
 *  Tiện ích
 * ================================================================== */

/**
 * Vùng nhấn không ripple — thiết kế phản hồi bằng chuyển động lún của nút,
 * ripple của Material sẽ phá vệt gradient.
 */
@Composable
internal fun Modifier.pressable(
    onClick: () -> Unit,
    enabled: Boolean = true,
    label: String? = null,
    interaction: MutableInteractionSource = remember { MutableInteractionSource() },
): Modifier = clickable(
    interactionSource = interaction,
    indication = null,
    enabled = enabled,
    onClickLabel = label,
    onClick = onClick,
)
