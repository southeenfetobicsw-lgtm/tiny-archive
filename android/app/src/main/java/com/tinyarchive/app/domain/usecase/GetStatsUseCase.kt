package com.tinyarchive.app.domain.usecase

import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.ArchiveStats
import com.tinyarchive.app.domain.model.Category

class GetStatsUseCase {

    operator fun invoke(items: List<ArchiveItem>, categories: List<Category>): ArchiveStats {
        val used = items.map { it.categoryId }.toSet()
        return ArchiveStats(
            itemCount = items.size,
            categoryCount = categories.count { it.id in used || it.builtIn },
            noteCount = items.count { it.note.isNotBlank() },
        )
    }
}
