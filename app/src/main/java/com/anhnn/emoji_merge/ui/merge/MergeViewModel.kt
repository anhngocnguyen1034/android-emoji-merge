package com.anhnn.emoji_merge.ui.merge

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.anhnn.emoji_merge.data.EmojiMerge
import com.anhnn.emoji_merge.data.EmojiRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MergeUiState(
    val slot1: EmojiMerge = EmojiMerge(),
    val slot2: EmojiMerge = EmojiMerge(),
    val activeSlot: Int = 1,
    val tabs: List<String> = emptyList(),
    val selectedTab: String = "",
    val grid: List<EmojiMerge> = emptyList(),
    val ready: Boolean = false,
)

sealed interface MergeEvent {
    data class NavigateResult(val result: EmojiMerge) : MergeEvent
    data object NoCombination : MergeEvent
}

class MergeViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(MergeUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<MergeEvent>()
    val events = _events.asSharedFlow()

    /** tabName -> emojis, cho tab đang hiển thị theo bộ lọc hiện tại. */
    private var grouped: Map<String, List<EmojiMerge>> = emptyMap()

    init {
        reset()
    }

    fun reset() {
        // Slot 1 active, picker hiện toàn bộ catalog.
        refilter(activeSlot = 1, partnerHex = "", base = MergeUiState())
    }

    /** Chọn slot đang thao tác; lọc picker theo emoji đã chọn ở slot còn lại. */
    fun selectSlot(index: Int) {
        val s = _state.value
        val partnerHex = if (index == 1) s.slot2.emojiAHex else s.slot1.emojiAHex
        refilter(index, partnerHex, s)
    }

    private fun refilter(activeSlot: Int, partnerHex: String, base: MergeUiState) {
        val (g, _) = EmojiRepository.tabsForPartner(partnerHex)
        grouped = g
        val tabs = g.keys.toList()
        val selectedTab = tabs.firstOrNull() ?: ""
        _state.value = base.copy(
            activeSlot = activeSlot,
            tabs = tabs,
            selectedTab = selectedTab,
            grid = g[selectedTab].orEmpty(),
        )
    }

    fun selectTab(tab: String) {
        _state.value = _state.value.copy(selectedTab = tab, grid = grouped[tab].orEmpty())
    }

    fun pickEmoji(e: EmojiMerge) {
        val s = _state.value
        if (s.activeSlot == 1) {
            val slot1 = s.slot1.copy(
                emojiAHex = e.emojiAHex, emojiAChar = e.emojiAChar, tabName = e.tabName
            )
            val base = s.copy(slot1 = slot1, ready = ready(slot1.emojiAHex, s.slot2.emojiAHex))
            // Tự chuyển sang slot 2, lọc picker theo emoji vừa chọn ở slot 1.
            refilter(2, slot1.emojiAHex, base)
        } else {
            val slot2 = s.slot2.copy(
                emojiAHex = e.emojiAHex, emojiAChar = e.emojiAChar, tabName = e.tabName
            )
            _state.value = s.copy(slot2 = slot2, ready = ready(s.slot1.emojiAHex, slot2.emojiAHex))
        }
    }

    private fun ready(h1: String, h2: String) = h1.isNotEmpty() && h2.isNotEmpty()

    /** Đường dẫn ảnh của emoji đang được active (để tô sáng ô đã chọn). */
    fun activeHex(): String {
        val s = _state.value
        return if (s.activeSlot == 1) s.slot1.emojiAHex else s.slot2.emojiAHex
    }

    fun attemptMerge() {
        val s = _state.value
        val h1 = s.slot1.emojiAHex
        val h2 = s.slot2.emojiAHex
        if (h1.isEmpty() || h2.isEmpty()) return

        viewModelScope.launch {
            val path = EmojiRepository.resolveMerge(h1, h2)
            if (path == null) {
                _events.emit(MergeEvent.NoCombination)
                return@launch
            }
            val result = EmojiMerge(
                emojiAHex = h1,
                emojiBHex = h2,
                emojiAChar = s.slot1.emojiAChar,
                emojiBChar = s.slot2.emojiAChar,
                mergeResult = path,
            )
            val store = EmojiRepository.store(getApplication())
            if (store.isNew(result)) store.add(result)
            _events.emit(MergeEvent.NavigateResult(result))
        }
    }
}
