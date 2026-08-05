package com.anhnn.emoji_merge.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Bo góc rất tròn theo kiểu game: ô/nút 16–22dp, thẻ 26–28dp, dialog 32–36dp.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),   // nút icon, chip
    medium = RoundedCornerShape(20.dp),  // nút chunky
    large = RoundedCornerShape(22.dp),   // ô emoji, ô hành động
    extraLarge = RoundedCornerShape(28.dp), // thẻ lớn
)

/** Vài bo góc ngoài thang Material mà thiết kế dùng riêng. */
val CardShape = RoundedCornerShape(26.dp)
val PanelShape = RoundedCornerShape(32.dp)
val DialogShape = RoundedCornerShape(36.dp)
val PillShape = RoundedCornerShape(999.dp)
