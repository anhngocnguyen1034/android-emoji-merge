package com.anhnn.emoji_merge.ui.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anhnn.emoji_merge.R
import com.anhnn.emoji_merge.data.EmojiMerge
import com.anhnn.emoji_merge.ui.theme.BilingualBody
import com.anhnn.emoji_merge.ui.theme.ChunkyButton
import com.anhnn.emoji_merge.ui.theme.EmojiTile
import com.anhnn.emoji_merge.ui.theme.Lime
import com.anhnn.emoji_merge.ui.theme.NavBarSpacer
import com.anhnn.emoji_merge.ui.theme.NeonScreen
import com.anhnn.emoji_merge.ui.theme.NeonTopBar
import com.anhnn.emoji_merge.ui.theme.OnLime
import com.anhnn.emoji_merge.ui.theme.ScreenBackdrop
import com.anhnn.emoji_merge.ui.theme.SectionLabel
import com.anhnn.emoji_merge.ui.theme.TextPrimary
import com.anhnn.emoji_merge.ui.theme.linearGradientDeg

@Composable
fun CollectionScreen(
    onBack: () -> Unit,
    onCreateNew: () -> Unit,
    onOpenDetail: (EmojiMerge) -> Unit,
    vm: CollectionViewModel = viewModel(),
) {
    val items by vm.items.collectAsStateWithLifecycle()

    // Nạp lại mỗi khi màn quay lại foreground (kể cả sau khi xoá ở Detail).
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.reload() }

    NeonScreen(ScreenBackdrop.Flat) {
        NeonTopBar(title = stringResource(R.string.collection_title), onBack = onBack)

        if (items.isEmpty()) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                EmptyState(onCreateNew = onCreateNew)
            }
        } else {
            Spacer(Modifier.height(18.dp))
            SectionLabel(
                vi = stringResource(R.string.collection_count_vi, items.size),
                en = stringResource(R.string.collection_count_en),
                modifier = Modifier.padding(horizontal = 22.dp),
            )
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 22.dp, end = 22.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { CreateNewTile(onClick = onCreateNew) }
                items(items) { saved ->
                    SavedTile(saved) {
                        onOpenDetail(saved)
                    }
                }
            }
        }

        NavBarSpacer()
    }
}

/** Ô mở màn Ghép — dùng gradient lime để nó là điểm sáng duy nhất trong lưới. */
@Composable
private fun CreateNewTile(onClick: () -> Unit) {
    EmojiTile(
        selected = true,
        accent = Lime,
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(linearGradientDeg(Lime.copy(alpha = 0.35f), Lime.copy(alpha = 0.10f))),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = "+", style = MaterialTheme.typography.displaySmall, color = Lime)
                Text(
                    text = stringResource(R.string.collection_new_tile),
                    style = MaterialTheme.typography.labelSmall,
                    color = Lime,
                )
            }
        }
    }
}

@Composable
private fun SavedTile(item: EmojiMerge, onClick: () -> Unit) {
    EmojiTile(onClick = onClick) {
        AsyncImage(
            model = item.imageUrl(),
            contentDescription = "${item.emojiAChar} + ${item.emojiBChar}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
        )
    }
}

@Composable
private fun EmptyState(onCreateNew: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "🗂️", fontSize = 52.sp)
        Text(
            text = stringResource(R.string.collection_empty_title),
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
        )
        BilingualBody(
            vi = stringResource(R.string.collection_empty_body_vi),
            en = stringResource(R.string.collection_empty_body_en),
        )
        Spacer(Modifier.height(6.dp))
        ChunkyButton(
            text = stringResource(R.string.collection_empty_cta),
            onClick = onCreateNew,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
