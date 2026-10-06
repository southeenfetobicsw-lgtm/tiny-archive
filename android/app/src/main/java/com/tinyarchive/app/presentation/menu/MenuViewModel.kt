package com.tinyarchive.app.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinyarchive.app.domain.repository.ArchiveRepository
import com.tinyarchive.app.domain.usecase.GetStatsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MenuViewModel(
    private val archiveRepository: ArchiveRepository,
    private val getStats: GetStatsUseCase,
) : ViewModel() {

    val uiState: StateFlow<MenuUiState> = combine(
        archiveRepository.items,
        archiveRepository.categories,
    ) { items, categories ->
        val stats = getStats(items, categories)
        MenuUiState(
            itemCount = stats.itemCount,
            categoryCount = stats.categoryCount,
            noteCount = stats.noteCount,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MenuUiState(),
    )
}
