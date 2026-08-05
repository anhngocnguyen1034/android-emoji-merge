package com.anhnn.emoji_merge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.anhnn.emoji_merge.ui.nav.AppNav
import com.anhnn.emoji_merge.ui.theme.EmojimergeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EmojimergeTheme {
                AppNav()
            }
        }
    }
}
