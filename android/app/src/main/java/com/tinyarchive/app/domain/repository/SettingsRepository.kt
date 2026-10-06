package com.tinyarchive.app.domain.repository

import com.tinyarchive.app.domain.model.SortOrder
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val sortOrder: StateFlow<SortOrder>
    val showNotePreview: StateFlow<Boolean>
    val compactRows: StateFlow<Boolean>
    fun setSortOrder(order: SortOrder)
    fun setShowNotePreview(enabled: Boolean)
    fun setCompactRows(enabled: Boolean)
}
