package com.anhnn.emoji_merge.ui.localmerge

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.anhnn.emoji_merge.data.LocalMergeRepository
import com.anhnn.emoji_merge.data.LocalMergeResult
import com.anhnn.emoji_merge.data.LocalMergeSource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LocalMergeUiState(
    val sources: List<LocalMergeSource> = emptyList(),
    val slot1: LocalMergeSource? = null,
    val slot2: LocalMergeSource? = null,
    val activeSlot: Int = 1,
    val loaded: Boolean = false,
    val ready: Boolean = false,
)

sealed interface LocalMergeEvent {
    data class NavigateResult(val result: LocalMergeResult) : LocalMergeEvent
    data object NoCombination : LocalMergeEvent
}

class LocalMergeViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(LocalMergeUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<LocalMergeEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            LocalMergeRepository.load(app)
            val sources = LocalMergeRepository.sources
            _state.value = LocalMergeUiState(sources = sources, loaded = true)
        }
    }

    /** Chọn slot đang thao tác. */
    fun selectSlot(index: Int) {
        if (index == _state.value.activeSlot) return
        _state.value = _state.value.copy(activeSlot = index)
    }

    /** Chọn một ảnh nguồn vào slot đang active, rồi tự chuyển sang slot còn lại. */
    fun pick(source: LocalMergeSource) {
        val s = _state.value
        if (s.activeSlot == 1) {
            _state.value = s.copy(
                slot1 = source,
                activeSlot = 2,
                ready = s.slot2 != null,
            )
        } else {
            _state.value = s.copy(
                slot2 = source,
                activeSlot = 1,
                ready = s.slot1 != null,
            )
        }
    }

    fun reset() {
        _state.value = LocalMergeUiState(
            sources = _state.value.sources,
            loaded = _state.value.loaded,
            activeSlot = 1,
        )
    }

    fun attemptMerge() {
        val s = _state.value
        val a = s.slot1 ?: return
        val b = s.slot2 ?: return
        viewModelScope.launch {
            val result = LocalMergeRepository.resolve(a, b)
            if (result == null) {
                _events.emit(LocalMergeEvent.NoCombination)
            } else {
                _events.emit(LocalMergeEvent.NavigateResult(result))
            }
        }
    }
}
