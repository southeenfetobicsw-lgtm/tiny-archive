package com.tinyarchive.app.data.local

import android.content.Context
import com.tinyarchive.app.domain.model.SortOrder

class SettingsPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun readSortOrder(): SortOrder {
        val stored = prefs.getString(KEY_SORT, null) ?: return SortOrder.NEWEST
        return SortOrder.values().firstOrNull { it.name == stored } ?: SortOrder.NEWEST
    }

    fun writeSortOrder(order: SortOrder) {
        prefs.edit().putString(KEY_SORT, order.name).apply()
    }

    fun readNotePreview(): Boolean = prefs.getBoolean(KEY_NOTE_PREVIEW, true)

    fun writeNotePreview(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTE_PREVIEW, enabled).apply()
    }

    fun readCompactRows(): Boolean = prefs.getBoolean(KEY_COMPACT, false)

    fun writeCompactRows(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_COMPACT, enabled).apply()
    }

    private companion object {
        const val STORE = "tiny_archive_settings"
        const val KEY_SORT = "sort_order"
        const val KEY_NOTE_PREVIEW = "note_preview"
        const val KEY_COMPACT = "compact_rows"
    }
}
