package com.tinyarchive.app.core.util

import android.view.View
import androidx.core.view.ViewCompat

object ViewExtensions {

    fun riseIn(target: View, delayMs: Long, distance: Float, durationMs: Long) {
        target.alpha = 0f
        target.translationY = distance
        target.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delayMs)
            .setDuration(durationMs)
            .start()
    }

    fun popIn(target: View, delayMs: Long, durationMs: Long) {
        target.alpha = 0f
        target.scaleX = 0.96f
        target.scaleY = 0.96f
        target.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(delayMs)
            .setDuration(durationMs)
            .start()
    }

    fun stopAnimations(vararg targets: View?) {
        targets.forEach { target ->
            target?.animate()?.cancel()
        }
    }

    fun describeState(target: View, state: String) {
        ViewCompat.setStateDescription(target, state)
    }
}
