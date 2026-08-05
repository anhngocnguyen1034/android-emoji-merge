package com.anhnn.emoji_merge.ui.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.anhnn.emoji_merge.R
import com.anhnn.emoji_merge.data.EmojiRepository
import com.anhnn.emoji_merge.ui.dialog.ExitAppDialog
import com.anhnn.emoji_merge.ui.theme.Cyan
import com.anhnn.emoji_merge.ui.theme.CyanEnd
import com.anhnn.emoji_merge.ui.theme.CyanShadow
import com.anhnn.emoji_merge.ui.theme.GradientNavCard
import com.anhnn.emoji_merge.ui.theme.Lime
import com.anhnn.emoji_merge.ui.theme.LimeEndSoft
import com.anhnn.emoji_merge.ui.theme.LimeShadow
import com.anhnn.emoji_merge.ui.theme.Magenta
import com.anhnn.emoji_merge.ui.theme.MagentaEnd
import com.anhnn.emoji_merge.ui.theme.MagentaShadow
import com.anhnn.emoji_merge.ui.theme.NavBarSpacer
import com.anhnn.emoji_merge.ui.theme.NeonScreen
import com.anhnn.emoji_merge.ui.theme.OnCyan
import com.anhnn.emoji_merge.ui.theme.OnLimeDeep
import com.anhnn.emoji_merge.ui.theme.PillShape
import com.anhnn.emoji_merge.ui.theme.ScreenBackdrop
import com.anhnn.emoji_merge.ui.theme.SectionLabel
import com.anhnn.emoji_merge.ui.theme.TextFaint
import com.anhnn.emoji_merge.ui.theme.TextPrimary
import com.anhnn.emoji_merge.ui.theme.linearGradientDeg

@Composable
fun HomeScreen(
    onOpenMerge: () -> Unit,
    onOpenGames: () -> Unit,
    onOpenCollection: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as Activity
    var showExit by remember { mutableStateOf(false) }

    // Số sticker đã lưu — đọc lại mỗi lần về foreground để nhãn thẻ luôn khớp.
    var savedCount by remember { mutableStateOf(EmojiRepository.store(context).all().size) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        savedCount = EmojiRepository.store(context).all().size
    }

    BackHandler { showExit = true }

    NeonScreen(ScreenBackdrop.Home) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
        ) {
            Spacer(Modifier.height(20.dp))
            Wordmark()

            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.home_greeting),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
            )
            Text(
                text = stringResource(R.string.home_greeting_en),
                style = MaterialTheme.typography.bodyMedium,
                color = TextFaint,
            )

            Spacer(Modifier.height(18.dp))
            MergePreviewCard()

            Spacer(Modifier.height(26.dp))
            SectionLabel(
                vi = stringResource(R.string.home_section_play_vi),
                en = stringResource(R.string.home_section_play_en),
            )
            Spacer(Modifier.height(12.dp))

            GradientNavCard(
                emoji = "🧪",
                title = stringResource(R.string.home_card_merge_title),
                subtitle = stringResource(R.string.home_card_merge_sub),
                top = Magenta,
                bottom = MagentaEnd,
                shadow = MagentaShadow,
                onContent = TextPrimary,
                onClick = onOpenMerge,
            )
            Spacer(Modifier.height(12.dp))
            GradientNavCard(
                emoji = "🎮",
                title = stringResource(R.string.home_card_games_title),
                subtitle = stringResource(R.string.home_card_games_sub),
                top = Cyan,
                bottom = CyanEnd,
                shadow = CyanShadow,
                onContent = OnCyan,
                onClick = onOpenGames,
            )
            Spacer(Modifier.height(12.dp))
            GradientNavCard(
                emoji = "🗂️",
                title = stringResource(R.string.home_card_collection_title),
                subtitle = if (savedCount == 0) {
                    stringResource(R.string.home_card_collection_sub_empty)
                } else {
                    stringResource(R.string.home_card_collection_sub, savedCount)
                },
                top = Lime,
                bottom = LimeEndSoft,
                shadow = LimeShadow,
                onContent = OnLimeDeep,
                onClick = onOpenCollection,
            )

            Spacer(Modifier.weight(1f))
            NavBarSpacer()
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showExit) {
        ExitAppDialog(
            onDismiss = { showExit = false },
            onExit = { activity.finish() },
        )
    }
}

/** Nhãn tên app dạng viên thuốc ở góc trên trái. */
@Composable
private fun Wordmark() {
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(Lime.copy(alpha = 0.14f))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "😀", fontSize = 15.sp)
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleSmall,
            color = Lime,
        )
    }
}

/**
 * Thẻ minh hoạ công thức ghép. Thuần trang trí — chỉ để người mới hiểu ngay
 * app làm gì, nên emoji để cứng trong code chứ không lấy từ dữ liệu.
 */
@Composable
private fun MergePreviewCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                linearGradientDeg(
                    Magenta.copy(alpha = 0.22f),
                    Cyan.copy(alpha = 0.14f),
                    degrees = 140.0,
                )
            )
            .padding(horizontal = 18.dp, vertical = 22.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "🐸", fontSize = 52.sp)
        Operator("+")
        Text(text = "😎", fontSize = 52.sp)
        Operator("=")
        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(TextPrimary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "🐸", fontSize = 46.sp)
            Text(
                text = "😎",
                fontSize = 30.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 4.dp, bottom = 2.dp),
            )
        }
    }
}

@Composable
private fun Operator(symbol: String) {
    Text(
        text = symbol,
        style = MaterialTheme.typography.titleLarge,
        color = Lime,
        modifier = Modifier.padding(horizontal = 12.dp),
    )
}
