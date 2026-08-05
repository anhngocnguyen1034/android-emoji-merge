package com.anhnn.emoji_merge.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.anhnn.emoji_merge.R
import com.anhnn.emoji_merge.ui.theme.BilingualBody
import com.anhnn.emoji_merge.ui.theme.ChunkyButton
import com.anhnn.emoji_merge.ui.theme.ChunkyStyle
import com.anhnn.emoji_merge.ui.theme.Danger
import com.anhnn.emoji_merge.ui.theme.DialogIcon
import com.anhnn.emoji_merge.ui.theme.GlassButton
import com.anhnn.emoji_merge.ui.theme.Lime
import com.anhnn.emoji_merge.ui.theme.NeonDialogBody
import com.anhnn.emoji_merge.ui.theme.PanelBottom
import com.anhnn.emoji_merge.ui.theme.PanelTopDanger
import com.anhnn.emoji_merge.ui.theme.SectionLabel
import com.anhnn.emoji_merge.ui.theme.glass

/**
 * Năm dialog của bản thiết kế. Tất cả dùng [Dialog] của Compose thay vì
 * AlertDialog để giữ được panel gradient và nút chunky.
 */

/** D1 — chặn Back ở Home. */
@Composable
fun ExitAppDialog(onDismiss: () -> Unit, onExit: () -> Unit) {
    NeonDialog(onDismiss = onDismiss) {
        NeonDialogBody(
            title = stringResource(R.string.dialog_exit_title),
            icon = { Text(text = "👋", fontSize = 44.sp) },
            body = {
                BilingualBody(
                    vi = stringResource(R.string.dialog_exit_body_vi),
                    en = stringResource(R.string.dialog_exit_body_en),
                )
            },
            buttons = {
                GlassButton(
                    text = stringResource(R.string.dialog_exit_confirm),
                    onClick = onExit,
                    modifier = Modifier.weight(1f),
                    height = 52.dp,
                )
                ChunkyButton(
                    text = stringResource(R.string.dialog_exit_stay),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1.4f),
                    height = 52.dp,
                )
            },
        )
    }
}

/** D2 — xác nhận xoá sticker. Panel đổi sang tông đỏ để báo hành động phá hủy. */
@Composable
fun DeleteStickerDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    NeonDialog(onDismiss = onDismiss) {
        NeonDialogBody(
            title = stringResource(R.string.dialog_delete_title),
            icon = { DialogIcon(emoji = "🗑️", tint = Danger) },
            top = PanelTopDanger,
            bottom = PanelBottom,
            stroke = Danger.copy(alpha = 0.35f),
            body = {
                BilingualBody(
                    vi = stringResource(R.string.dialog_delete_body_vi),
                    en = stringResource(R.string.dialog_delete_body_en),
                )
            },
            buttons = {
                GlassButton(
                    text = stringResource(R.string.dialog_delete_cancel),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    height = 52.dp,
                )
                ChunkyButton(
                    text = stringResource(R.string.dialog_delete_confirm),
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    style = ChunkyStyle.Destructive,
                    height = 52.dp,
                )
            },
        )
    }
}

/** D3 — tạm dừng khi bấm Back trong lúc chơi. */
@Composable
fun PauseGameDialog(onResume: () -> Unit, onQuit: () -> Unit) {
    NeonDialog(onDismiss = onResume) {
        NeonDialogBody(
            title = stringResource(R.string.dialog_pause_title),
            icon = { Text(text = "⏸️", fontSize = 44.sp) },
            body = {
                BilingualBody(
                    vi = stringResource(R.string.dialog_pause_body_vi),
                    en = stringResource(R.string.dialog_pause_body_en),
                )
            },
            buttons = {
                GlassButton(
                    text = stringResource(R.string.dialog_pause_quit),
                    onClick = onQuit,
                    modifier = Modifier.weight(1f),
                    height = 52.dp,
                )
                ChunkyButton(
                    text = stringResource(R.string.dialog_pause_resume),
                    onClick = onResume,
                    modifier = Modifier.weight(1.4f),
                    height = 52.dp,
                )
            },
        )
    }
}

/** D4 — thắng màn. */
@Composable
fun GameWonDialog(onNextLevel: () -> Unit) {
    NeonDialog(onDismiss = null) {
        NeonDialogBody(
            title = stringResource(R.string.dialog_win_title),
            icon = { Text(text = "🎉", fontSize = 44.sp) },
            body = {
                BilingualBody(
                    vi = stringResource(R.string.dialog_win_body_vi),
                    en = stringResource(R.string.dialog_win_body_en),
                )
            },
            buttons = {
                ChunkyButton(
                    text = stringResource(R.string.dialog_win_next),
                    onClick = onNextLevel,
                    modifier = Modifier.weight(1f),
                    height = 54.dp,
                )
            },
        )
    }
}

/**
 * D5 — thua màn. [answer] là dãy emoji đáp án, hiển thị trong một ô kính mờ
 * để người chơi thấy ngay mình đã trượt ở đâu.
 */
@Composable
fun GameLostDialog(
    answer: List<String>,
    onHome: () -> Unit,
    onRetry: () -> Unit,
) {
    NeonDialog(onDismiss = null) {
        NeonDialogBody(
            title = stringResource(R.string.dialog_lose_title),
            icon = { Text(text = "😅", fontSize = 40.sp) },
            body = {
                SectionLabel(
                    vi = stringResource(R.string.dialog_lose_answer_vi),
                    en = stringResource(R.string.dialog_lose_answer_en),
                    centered = true,
                )
                Row(
                    modifier = Modifier
                        .glass(MaterialTheme.shapes.medium)
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    answer.forEachIndexed { index, emoji ->
                        if (index > 0) {
                            Text(
                                text = "+",
                                style = MaterialTheme.typography.titleMedium,
                                color = Lime,
                            )
                        }
                        Text(text = emoji, fontSize = 34.sp)
                    }
                }
            },
            buttons = {
                GlassButton(
                    text = stringResource(R.string.dialog_lose_home),
                    onClick = onHome,
                    modifier = Modifier.weight(1f),
                    height = 54.dp,
                )
                ChunkyButton(
                    text = stringResource(R.string.dialog_lose_retry),
                    onClick = onRetry,
                    modifier = Modifier.weight(1.3f),
                    style = ChunkyStyle.Accent,
                    height = 54.dp,
                )
            },
        )
    }
}

/**
 * Khung dialog dùng chung. [onDismiss] để null khi bắt buộc người chơi phải
 * chọn một hành động (kết quả game) — lúc đó Back và chạm ra ngoài đều không tắt.
 */
@Composable
private fun NeonDialog(
    onDismiss: (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismiss?.invoke() },
        properties = DialogProperties(
            dismissOnBackPress = onDismiss != null,
            dismissOnClickOutside = onDismiss != null,
        ),
    ) {
        Row(Modifier.padding(horizontal = 8.dp)) { content() }
    }
}
