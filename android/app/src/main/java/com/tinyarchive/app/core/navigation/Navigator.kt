package com.tinyarchive.app.core.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.tinyarchive.app.R
import com.tinyarchive.app.domain.model.SessionSummary
import com.tinyarchive.app.presentation.game.GameFragment
import com.tinyarchive.app.presentation.gameover.GameOverFragment
import com.tinyarchive.app.presentation.menu.MenuFragment
import com.tinyarchive.app.presentation.splash.SplashFragment

class Navigator(
    private val fragmentManager: FragmentManager,
    private val containerId: Int,
) {

    fun showSplash(): Boolean = replace(
        fragment = SplashFragment(),
        enter = R.anim.fade_in,
        exit = R.anim.fade_out,
        tag = TAG_SPLASH,
        addToBackStack = false,
    )

    fun showMenu(): Boolean = replace(
        fragment = MenuFragment(),
        enter = R.anim.fade_in,
        exit = R.anim.fade_out,
        tag = TAG_MENU,
        addToBackStack = false,
    )

    fun showArchive(): Boolean = replace(
        fragment = GameFragment(),
        enter = R.anim.slide_in_right,
        exit = R.anim.fade_out,
        tag = TAG_ARCHIVE,
        addToBackStack = true,
    )

    fun showSummary(summary: SessionSummary): Boolean = replace(
        fragment = GameOverFragment.newInstance(summary),
        enter = R.anim.slide_in_up,
        exit = R.anim.fade_out,
        tag = TAG_SUMMARY,
        addToBackStack = true,
    )

    fun back(): Boolean {
        if (fragmentManager.isStateSaved) {
            return false
        }
        if (fragmentManager.backStackEntryCount == 0) {
            return false
        }
        fragmentManager.popBackStack()
        return true
    }

    fun backToMenu(): Boolean {
        if (fragmentManager.isStateSaved) {
            return false
        }
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        return true
    }

    private fun replace(
        fragment: Fragment,
        enter: Int,
        exit: Int,
        tag: String,
        addToBackStack: Boolean,
    ): Boolean {
        if (fragmentManager.isStateSaved) {
            return false
        }
        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(enter, exit, enter, exit)
        transaction.replace(containerId, fragment, tag)
        if (addToBackStack) {
            transaction.addToBackStack(tag)
        }
        transaction.commit()
        return true
    }

    private companion object {
        const val TAG_SPLASH = "splash"
        const val TAG_MENU = "menu"
        const val TAG_ARCHIVE = "archive"
        const val TAG_SUMMARY = "summary"
    }
}
