package com.anhnn.emoji_merge.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * Kho lưu các bản ghép của người dùng dưới dạng file JSON (thay cho Room ở luồng gốc).
 * Tương đương `EmojiDatabase` trong project tham khảo.
 */
class SavedEmojiStore(private val file: File) {

    private val gson = Gson()
    private val listType = object : TypeToken<MutableList<EmojiMerge>>() {}.type

    fun all(): List<EmojiMerge> = load()

    fun add(item: EmojiMerge) {
        val items = load()
        items.add(item)
        save(items)
    }

    fun remove(mergeResult: String) {
        val items = load()
        items.removeAll { it.mergeResult == mergeResult }
        save(items)
    }

    /** True nếu chưa có bản ghép nào cho cùng ảnh kết quả. */
    fun isNew(item: EmojiMerge): Boolean =
        load().none { it.imageUrl() == item.imageUrl() }

    private fun load(): MutableList<EmojiMerge> {
        if (!file.exists()) return mutableListOf()
        return try {
            val json = file.readText()
            if (json.isBlank()) mutableListOf()
            else gson.fromJson(json, listType) ?: mutableListOf()
        } catch (e: Exception) {
            Log.e("SavedEmojiStore", "Không đọc được kho đã lưu", e)
            mutableListOf()
        }
    }

    private fun save(items: List<EmojiMerge>) {
        file.writeText(gson.toJson(items))
    }
}
