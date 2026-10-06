package com.tinyarchive.app.presentation.game

import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.Category

data class GameUiState(
    val visible: List<ArchiveItem> = emptyList(),
    val categories: List<Category> = emptyList(),
    val totalCount: Int = 0,
    val noteCount: Int = 0,
    val query: String = "",
    val selectedCategoryId: String? = null,
    val showNotePreview: Boolean = true,
    val compactRows: Boolean = false,
    val unreadable: Boolean = false,
)
