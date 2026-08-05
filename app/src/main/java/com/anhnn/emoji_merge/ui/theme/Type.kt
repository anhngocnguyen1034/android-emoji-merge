package com.anhnn.emoji_merge.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.anhnn.emoji_merge.R

/**
 * Baloo 2 (tròn, mập) cho tiêu đề và nhãn nút — đây là phần lớn "chất teen"
 * của thiết kế. Be Vietnam Pro cho nội dung. Cả hai đều đã subset còn
 * Latin + tiếng Việt nên hiển thị đủ dấu.
 */
val Baloo = FontFamily(
    Font(R.font.baloo2_600, FontWeight.SemiBold),
    Font(R.font.baloo2_700, FontWeight.Bold),
    Font(R.font.baloo2_800, FontWeight.ExtraBold),
)

val Viet = FontFamily(
    Font(R.font.bevietnampro_400, FontWeight.Normal),
    Font(R.font.bevietnampro_500, FontWeight.Medium),
    Font(R.font.bevietnampro_600, FontWeight.SemiBold),
    Font(R.font.bevietnampro_700, FontWeight.Bold),
)

/** Tiêu đề Baloo 2 ExtraBold — dùng cho các bậc display và headline dưới đây. */
private fun baloo(size: Int, line: Int = (size * 1.15).toInt()) = TextStyle(
    fontFamily = Baloo,
    fontWeight = FontWeight.ExtraBold,
    fontSize = size.sp,
    lineHeight = line.sp,
)

private fun viet(size: Double, weight: FontWeight, line: Double = size * 1.45) = TextStyle(
    fontFamily = Viet,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
)

val Typography = Typography(
    displayLarge = baloo(40),
    displayMedium = baloo(34),
    displaySmall = baloo(30),
    // Tiêu đề màn hình lớn (Result: "Xong luôn!", dialog: "Thoát app?")
    headlineLarge = baloo(26),
    headlineMedium = baloo(24),
    // Tiêu đề thẻ điều hướng ở Home
    headlineSmall = baloo(23),
    // Tiêu đề trên top bar
    titleLarge = baloo(19),
    // Nhãn nút chunky
    titleMedium = baloo(18),
    titleSmall = baloo(16),
    bodyLarge = viet(15.0, FontWeight.Normal),
    bodyMedium = viet(13.5, FontWeight.Normal),
    bodySmall = viet(12.5, FontWeight.Normal),
    labelLarge = viet(15.0, FontWeight.Bold),
    labelMedium = viet(13.0, FontWeight.SemiBold),
    labelSmall = viet(11.0, FontWeight.SemiBold),
)
