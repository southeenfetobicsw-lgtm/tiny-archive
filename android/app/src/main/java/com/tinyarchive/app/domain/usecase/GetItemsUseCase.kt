package com.tinyarchive.app.domain.usecase

import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.Category
import com.tinyarchive.app.domain.model.SortOrder

class GetItemsUseCase {

    operator fun invoke(
        items: List<ArchiveItem>,
        categories: List<Category>,
        order: SortOrder,
    ): List<ArchiveItem> {
        val names = categories.associate { it.id to it.name }
        return when (order) {
            SortOrder.NEWEST -> items.sortedByDescending { it.updatedAt }
            SortOrder.TITLE_AZ -> items.sortedBy { it.title.lowercase() }
            SortOrder.CATEGORY -> items.sortedWith(
                compareBy(
                    { names[it.categoryId].orEmpty().lowercase() },
                    { it.title.lowercase() },
                )
            )
        }
    }
}
