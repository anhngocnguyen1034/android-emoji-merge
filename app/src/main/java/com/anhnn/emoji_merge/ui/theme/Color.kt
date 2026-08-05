package com.anhnn.emoji_merge.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Bảng màu "neon candy" — nền tím đêm, hai màu nhấn nóng (lime + magenta),
 * accent phụ theo từng khu vực (cyan cho game chữ, đỏ cho nguy hiểm, vàng cho sao).
 * Chỉ có tông tối: thiết kế không có biến thể sáng.
 */

// ---- Nền ----
val Ink = Color(0xFF12051F)          // đáy của mọi gradient
val InkDeep = Color(0xFF10041F)
val InkDialog = Color(0xFF080312)    // lớp phủ sau dialog (dùng với alpha .86)
val PanelTop = Color(0xFF2A1050)     // đỉnh gradient của panel dialog
val PanelTopDanger = Color(0xFF3A0F2A)
val PanelBottom = Color(0xFF1A0733)

// ---- Lime: hành động chính ----
val Lime = Color(0xFFC8FF4D)
val LimeEnd = Color(0xFF7BE83B)
val LimeEndSoft = Color(0xFF63D93A)
val LimeShadow = Color(0xFF4E8C13)
val OnLime = Color(0xFF16290A)
val OnLimeDeep = Color(0xFF1D3305)

// ---- Magenta: hành động phụ / thẻ Ghép ----
val Magenta = Color(0xFFFF4FA3)
val MagentaEnd = Color(0xFFB02BD6)
val MagentaShadow = Color(0xB3781060)   // rgba(120,16,90,.7)

// ---- Cyan: Mini Games / game đố chữ ----
val Cyan = Color(0xFF38E1FF)
val CyanEnd = Color(0xFF3B6BFF)
val CyanShadow = Color(0xB30F3A8C)
val OnCyan = Color(0xFF08243F)

// ---- Đỏ: xoá / đồng hồ ----
val Danger = Color(0xFFFF4F6E)
val DangerEnd = Color(0xFFD31F52)
val DangerShadow = Color(0xFF7A0F2C)
val TimerText = Color(0xFFFF849B)

// ---- Vàng: sao, thành tích ----
val Gold = Color(0xFFFFC53D)

// ---- Chữ ----
val TextPrimary = Color(0xFFFFFFFF)
val TextMuted = Color(0xFFA79BC4)     // dòng phụ tiếng Việt
val TextFaint = Color(0xFF7C6F9C)     // dòng tiếng Anh
val TextCaption = Color(0xFF8C7FAE)
val TextLavender = Color(0xFFCBBFEA)
val TextIceBlue = Color(0xFFB9C9E8)

// ---- Kính mờ: bề mặt phổ biến nhất của thiết kế ----
val GlassFill = Color(0x14FFFFFF)      // rgba(255,255,255,.08)
val GlassFillSoft = Color(0x12FFFFFF)  // rgba(255,255,255,.07)
val GlassStroke = Color(0x1FFFFFFF)    // rgba(255,255,255,.12)
val GlassStrokeStrong = Color(0x29FFFFFF)
