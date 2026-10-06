package com.tinyarchive.app.presentation.splash

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.tinyarchive.app.databinding.FragmentSplashBinding

class SplashAnimator(private val density: Float) {

    private val loops = mutableListOf<ValueAnimator>()
    private val entrances = mutableListOf<View>()

    fun playEntrance(binding: FragmentSplashBinding) {
        prepare(binding)
        enterCard(binding.cardSplashArt)
        enterTitle(binding.txtSplashTitle)
        enterRule(binding.viewSplashRule)
        enterSubtitle(binding.txtSplashSubtitle)
        enterProgress(binding.progressSplash)
        enterPhase(binding.txtSplashPhase)
        enterFooter(binding.txtSplashFooter)
    }

    fun startLoops(binding: FragmentSplashBinding) {
        loopHairline(binding.viewSplashRule)
        loopDrawer(binding.cardSplashArt)
        loopScrim(binding.viewSplashScrim)
    }

    private fun prepare(binding: FragmentSplashBinding) {
        entrances.clear()
        entrances.add(binding.cardSplashArt)
        entrances.add(binding.txtSplashTitle)
        entrances.add(binding.viewSplashRule)
        entrances.add(binding.txtSplashSubtitle)
        entrances.add(binding.progressSplash)
        entrances.add(binding.txtSplashPhase)
        entrances.add(binding.txtSplashFooter)
        entrances.forEach { target ->
            target.alpha = 0f
        }
    }

    private fun enterCard(target: View) {
        target.scaleX = 0.82f
        target.scaleY = 0.82f
        target.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(120L)
            .setDuration(620L)
            .setInterpolator(DecelerateInterpolator(1.6f))
            .start()
    }

    private fun enterTitle(target: View) {
        target.translationY = 26f * density
        target.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(420L)
            .setDuration(520L)
            .setInterpolator(DecelerateInterpolator(1.4f))
            .start()
    }

    private fun enterRule(target: View) {
        target.scaleX = 0.2f
        target.animate()
            .alpha(1f)
            .scaleX(1f)
            .setStartDelay(560L)
            .setDuration(480L)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun enterSubtitle(target: View) {
        target.animate()
            .alpha(1f)
            .setStartDelay(700L)
            .setDuration(420L)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun enterProgress(target: View) {
        target.translationY = 12f * density
        target.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(880L)
            .setDuration(420L)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun enterPhase(target: View) {
        target.animate()
            .alpha(1f)
            .setStartDelay(1040L)
            .setDuration(420L)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun enterFooter(target: View) {
        target.animate()
            .alpha(1f)
            .setStartDelay(1200L)
            .setDuration(520L)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun loopHairline(target: View) {
        val animator = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.2f, 1f)
        animator.duration = 2200L
        animator.startDelay = 1100L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.interpolator = LinearInterpolator()
        animator.start()
        loops.add(animator)
    }

    private fun loopDrawer(target: View) {
        val animator = ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, 0f, -6f * density)
        animator.duration = 2600L
        animator.startDelay = 900L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = DecelerateInterpolator(1.2f)
        animator.start()
        loops.add(animator)
    }

    private fun loopScrim(target: View) {
        val animator = ObjectAnimator.ofFloat(target, View.ALPHA, 1f, 0.86f)
        animator.duration = 3400L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = LinearInterpolator()
        animator.start()
        loops.add(animator)
    }

    fun cancel() {
        loops.forEach { animator ->
            animator.cancel()
        }
        loops.clear()
        entrances.forEach { target ->
            target.animate().cancel()
        }
        entrances.clear()
    }
}
