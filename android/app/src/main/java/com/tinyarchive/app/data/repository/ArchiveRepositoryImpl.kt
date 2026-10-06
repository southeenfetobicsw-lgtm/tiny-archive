package com.tinyarchive.app.data.repository

import com.tinyarchive.app.data.local.ArchivePreferences
import com.tinyarchive.app.data.sample.SampleData
import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.Category
import com.tinyarchive.app.domain.repository.ArchiveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArchiveRepositoryImpl(private val storage: ArchivePreferences) : ArchiveRepository {

    private val itemState = MutableStateFlow<List<ArchiveItem>>(emptyList())
    private val categoryState = MutableStateFlow<List<Category>>(emptyList())
    private val unreadableState = MutableStateFlow(false)

    override val items: StateFlow<List<ArchiveItem>> = itemState.asStateFlow()
    override val categories: StateFlow<List<Category>> = categoryState.asStateFlow()
    override val unreadable: StateFlow<Boolean> = unreadableState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        if (!storage.isSeeded()) {
            seed()
            return
        }
        try {
            val storedCategories = storage.readCategories()
            val storedItems = storage.readItems()
            categoryState.value = if (storedCategories.isEmpty()) {
                SampleData.categories
            } else {
                storedCategories
            }
            itemState.value = storedItems
            unreadableState.value = false
        } catch (e: Exception) {
            categoryState.value = SampleData.categories
            itemState.value = emptyList()
            unreadableState.value = true
        }
    }

    private fun seed() {
        categoryState.value = SampleData.categories
        itemState.value = SampleData.items
        unreadableState.value = false
        storage.writeCategories(SampleData.categories)
        storage.writeItems(SampleData.items)
        storage.markSeeded()
    }

    override fun save(item: ArchiveItem) {
        val current = itemState.value.toMutableList()
        val index = current.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            current[index] = item
        } else {
            current.add(item)
        }
        itemState.value = current.toList()
        unreadableState.value = false
        storage.writeItems(current)
    }

    override fun delete(id: String) {
        val current = itemState.value.filterNot { it.id == id }
        itemState.value = current
        storage.writeItems(current)
    }

    override fun upsertCategory(category: Category) {
        val current = categoryState.value.toMutableList()
        val index = current.indexOfFirst { it.id == category.id }
        if (index >= 0) {
            current[index] = category
        } else {
            current.add(category)
        }
        categoryState.value = current.toList()
        storage.writeCategories(current)
    }

    override fun removeCategory(id: String): Boolean {
        val inUse = itemState.value.any { it.categoryId == id }
        if (inUse) {
            return false
        }
        val current = categoryState.value.filterNot { it.id == id }
        categoryState.value = current
        storage.writeCategories(current)
        return true
    }

    override fun reset() {
        storage.clear()
        seed()
    }
}
