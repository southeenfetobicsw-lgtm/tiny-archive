package com.tinyarchive.app.domain.usecase

import com.tinyarchive.app.core.config.GameConfig
import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.SessionSummary

class BuildSessionSummaryUseCase {

    operator fun invoke(
        items: List<ArchiveItem>,
        touchedIds: List<String>,
        elapsedSeconds: Int,
    ): SessionSummary {
        val known = items.map { it.id }.toSet()
        val recent = touchedIds.filter { it in known }.distinct().take(GameConfig.RECENT_LIMIT)
        val fallback = items.sortedByDescending { it.updatedAt }
            .map { it.id }
            .take(GameConfig.RECENT_LIMIT)
        val usedCategories = items.map { it.categoryId }.distinct().size
        val minutes = if (elapsedSeconds <= 0) 0 else maxOf(1, elapsedSeconds / 60)
        return SessionSummary(
            itemsFiled = items.size,
            categoriesUsed = usedCategories,
            notesWritten = items.count { it.note.isNotBlank() },
            minutesSpent = minutes,
            recentIds = if (recent.isEmpty()) fallback else recent,
        )
    }
}
