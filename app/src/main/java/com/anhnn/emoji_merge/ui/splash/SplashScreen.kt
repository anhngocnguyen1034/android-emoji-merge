package com.anhnn.emoji_merge.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anhnn.emoji_merge.R
import com.anhnn.emoji_merge.data.EmojiRepository
import com.anhnn.emoji_merge.data.LocalMergeRepository
import com.anhnn.emoji_merge.ui.theme.GlassFillSoft
import com.anhnn.emoji_merge.ui.theme.Lime
import com.anhnn.emoji_merge.ui.theme.LimeEnd
import com.anhnn.emoji_merge.ui.theme.Magenta
import com.anhnn.emoji_merge.ui.theme.NeonScreen
import com.anhnn.emoji_merge.ui.theme.PillShape
import com.anhnn.emoji_merge.ui.theme.ScreenBackdrop
import com.anhnn.emoji_merge.ui.theme.TextFaint
import com.anhnn.emoji_merge.ui.theme.TextMuted
import com.anhnn.emoji_merge.ui.theme.linearGradientDeg
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onDone: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Nạp dữ liệu emoji + bộ ảnh ghép riêng, song song với splash tối thiểu.
        val emojiJob = launch { EmojiRepository.load(context) }
        val localJob = launch { LocalMergeRepository.load(context) }
        delay(1200)
        emojiJob.join()
        localJob.join()
        onDone()
    }

    NeonScreen(ScreenBackdrop.Splash) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Logo: ô lớn bo tròn với emoji chồng góc, giống ô kết quả ở màn Result.
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(RoundedCornerShape(56.dp))
                        .background(
                            linearGradientDeg(
                                Magenta.copy(alpha = 0.30f),
                                Lime.copy(alpha = 0.16f),
                                degrees = 140.0,
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "😀", fontSize = 96.sp)
                    Text(
                        text = "✨",
                        fontSize = 44.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 18.dp, bottom = 14.dp),
                    )
                }

                Spacer(Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayLarge.copy(
                        brush = Brush.linearGradient(listOf(Lime, LimeEnd))
                    ),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.splash_tagline_vi),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.splash_tagline_en),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextFaint,
                    textAlign = TextAlign.Center,
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                LoadingBar()
                Text(
                    text = stringResource(R.string.splash_loading),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextFaint,
                )
            }
        }
    }
}

/** Thanh tải chạy vô hạn — splash không biết tiến độ thật nên không giả vờ hiển thị %. */
@Composable
private fun LoadingBar() {
    val transition = rememberInfiniteTransition(label = "loading")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sweep",
    )

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(8.dp)
            .clip(PillShape)
            .background(GlassFillSoft),
    ) {
        Box(
            Modifier
                .fillMaxWidth(0.35f + 0.45f * progress)
                .height(8.dp)
                .clip(PillShape)
                .background(linearGradientDeg(Lime, LimeEnd, degrees = 90.0)),
        )
    }
}
