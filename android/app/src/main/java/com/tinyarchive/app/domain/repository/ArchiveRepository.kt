package com.tinyarchive.app.domain.repository

import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.Category
import kotlinx.coroutines.flow.StateFlow

interface ArchiveRepository {
    val items: StateFlow<List<ArchiveItem>>
    val categories: StateFlow<List<Category>>
    val unreadable: StateFlow<Boolean>
    fun save(item: ArchiveItem)
    fun delete(id: String)
    fun upsertCategory(category: Category)
    fun removeCategory(id: String): Boolean
    fun reset()
}
