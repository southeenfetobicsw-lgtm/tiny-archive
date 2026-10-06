package com.tinyarchive.app.presentation.gameover

import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.Category

data class GameOverUiState(
    val itemsFiled: Int = 0,
    val categoriesUsed: Int = 0,
    val notesWritten: Int = 0,
    val minutesSpent: Int = 0,
    val recent: List<ArchiveItem> = emptyList(),
    val categories: List<Category> = emptyList(),
)
