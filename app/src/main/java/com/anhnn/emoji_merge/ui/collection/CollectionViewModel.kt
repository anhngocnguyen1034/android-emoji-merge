package com.anhnn.emoji_merge.ui.collection

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.anhnn.emoji_merge.data.EmojiMerge
import com.anhnn.emoji_merge.data.EmojiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CollectionViewModel(app: Application) : AndroidViewModel(app) {

    private val store = EmojiRepository.store(app)

    // Nạp ngay trong init: nếu để ON_RESUME nạp thì lần compose đầu vẫn còn rỗng
    // -> màn hình nháy EmptyState một nhịp rồi mới hiện lưới.
    private val _items = MutableStateFlow(load())
    val items = _items.asStateFlow()

    fun reload() {
        _items.value = load()
    }

    private fun load(): List<EmojiMerge> = store.all().reversed() // mới nhất lên đầu

    fun delete(item: EmojiMerge) {
        store.remove(item.mergeResult)
        reload()
    }
}
