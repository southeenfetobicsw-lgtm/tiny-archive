package com.tinyarchive.app.data.local

import android.content.Context
import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.Category

class ArchivePreferences(context: Context) {

    private val prefs = context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun isSeeded(): Boolean = prefs.getBoolean(KEY_SEEDED, false)

    fun markSeeded() {
        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun readItems(): List<ArchiveItem> {
        val raw = prefs.getString(KEY_ITEMS, null) ?: return emptyList()
        if (raw.isEmpty()) {
            return emptyList()
        }
        return raw.split(RECORD).filter { it.isNotEmpty() }.map { record ->
            val parts = record.split(FIELD)
            if (parts.size != ITEM_FIELDS) {
                throw ArchiveFormatException("record field count")
            }
            ArchiveItem(
                id = parts[0],
                title = parts[1],
                categoryId = parts[2],
                reference = parts[3],
                location = parts[4],
                note = parts[5],
                createdAt = parts[6].toLongOrNull()
                    ?: throw ArchiveFormatException("created stamp"),
                updatedAt = parts[7].toLongOrNull()
                    ?: throw ArchiveFormatException("updated stamp"),
            )
        }
    }

    fun writeItems(items: List<ArchiveItem>) {
        val encoded = items.joinToString(RECORD) { item ->
            listOf(
                clean(item.id),
                clean(item.title),
                clean(item.categoryId),
                clean(item.reference),
                clean(item.location),
                clean(item.note),
                item.createdAt.toString(),
                item.updatedAt.toString(),
            ).joinToString(FIELD)
        }
        prefs.edit().putString(KEY_ITEMS, encoded).apply()
    }

    fun readCategories(): List<Category> {
        val raw = prefs.getString(KEY_CATEGORIES, null) ?: return emptyList()
        if (raw.isEmpty()) {
            return emptyList()
        }
        return raw.split(RECORD).filter { it.isNotEmpty() }.map { record ->
            val parts = record.split(FIELD)
            if (parts.size != CATEGORY_FIELDS) {
                throw ArchiveFormatException("category field count")
            }
            Category(
                id = parts[0],
                name = parts[1],
                colorHex = parts[2],
                spriteKey = parts[3],
                builtIn = parts[4] == FLAG_TRUE,
            )
        }
    }

    fun writeCategories(categories: List<Category>) {
        val encoded = categories.joinToString(RECORD) { category ->
            listOf(
                clean(category.id),
                clean(category.name),
                clean(category.colorHex),
                clean(category.spriteKey),
                if (category.builtIn) FLAG_TRUE else FLAG_FALSE,
            ).joinToString(FIELD)
        }
        prefs.edit().putString(KEY_CATEGORIES, encoded).apply()
    }

    private fun clean(value: String): String =
        value.filter { it.code > 31 || it == ' ' }.trim()

    private companion object {
        const val STORE = "tiny_archive_store"
        const val KEY_ITEMS = "archive_items"
        const val KEY_CATEGORIES = "archive_categories"
        const val KEY_SEEDED = "archive_seeded"
        const val ITEM_FIELDS = 8
        const val CATEGORY_FIELDS = 5
        const val FLAG_TRUE = "1"
        const val FLAG_FALSE = "0"
        val FIELD = Char(1).toString()
        val RECORD = Char(2).toString()
    }
}
