package com.anhnn.emoji_merge.ui.localmerge

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anhnn.emoji_merge.R
import com.anhnn.emoji_merge.data.LocalMergeRepository
import com.anhnn.emoji_merge.data.LocalMergeResult
import com.anhnn.emoji_merge.data.LocalMergeSource
import com.anhnn.emoji_merge.ui.theme.ChunkyButton
import com.anhnn.emoji_merge.ui.theme.Cyan
import com.anhnn.emoji_merge.ui.theme.EmojiTile
import com.anhnn.emoji_merge.ui.theme.GlassIconButton
import com.anhnn.emoji_merge.ui.theme.Lime
import com.anhnn.emoji_merge.ui.theme.Magenta
import com.anhnn.emoji_merge.ui.theme.NavBarSpacer
import com.anhnn.emoji_merge.ui.theme.NeonScreen
import com.anhnn.emoji_merge.ui.theme.NeonTopBar
import com.anhnn.emoji_merge.ui.theme.PanelTop
import com.anhnn.emoji_merge.ui.theme.ScreenBackdrop
import com.anhnn.emoji_merge.ui.theme.SectionLabel
import com.anhnn.emoji_merge.ui.theme.TextFaint
import com.anhnn.emoji_merge.ui.theme.TextMuted
import com.anhnn.emoji_merge.ui.theme.TextPrimary
import com.anhnn.emoji_merge.ui.theme.linearGradientDeg

@Composable
fun LocalMergeScreen(
    onBack: () -> Unit,
    onMerged: (LocalMergeResult) -> Unit,
    vm: LocalMergeViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val noComboMessage = stringResource(R.string.local_merge_no_combo)

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is LocalMergeEvent.NavigateResult -> onMerged(event.result)
                LocalMergeEvent.NoCombination -> snackbar.showSnackbar(noComboMessage)
            }
        }
    }

    NeonScreen(ScreenBackdrop.Flat) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                NeonTopBar(
                    title = stringResource(R.string.local_merge_title),
                    onBack = onBack,
                    trailing = {
                        GlassIconButton(
                            glyph = "↺",
                            contentDescription = stringResource(R.string.merge_reset),
                            onClick = { vm.reset() },
                        )
                    },
                )

                if (!state.loaded) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.local_merge_loading),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                        )
                    }
                    NavBarSpacer()
                    return@NeonScreen
                }

                // Bàn ghép: hai ô nguyên liệu + dấu cộng.
                Row(
                    modifier = Modifier
                        .padding(horizontal = 22.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            linearGradientDeg(
                                Magenta.copy(alpha = 0.20f),
                                Cyan.copy(alpha = 0.12f),
                                degrees = 140.0,
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Slot(
                        label = stringResource(R.string.local_merge_slot_1),
                        source = state.slot1,
                        active = state.activeSlot == 1,
                        modifier = Modifier.weight(1f),
                        onClick = { vm.selectSlot(1) },
                    )
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.displaySmall,
                        color = Lime,
                        modifier = Modifier.padding(bottom = 22.dp),
                    )
                    Slot(
                        label = stringResource(R.string.local_merge_slot_2),
                        source = state.slot2,
                        active = state.activeSlot == 2,
                        modifier = Modifier.weight(1f),
                        onClick = { vm.selectSlot(2) },
                    )
                }

                Spacer(Modifier.height(16.dp))
                ChunkyButton(
                    text = stringResource(R.string.local_merge_action),
                    onClick = { vm.attemptMerge() },
                    enabled = state.ready,
                    height = 60.dp,
                    textStyle = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 22.dp)
                        .fillMaxWidth(),
                )

                Spacer(Modifier.height(22.dp))
                SectionLabel(
                    vi = stringResource(R.string.local_merge_section_pick_vi),
                    en = stringResource(R.string.local_merge_section_pick_en),
                    modifier = Modifier.padding(horizontal = 22.dp),
                )
                Spacer(Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.sources) { source ->
                        SourceCell(
                            source = source,
                            selected = source.id == state.slot1?.id ||
                                source.id == state.slot2?.id,
                            onClick = { vm.pick(source) },
                        )
                    }
                }

                NavBarSpacer()
                Spacer(Modifier.height(8.dp))
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

/** Ô nguyên liệu kèm nhãn; nhãn sáng lên khi slot đang được chọn. */
@Composable
private fun Slot(
    label: String,
    source: LocalMergeSource?,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        EmojiTile(
            modifier = Modifier.fillMaxWidth(),
            selected = active,
            shape = RoundedCornerShape(26.dp),
            onClick = onClick,
        ) {
            if (source == null) {
                Text(
                    text = stringResource(R.string.local_merge_slot_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextFaint,
                )
            } else {
                val res = LocalMergeRepository.drawableId(context, source.drawable)
                if (res != 0) {
                    Image(
                        painter = painterResource(res),
                        contentDescription = source.label,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) Lime else TextMuted,
        )
    }
}

/** Ô trong lưới chọn ảnh nguồn. */
@Composable
private fun SourceCell(
    source: LocalMergeSource,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val res = LocalMergeRepository.drawableId(context, source.drawable)
    EmojiTile(
        selected = selected,
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
    ) {
        if (res != 0) {
            Image(
                painter = painterResource(res),
                contentDescription = source.label,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}
