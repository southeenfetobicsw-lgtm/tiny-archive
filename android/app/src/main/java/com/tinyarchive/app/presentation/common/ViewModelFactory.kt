package com.tinyarchive.app.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.tinyarchive.app.core.di.ServiceLocator
import com.tinyarchive.app.presentation.game.GameViewModel
import com.tinyarchive.app.presentation.gameover.GameOverViewModel
import com.tinyarchive.app.presentation.menu.MenuViewModel
import com.tinyarchive.app.presentation.splash.SplashViewModel

object ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                archiveRepository = ServiceLocator.archiveRepository(),
                getStats = ServiceLocator.getStatsUseCase(),
            )
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                archiveRepository = ServiceLocator.archiveRepository(),
                settingsRepository = ServiceLocator.settingsRepository(),
                getItems = ServiceLocator.getItemsUseCase(),
                searchItems = ServiceLocator.searchItemsUseCase(),
                buildSummary = ServiceLocator.buildSessionSummaryUseCase(),
            )
            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                archiveRepository = ServiceLocator.archiveRepository(),
            )
            else -> throw IllegalArgumentException("Unknown ViewModel " + modelClass.name)
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }
}
