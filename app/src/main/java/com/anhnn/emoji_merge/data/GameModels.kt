package com.anhnn.emoji_merge.data

import com.google.gson.annotations.SerializedName

/** Một từ + 2 emoji (theo hex) biểu diễn nó, dùng cho game Word Pictogram. */
data class WordSpec(
    @SerializedName("w") val word: String = "",
    @SerializedName("h") val hexes: List<String> = emptyList(),
)

/** Một ô lựa chọn emoji trong Word Pictogram. */
data class WordCandidate(val hex: String, val char: String)

/** Một màn Guess Emoji: ảnh ghép là đề, đáp án là cặp emoji nguồn. */
data class GuessLevel(
    val targetUrl: String,
    val answer: Set<String>,
    val candidates: List<String>,
)

/** Một màn Word Pictogram: từ gợi ý + đáp án (thứ tự hex) + các ô emoji. */
data class WordLevel(
    val word: String,
    val answerHexes: List<String>,
    val candidates: List<WordCandidate>,
)

enum class GamePhase { Playing, Won, Lost }

/** Chuẩn hoá hex: chữ thường, bỏ khoảng trắng. */
fun normalizeHex(hex: String): String = hex.trim().lowercase()

/** Dựng chuỗi emoji từ hex codepoint (vd "2764-fe0f" -> "❤️", "1f31e" -> "🌞"). */
fun hexToEmoji(hex: String): String {
    val sb = StringBuilder()
    hex.split("-").forEach { part ->
        val cp = part.trim().toIntOrNull(16)
        if (cp != null) sb.appendCodePoint(cp)
    }
    return sb.toString()
}
