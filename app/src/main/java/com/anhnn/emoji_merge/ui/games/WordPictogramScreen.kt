package com.anhnn.emoji_merge.ui.games

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anhnn.emoji_merge.R
import com.anhnn.emoji_merge.data.GamePhase
import com.anhnn.emoji_merge.data.hexToEmoji
import com.anhnn.emoji_merge.ui.dialog.GameLostDialog
import com.anhnn.emoji_merge.ui.dialog.GameWonDialog
import com.anhnn.emoji_merge.ui.dialog.PauseGameDialog
import com.anhnn.emoji_merge.ui.theme.BilingualBody
import com.anhnn.emoji_merge.ui.theme.ChunkyButton
import com.anhnn.emoji_merge.ui.theme.ChunkyStyle
import com.anhnn.emoji_merge.ui.theme.Cyan
import com.anhnn.emoji_merge.ui.theme.EmojiTile
import com.anhnn.emoji_merge.ui.theme.NavBarSpacer
import com.anhnn.emoji_merge.ui.theme.NeonScreen
import com.anhnn.emoji_merge.ui.theme.NeonTopBar
import com.anhnn.emoji_merge.ui.theme.OrderBadge
import com.anhnn.emoji_merge.ui.theme.ScreenBackdrop
import com.anhnn.emoji_merge.ui.theme.TextFaint
import com.anhnn.emoji_merge.ui.theme.TextPrimary
import com.anhnn.emoji_merge.ui.theme.TimerChip
import com.anhnn.emoji_merge.ui.theme.linearGradientDeg

@Composable
fun WordPictogramScreen(
    onBack: () -> Unit,
    vm: WordPictogramViewModel = viewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var showPause by remember { mutableStateOf(false) }

    BackHandler { showPause = true }

    NeonScreen(ScreenBackdrop.Word) {
        NeonTopBar(
            title = stringResource(R.string.word_title, state.level),
            onBack = { showPause = true },
            trailing = { TimerChip(secondsLeft = state.timeLeftSec) },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(18.dp))

            // Từ gợi ý là nhân vật chính: chữ Baloo lớn trên panel cyan,
            // hai ô đáp án nằm ngay dưới trong cùng khung.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        linearGradientDeg(
                            Cyan.copy(alpha = 0.22f),
                            Cyan.copy(alpha = 0.07f),
                            degrees = 160.0,
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.word,
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(2) { index ->
                        val picked = state.selected.getOrNull(index)
                        EmojiTile(
                            modifier = Modifier.size(72.dp),
                            selected = picked != null,
                            accent = Cyan,
                        ) {
                            if (picked == null) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextFaint,
                                )
                            } else {
                                Text(text = picked.char, fontSize = 38.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            BilingualBody(
                vi = stringResource(R.string.word_question_vi),
                en = stringResource(R.string.word_question_en),
            )

            Spacer(Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.candidates) { candidate ->
                    val order = state.selected.indexOf(candidate)
                    EmojiTile(
                        selected = order >= 0,
                        accent = Cyan,
                        onClick = { vm.toggle(candidate) },
                    ) {
                        Text(text = candidate.char, fontSize = 40.sp)
                        if (order >= 0) OrderBadge(order = order + 1, accent = Cyan)
                    }
                }
            }

            ChunkyButton(
                text = stringResource(R.string.game_confirm),
                onClick = { vm.confirm() },
                enabled = state.canConfirm,
                style = ChunkyStyle.Cool,
                height = 60.dp,
                textStyle = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            NavBarSpacer()
        }
    }

    if (showPause) {
        PauseGameDialog(
            onResume = { showPause = false },
            onQuit = {
                showPause = false
                onBack()
            },
        )
    }

    when (state.phase) {
        GamePhase.Won -> GameWonDialog(onNextLevel = { vm.nextLevel() })
        GamePhase.Lost -> GameLostDialog(
            answer = state.answerHexes.map { hexToEmoji(it) },
            onHome = onBack,
            onRetry = { vm.retry() },
        )
        else -> Unit
    }
}
