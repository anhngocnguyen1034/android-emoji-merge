package com.anhnn.emoji_merge.ui.games

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anhnn.emoji_merge.R
import com.anhnn.emoji_merge.data.GamePrefs
import com.anhnn.emoji_merge.ui.theme.Cyan
import com.anhnn.emoji_merge.ui.theme.CyanEnd
import com.anhnn.emoji_merge.ui.theme.CyanShadow
import com.anhnn.emoji_merge.ui.theme.GradientNavCard
import com.anhnn.emoji_merge.ui.theme.Magenta
import com.anhnn.emoji_merge.ui.theme.MagentaEnd
import com.anhnn.emoji_merge.ui.theme.MagentaShadow
import com.anhnn.emoji_merge.ui.theme.NavBarSpacer
import com.anhnn.emoji_merge.ui.theme.NeonScreen
import com.anhnn.emoji_merge.ui.theme.NeonTopBar
import com.anhnn.emoji_merge.ui.theme.OnCyan
import com.anhnn.emoji_merge.ui.theme.ScreenBackdrop
import com.anhnn.emoji_merge.ui.theme.SectionLabel
import com.anhnn.emoji_merge.ui.theme.TextFaint
import com.anhnn.emoji_merge.ui.theme.TextPrimary

@Composable
fun GamesHubScreen(
    onBack: () -> Unit,
    onOpenGuess: () -> Unit,
    onOpenWord: () -> Unit,
) {
    val context = LocalContext.current

    // Đọc level hiện tại một lần khi vào màn.
    val prefs = remember { GamePrefs(context) }
    val guessLevel = remember { prefs.guessEmojiLevel }
    val wordLevel = remember { prefs.wordPictogramLevel }

    NeonScreen(ScreenBackdrop.Games) {
        NeonTopBar(title = stringResource(R.string.games_title), onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
        ) {
            Spacer(Modifier.height(26.dp))
            Text(
                text = stringResource(R.string.games_headline),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
            )
            Text(
                text = stringResource(R.string.games_headline_en),
                style = MaterialTheme.typography.bodyMedium,
                color = TextFaint,
            )

            Spacer(Modifier.height(28.dp))
            SectionLabel(vi = "Chế độ", en = "Modes")
            Spacer(Modifier.height(12.dp))

            GradientNavCard(
                emoji = "🧩",
                title = stringResource(R.string.games_guess_title),
                subtitle = stringResource(R.string.games_guess_sub, guessLevel),
                top = Magenta,
                bottom = MagentaEnd,
                shadow = MagentaShadow,
                onContent = TextPrimary,
                onClick = onOpenGuess,
            )
            Spacer(Modifier.height(12.dp))
            GradientNavCard(
                emoji = "🔤",
                title = stringResource(R.string.games_word_title),
                subtitle = stringResource(R.string.games_word_sub, wordLevel),
                top = Cyan,
                bottom = CyanEnd,
                shadow = CyanShadow,
                onContent = OnCyan,
                onClick = onOpenWord,
            )

            Spacer(Modifier.weight(1f))
            NavBarSpacer()
        }
    }
}
