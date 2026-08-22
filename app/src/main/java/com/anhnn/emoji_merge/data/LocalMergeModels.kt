package com.anhnn.emoji_merge.data

import com.google.gson.annotations.SerializedName

data class LocalMergeSource(
    @SerializedName("id") val id: String = "",
    @SerializedName("label") val label: String = "",
    @SerializedName("drawable") val drawable: String = "",
)

typealias LocalMergeMap = Map<String, String>

/** Kết quả một lần ghép hợp lệ: hai ảnh nguồn + ảnh kết quả (drawable res). */
data class LocalMergeResult(
    val sourceA: LocalMergeSource,
    val sourceB: LocalMergeSource,
    // Tên drawable resource ảnh kết quả.
    val resultDrawable: String,
) {
    /** Key chuẩn để lưu/tra cứu kho lưu (không phân biệt thứ tự chọn). */
    val key: String get() = pairKey(sourceA.id, sourceB.id)

    companion object {
        /** Key chuẩn hoá cặp id: sắp xếp abc, phân tách bằng dấu gạch dưới kép. */
        fun pairKey(idA: String, idB: String): String =
            listOf(idA, idB).sorted().joinToString("__")
    }
}
