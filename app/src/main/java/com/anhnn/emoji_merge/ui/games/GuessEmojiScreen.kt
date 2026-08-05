package com.anhnn.emoji_merge.ui.games

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anhnn.emoji_merge.R
import com.anhnn.emoji_merge.data.GamePhase
import com.anhnn.emoji_merge.ui.dialog.GameLostDialog
import com.anhnn.emoji_merge.ui.dialog.GameWonDialog
import com.anhnn.emoji_merge.ui.dialog.PauseGameDialog
import com.anhnn.emoji_merge.ui.theme.BilingualBody
import com.anhnn.emoji_merge.ui.theme.ChunkyButton
import com.anhnn.emoji_merge.ui.theme.EmojiTile
import com.anhnn.emoji_merge.ui.theme.Magenta
import com.anhnn.emoji_merge.ui.theme.NavBarSpacer
import com.anhnn.emoji_merge.ui.theme.NeonScreen
import com.anhnn.emoji_merge.ui.theme.NeonTopBar
import com.anhnn.emoji_merge.ui.theme.OrderBadge
import com.anhnn.emoji_merge.ui.theme.ScreenBackdrop
import com.anhnn.emoji_merge.ui.theme.TimerChip
import com.anhnn.emoji_merge.ui.theme.linearGradientDeg

@Composable
fun GuessEmojiScreen(
    onBack: () -> Unit,
    vm: GuessEmojiViewModel = viewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var showPause by remember { mutableStateOf(false) }

    BackHandler { showPause = true }

    NeonScreen(ScreenBackdrop.Guess) {
        NeonTopBar(
            title = stringResource(R.string.guess_title, state.level),
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

            // Đề bài: ảnh cần đoán trên bục gradient hồng.
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .clip(RoundedCornerShape(44.dp))
                    .background(
                        linearGradientDeg(
                            Magenta.copy(alpha = 0.26f),
                            Magenta.copy(alpha = 0.08f),
                            degrees = 140.0,
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = state.target,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                )
            }

            Spacer(Modifier.height(16.dp))
            BilingualBody(
                vi = stringResource(R.string.guess_question_vi),
                en = stringResource(R.string.guess_question_en),
            )

            Spacer(Modifier.height(18.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.candidates) { char ->
                    val order = state.selected.indexOf(char)
                    EmojiTile(
                        selected = order >= 0,
                        onClick = { vm.toggle(char) },
                    ) {
                        Text(text = char, fontSize = 40.sp)
                        if (order >= 0) OrderBadge(order = order + 1)
                    }
                }
            }

            ChunkyButton(
                text = stringResource(R.string.game_confirm),
                onClick = { vm.confirm() },
                enabled = state.canConfirm,
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
            answer = state.answer.toList(),
            onHome = onBack,
            onRetry = { vm.retry() },
        )
        else -> Unit
    }
}
