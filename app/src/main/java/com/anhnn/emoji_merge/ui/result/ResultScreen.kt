package com.anhnn.emoji_merge.ui.result

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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anhnn.emoji_merge.R
import com.anhnn.emoji_merge.data.EmojiMerge
import com.anhnn.emoji_merge.ui.theme.ChunkyButton
import com.anhnn.emoji_merge.ui.theme.ChunkyStyle
import com.anhnn.emoji_merge.ui.theme.Cyan
import com.anhnn.emoji_merge.ui.theme.GlassButton
import com.anhnn.emoji_merge.ui.theme.GlassIconButton
import com.anhnn.emoji_merge.ui.theme.Lime
import com.anhnn.emoji_merge.ui.theme.Magenta
import com.anhnn.emoji_merge.ui.theme.NavBarSpacer
import com.anhnn.emoji_merge.ui.theme.NeonScreen
import com.anhnn.emoji_merge.ui.theme.NeonTopBar
import com.anhnn.emoji_merge.ui.theme.PanelTop
import com.anhnn.emoji_merge.ui.theme.ScreenBackdrop
import com.anhnn.emoji_merge.ui.theme.TextFaint
import com.anhnn.emoji_merge.ui.theme.TextPrimary
import com.anhnn.emoji_merge.ui.theme.linearGradientDeg
import com.anhnn.emoji_merge.util.saveMergeToGallery
import com.anhnn.emoji_merge.util.shareMerge
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    result: EmojiMerge,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onCreateNew: () -> Unit,
    onOpenCollection: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    val imageUrl = result.imageUrl()

    val savedMsg = stringResource(R.string.result_saved)
    val saveFailMsg = stringResource(R.string.result_save_failed)
    val shareFailMsg = stringResource(R.string.result_share_failed)

    NeonScreen(ScreenBackdrop.Result) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                NeonTopBar(
                    title = stringResource(R.string.result_title),
                    onBack = onBack,
                    trailing = {
                        GlassIconButton(
                            glyph = "🏠",
                            contentDescription = "Trang chủ",
                            onClick = onHome,
                            glyphSize = 16,
                        )
                    },
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = stringResource(R.string.result_headline),
                        style = MaterialTheme.typography.displaySmall,
                        color = TextPrimary,
                    )
                    Text(
                        text = stringResource(R.string.result_headline_en),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextFaint,
                    )

                    Spacer(Modifier.height(20.dp))
                    StickerStage(imageUrl = imageUrl)

                    Spacer(Modifier.height(18.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = result.emojiAChar, fontSize = 30.sp)
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.titleLarge,
                            color = Lime,
                        )
                        Text(text = result.emojiBChar, fontSize = 30.sp)
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
                                        context, imageUrl, "emoji_${result.mergeResult.hashCode()}"
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
                        text = stringResource(R.string.result_create_new),
                        onClick = onCreateNew,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    GlassButton(
                        text = stringResource(R.string.result_open_collection),
                        onClick = onOpenCollection,
                        modifier = Modifier.fillMaxWidth(),
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
}

/** Bục trưng bày sticker: ô lớn bo tròn, nền gradient nhạt để ảnh nổi lên. */
@Composable
fun StickerStage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 220.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(48.dp))
            .background(
                linearGradientDeg(
                    Magenta.copy(alpha = 0.24f),
                    Cyan.copy(alpha = 0.14f),
                    degrees = 140.0,
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
        )
    }
}
