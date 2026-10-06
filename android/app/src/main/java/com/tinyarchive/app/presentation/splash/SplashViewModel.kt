package com.tinyarchive.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinyarchive.app.core.config.GameConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val handOffState = MutableStateFlow(false)
    private val phaseState = MutableStateFlow(SplashPhase.OPENING)

    val handOffReady: StateFlow<Boolean> = handOffState.asStateFlow()
    val phase: StateFlow<SplashPhase> = phaseState.asStateFlow()

    private var started = false

    fun start() {
        if (started) {
            return
        }
        started = true
        viewModelScope.launch {
            runPhases()
        }
        viewModelScope.launch {
            delay(GameConfig.LOADER_DURATION_MS)
            handOffState.value = true
        }
    }

    private suspend fun runPhases() {
        phaseState.value = SplashPhase.OPENING
        delay(GameConfig.SPLASH_PHASE_MS)
        phaseState.value = SplashPhase.INDEXING
        delay(GameConfig.SPLASH_PHASE_MS)
        phaseState.value = SplashPhase.READY
    }

    fun consumeHandOff() {
        handOffState.value = false
    }
}
