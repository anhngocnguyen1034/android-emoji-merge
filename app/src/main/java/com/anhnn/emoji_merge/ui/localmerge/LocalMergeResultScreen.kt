package com.anhnn.emoji_merge.ui.localmerge

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anhnn.emoji_merge.R
import com.anhnn.emoji_merge.data.LocalMergeRepository
import com.anhnn.emoji_merge.data.LocalMergeResult
import com.anhnn.emoji_merge.ui.theme.ChunkyButton
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
import com.anhnn.emoji_merge.ui.theme.TextMuted
import com.anhnn.emoji_merge.ui.theme.TextPrimary
import com.anhnn.emoji_merge.ui.theme.linearGradientDeg

@Composable
fun LocalMergeResultScreen(
    result: LocalMergeResult,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onCreateNew: () -> Unit,
) {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }

    val resId = LocalMergeRepository.drawableId(context, result.resultDrawable)

    NeonScreen(ScreenBackdrop.Result) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                NeonTopBar(
                    title = stringResource(R.string.local_result_title),
                    onBack = onBack,
                    trailing = {
                        GlassIconButton(
                            glyph = "🏠",
                            contentDescription = stringResource(R.string.result_home_desc),
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
                        text = stringResource(R.string.local_result_headline),
                        style = MaterialTheme.typography.displaySmall,
                        color = TextPrimary,
                    )
                    Text(
                        text = stringResource(R.string.local_result_headline_en),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextFaint,
                    )

                    Spacer(Modifier.height(20.dp))
                    ResultStage(resId = resId, sourceLabel = result.sourceA.label)

                    Spacer(Modifier.height(18.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = result.sourceA.label, fontSize = 15.sp, color = TextMuted)
                        Text(text = "+", style = MaterialTheme.typography.titleLarge, color = Lime)
                        Text(text = result.sourceB.label, fontSize = 15.sp, color = TextMuted)
                    }

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = stringResource(R.string.local_result_unsaved_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextFaint,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(10.dp))

                    ChunkyButton(
                        text = stringResource(R.string.local_result_create_new),
                        onClick = onCreateNew,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(10.dp))
                    GlassButton(
                        text = stringResource(R.string.local_result_back),
                        onClick = onBack,
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

@Composable
private fun ResultStage(resId: Int, sourceLabel: String) {
    Box(
        modifier = Modifier
            .size(240.dp)
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
        if (resId != 0) {
            Image(
                painter = painterResource(resId),
                contentDescription = sourceLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                contentScale = ContentScale.Fit,
            )
        } else {
            Text(
                text = stringResource(R.string.local_result_missing),
                style = MaterialTheme.typography.bodyMedium,
                color = TextFaint,
            )
        }
    }
}
