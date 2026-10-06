package com.tinyarchive.app.data.repository

import com.tinyarchive.app.data.local.SettingsPreferences
import com.tinyarchive.app.domain.model.SortOrder
import com.tinyarchive.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(private val storage: SettingsPreferences) : SettingsRepository {

    private val sortState = MutableStateFlow(storage.readSortOrder())
    private val notePreviewState = MutableStateFlow(storage.readNotePreview())
    private val compactState = MutableStateFlow(storage.readCompactRows())

    override val sortOrder: StateFlow<SortOrder> = sortState.asStateFlow()
    override val showNotePreview: StateFlow<Boolean> = notePreviewState.asStateFlow()
    override val compactRows: StateFlow<Boolean> = compactState.asStateFlow()

    override fun setSortOrder(order: SortOrder) {
        sortState.value = order
        storage.writeSortOrder(order)
    }

    override fun setShowNotePreview(enabled: Boolean) {
        notePreviewState.value = enabled
        storage.writeNotePreview(enabled)
    }

    override fun setCompactRows(enabled: Boolean) {
        compactState.value = enabled
        storage.writeCompactRows(enabled)
    }
}
