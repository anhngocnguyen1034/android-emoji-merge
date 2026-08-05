package com.anhnn.emoji_merge.ui.games

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.anhnn.emoji_merge.data.GameLevels
import com.anhnn.emoji_merge.data.GamePhase
import com.anhnn.emoji_merge.data.GamePrefs
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GuessUiState(
    val level: Int = 1,
    val target: String = "",
    val candidates: List<String> = emptyList(),
    val selected: List<String> = emptyList(),
    val answer: Set<String> = emptySet(),
    val timeLeftSec: Int = 60,
    val phase: GamePhase = GamePhase.Playing,
) {
    val canConfirm: Boolean get() = selected.size == 2 && phase == GamePhase.Playing
}

class GuessEmojiViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = GamePrefs(app)
    private val _state = MutableStateFlow(GuessUiState(level = prefs.guessEmojiLevel))
    val state = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadLevel()
    }

    private fun loadLevel() {
        timerJob?.cancel()
        val level = _state.value.level
        val lv = GameLevels.guessLevel(level)
        if (lv == null) {
            _state.update { it.copy(phase = GamePhase.Playing, target = "", candidates = emptyList()) }
            return
        }
        _state.update {
            it.copy(
                target = lv.targetUrl,
                candidates = lv.candidates,
                answer = lv.answer,
                selected = emptyList(),
                timeLeftSec = (GameLevels.LEVEL_TIME_MS / 1000).toInt(),
                phase = GamePhase.Playing,
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            var t = _state.value.timeLeftSec
            while (t > 0 && _state.value.phase == GamePhase.Playing) {
                delay(1000)
                t--
                _state.update { it.copy(timeLeftSec = t) }
            }
            if (t <= 0 && _state.value.phase == GamePhase.Playing) lose()
        }
    }

    fun toggle(char: String) {
        if (_state.value.phase != GamePhase.Playing) return
        val sel = _state.value.selected.toMutableList()
        when {
            sel.contains(char) -> sel.remove(char)
            sel.size < 2 -> sel.add(char)
        }
        _state.update { it.copy(selected = sel) }
    }

    /** Gọi sau khi màn hình đã hiện xong quảng cáo confirm. */
    fun confirm() {
        val s = _state.value
        if (s.selected.size < 2) return
        if (s.selected.toSet() == s.answer) win() else lose()
    }

    private fun win() {
        timerJob?.cancel()
        _state.update { it.copy(phase = GamePhase.Won) }
    }

    private fun lose() {
        timerJob?.cancel()
        _state.update { it.copy(phase = GamePhase.Lost) }
    }

    fun nextLevel() {
        val next = _state.value.level + 1
        prefs.guessEmojiLevel = next
        _state.update { it.copy(level = next) }
        loadLevel()
    }

    fun retry() {
        loadLevel()
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
