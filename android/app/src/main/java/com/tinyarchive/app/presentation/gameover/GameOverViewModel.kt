package com.tinyarchive.app.presentation.gameover

import androidx.lifecycle.ViewModel
import com.tinyarchive.app.domain.model.SessionSummary
import com.tinyarchive.app.domain.repository.ArchiveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(
    private val archiveRepository: ArchiveRepository,
) : ViewModel() {

    private val state = MutableStateFlow(GameOverUiState())

    val uiState: StateFlow<GameOverUiState> = state.asStateFlow()

    fun applySummary(summary: SessionSummary) {
        val items = archiveRepository.items.value
        val recent = summary.recentIds.mapNotNull { id ->
            items.firstOrNull { it.id == id }
        }
        state.value = GameOverUiState(
            itemsFiled = summary.itemsFiled,
            categoriesUsed = summary.categoriesUsed,
            notesWritten = summary.notesWritten,
            minutesSpent = summary.minutesSpent,
            recent = recent,
            categories = archiveRepository.categories.value,
        )
    }
}
