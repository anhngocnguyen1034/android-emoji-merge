package com.anhnn.emoji_merge.ui.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anhnn.emoji_merge.R
import com.anhnn.emoji_merge.data.EmojiRepository
import com.anhnn.emoji_merge.ui.dialog.DeleteStickerDialog
import com.anhnn.emoji_merge.ui.result.StickerStage
import com.anhnn.emoji_merge.ui.theme.ChunkyButton
import com.anhnn.emoji_merge.ui.theme.ChunkyStyle
import com.anhnn.emoji_merge.ui.theme.GlassButton
import com.anhnn.emoji_merge.ui.theme.Lime
import com.anhnn.emoji_merge.ui.theme.NavBarSpacer
import com.anhnn.emoji_merge.ui.theme.NeonScreen
import com.anhnn.emoji_merge.ui.theme.NeonTopBar
import com.anhnn.emoji_merge.ui.theme.PanelTop
import com.anhnn.emoji_merge.ui.theme.ScreenBackdrop
import com.anhnn.emoji_merge.ui.theme.TextPrimary
import com.anhnn.emoji_merge.util.saveMergeToGallery
import com.anhnn.emoji_merge.util.shareMerge
import kotlinx.coroutines.launch

/**
 * @param mergeResult id của bản ghép trong kho đã lưu. Dùng route arg thay vì biến tạm
 *   trong repository để màn hình vẫn dựng lại đúng sau khi process bị hệ thống kill.
 */
@Composable
fun EmojiDetailScreen(mergeResult: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    val store = remember(context) { EmojiRepository.store(context) }
    val item = remember(store, mergeResult) {
        store.all().firstOrNull { it.mergeResult == mergeResult }
    }

    // Id không còn trong kho (đã xoá ở nơi khác, hoặc route thiếu arg) → quay lại.
    if (item == null) {
        LaunchedEffect(mergeResult) { onBack() }
        return
    }

    val imageUrl = item.imageUrl()
    var showDelete by remember { mutableStateOf(false) }

    val savedMsg = stringResource(R.string.result_saved)
    val saveFailMsg = stringResource(R.string.result_save_failed)
    val shareFailMsg = stringResource(R.string.result_share_failed)

    NeonScreen(ScreenBackdrop.Detail) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                NeonTopBar(title = stringResource(R.string.detail_title), onBack = onBack)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(24.dp))
                    StickerStage(imageUrl = imageUrl)

                    if (item.emojiAChar.isNotEmpty()) {
                        Spacer(Modifier.height(18.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = item.emojiAChar, fontSize = 30.sp)
                            Text(
                                text = "+",
                                style = MaterialTheme.typography.titleLarge,
                                color = Lime,
                            )
                            Text(text = item.emojiBChar, fontSize = 30.sp)
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ChunkyButton(
                            text = "⬇  ${stringResource(R.string.result_save)}",
                            onClick = {
                                scope.launch {
                                    val ok = saveMergeToGallery(
                                        context, imageUrl, "emoji_${item.mergeResult.hashCode()}"
                                    )
                                    snackbar.showSnackbar(if (ok) savedMsg else saveFailMsg)
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )
                        ChunkyButton(
                            text = "↗  ${stringResource(R.string.result_share)}",
                            onClick = {
                                scope.launch {
                                    runCatching { shareMerge(context, imageUrl) }
                                        .onFailure { snackbar.showSnackbar(shareFailMsg) }
                                }
                            },
                            style = ChunkyStyle.Accent,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    GlassButton(
                        text = "🗑  ${stringResource(R.string.detail_delete)}",
                        onClick = { showDelete = true },
                        modifier = Modifier.fillMaxWidth(),
                        contentColor = MaterialTheme.colorScheme.error,
                    )
                    Spacer(Modifier.height(12.dp))
                }

                NavBarSpacer()
            }

            SnackbarHost(
                hostState = snackbar,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 22.dp, vertical = 24.dp),
            ) { data ->
                Snackbar(
                    shape = MaterialTheme.shapes.medium,
                    containerColor = PanelTop,
                    contentColor = TextPrimary,
                ) {
                    Text(data.visuals.message, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (showDelete) {
        DeleteStickerDialog(
            onDismiss = { showDelete = false },
            onConfirm = {
                store.remove(item.mergeResult)
                showDelete = false
                onBack()
            },
        )
    }
}
