package com.anhnn.emoji_merge.data

import com.google.gson.annotations.SerializedName

/** Một emoji có thể chọn trong picker (asset `emoji_tabs.json`). */
data class EmojiTab(
    @SerializedName("c") val char: String = "",
    @SerializedName("h") val hex: String = "",
    @SerializedName("tn") val tabName: String = "",
)

/**
 * Một lượt ghép: hai emoji nguồn + đường dẫn ảnh kết quả (Emoji Kitchen).
 * Dùng chung cho picker (chỉ emoji_a_*) và cho bản ghi đã lưu.
 */
data class EmojiMerge(
    @SerializedName("ah") var emojiAHex: String = "",
    @SerializedName("bh") var emojiBHex: String = "",
    @SerializedName("ac") var emojiAChar: String = "",
    @SerializedName("bc") var emojiBChar: String = "",
    @SerializedName("re") var mergeResult: String = "",
    @SerializedName("tn") var tabName: String = "",
) {
    fun imageUrl(): String =
        "https://www.gstatic.com/android/keyboard/emojikitchen/$mergeResult"
}

/** Map merge Emoji Kitchen: leftHex -> (partnerHex -> đường dẫn ảnh tương đối). */
typealias EmojiKitchen = Map<String, Map<String, String>>
