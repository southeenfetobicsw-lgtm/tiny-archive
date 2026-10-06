package com.tinyarchive.app.domain.usecase

import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.Category

class SearchItemsUseCase {

    operator fun invoke(
        items: List<ArchiveItem>,
        categories: List<Category>,
        query: String,
        categoryId: String?,
    ): List<ArchiveItem> {
        val names = categories.associate { it.id to it.name.lowercase() }
        val needle = query.trim().lowercase()
        return items.filter { item ->
            val inCategory = categoryId == null || item.categoryId == categoryId
            if (!inCategory) {
                false
            } else if (needle.isEmpty()) {
                true
            } else {
                val categoryName = names[item.categoryId].orEmpty()
                item.title.lowercase().contains(needle) ||
                    item.note.lowercase().contains(needle) ||
                    item.reference.lowercase().contains(needle) ||
                    item.location.lowercase().contains(needle) ||
                    categoryName.contains(needle)
            }
        }
    }
}
