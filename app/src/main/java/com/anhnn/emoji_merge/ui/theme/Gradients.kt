package com.anhnn.emoji_merge.ui.theme

import android.graphics.Matrix
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Brush dựng lại đúng ngữ nghĩa gradient của CSS trong bản thiết kế.
 * Compose không có sẵn gradient theo góc hay radial hình ê-líp, mà thiết kế
 * dùng cả hai ở khắp nơi, nên hai helper này gánh phần đó.
 */

/**
 * Tương đương `linear-gradient(<deg>, ...)`: 0° hướng lên, tăng theo chiều kim đồng hồ,
 * độ dài đường gradient tính theo công thức của CSS nên màu cuối luôn chạm đúng góc hộp.
 */
fun linearGradientDeg(
    colors: List<Color>,
    degrees: Double = 120.0,
): Brush = object : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val rad = Math.toRadians(degrees)
        val dx = sin(rad)
        val dy = -cos(rad)
        val length = abs(size.width * dx) + abs(size.height * dy)
        val hx = (dx * length / 2).toFloat()
        val hy = (dy * length / 2).toFloat()
        val cx = size.width / 2f
        val cy = size.height / 2f
        return LinearGradientShader(
            from = Offset(cx - hx, cy - hy),
            to = Offset(cx + hx, cy + hy),
            colors = colors,
        )
    }
}

/** Dạng gọi hai màu — chiếm phần lớn lời gọi trong app. */
fun linearGradientDeg(from: Color, to: Color, degrees: Double = 120.0): Brush =
    linearGradientDeg(listOf(from, to), degrees)

/**
 * Tương đương `radial-gradient(<rx>% <ry>% at <cx>% <cy>%, ...)`.
 * Dựng shader tròn bán kính rx rồi co trục Y để thành ê-líp — cách duy nhất
 * để ra được vệt sáng dẹt ở đỉnh màn hình như thiết kế.
 */
fun radialGradientEllipse(
    vararg stops: Pair<Float, Color>,
    radiusXFraction: Float,
    radiusYFraction: Float,
    centerXFraction: Float = 0.5f,
    centerYFraction: Float = 0f,
): Brush = object : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val rx = (size.width * radiusXFraction).coerceAtLeast(1f)
        val ry = (size.height * radiusYFraction).coerceAtLeast(1f)
        val cx = size.width * centerXFraction
        val cy = size.height * centerYFraction
        return RadialGradient(
            cx, cy, rx,
            stops.map { it.second.toArgb() }.toIntArray(),
            stops.map { it.first }.toFloatArray(),
            Shader.TileMode.CLAMP,
        ).apply {
            setLocalMatrix(Matrix().apply { setScale(1f, ry / rx, cx, cy) })
        }
    }
}

/**
 * Nền của từng màn hình. Mỗi màn có một vệt sáng riêng để lúc chuyển màn
 * người dùng cảm nhận được mình đang đi sang khu vực khác.
 */
enum class ScreenBackdrop {
    /** Tím sâu, vệt sáng lớn giữa đỉnh. */
    Splash,

    /** Tím, vệt sáng lệch góc trên phải. */
    Home,

    /** Phẳng, tím than — dùng cho các màn có lưới dày. */
    Flat,

    /** Tím hoa cà rực, vệt sáng hạ thấp để tôn ảnh kết quả. */
    Result,

    /** Tím dịu hơn Result. */
    Detail,

    /** Xanh dương, vệt sáng góc trên trái. */
    Games,

    /** Hồng sen — game Đoán Emoji. */
    Guess,

    /** Xanh cyan — game Đố Chữ. */
    Word,
    ;

    val brush: Brush
        get() = when (this) {
            Splash -> radialGradientEllipse(
                0f to Color(0xFF4A1078), 0.55f to Color(0xFF1A0733), 1f to InkDeep,
                radiusXFraction = 1.2f, radiusYFraction = 0.7f,
            )
            Home -> radialGradientEllipse(
                0f to Color(0xFF3B1064), 0.60f to Color(0xFF1A0733), 1f to Ink,
                radiusXFraction = 1.0f, radiusYFraction = 0.45f, centerXFraction = 1f,
            )
            Flat -> linearGradientDeg(Color(0xFF1C0838), Ink, degrees = 180.0)
            Result -> radialGradientEllipse(
                0f to Color(0xFF5A1490), 0.55f to Color(0xFF23083F), 1f to Ink,
                radiusXFraction = 0.9f, radiusYFraction = 0.45f, centerYFraction = 0.18f,
            )
            Detail -> radialGradientEllipse(
                0f to Color(0xFF3E1170), 0.60f to Color(0xFF1C0838), 1f to Ink,
                radiusXFraction = 0.85f, radiusYFraction = 0.40f, centerYFraction = 0.12f,
            )
            Games -> radialGradientEllipse(
                0f to Color(0xFF0F3B6B), 0.55f to Color(0xFF1A0838), 1f to Ink,
                radiusXFraction = 1.0f, radiusYFraction = 0.40f, centerXFraction = 0f,
            )
            Guess -> radialGradientEllipse(
                0f to Color(0xFF6B1258), 0.55f to Color(0xFF260A3F), 1f to Ink,
                radiusXFraction = 0.9f, radiusYFraction = 0.40f,
            )
            Word -> radialGradientEllipse(
                0f to Color(0xFF12507F), 0.55f to Color(0xFF1A0F44), 1f to Ink,
                radiusXFraction = 0.9f, radiusYFraction = 0.40f,
            )
        }
}
