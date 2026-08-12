package com.anhnn.emoji_merge.data

import android.content.Context
import androidx.core.content.edit

/**
 * Lưu tiến độ mini-game (thay cho `SharedPreferenceUtils` trong project tham khảo).
 * Mỗi game giữ level hiện tại; level sinh nội dung một cách tất định theo số level.
 */
class GamePrefs(context: Context) {

    private val prefs = context.getSharedPreferences("emoji_games", Context.MODE_PRIVATE)

    var guessEmojiLevel: Int
        get() = prefs.getInt(KEY_GUESS, 1)
        set(value) = prefs.edit { putInt(KEY_GUESS, value) }

    var wordPictogramLevel: Int
        get() = prefs.getInt(KEY_WORD, 1)
        set(value) = prefs.edit { putInt(KEY_WORD, value) }

    companion object {
        private const val KEY_GUESS = "guess_emoji_level"
        private const val KEY_WORD = "word_pictogram_level"
    }
}
