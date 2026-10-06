package com.tinyarchive.app.domain.usecase

import com.tinyarchive.app.core.config.GameConfig
import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.SaveResult
import com.tinyarchive.app.domain.repository.ArchiveRepository

class SaveItemUseCase(private val repository: ArchiveRepository) {

    operator fun invoke(draft: ArchiveItem, now: Long): SaveResult {
        val title = draft.title.trim().take(GameConfig.MAX_TITLE_CHARS)
        if (title.isEmpty()) {
            return SaveResult.BLANK_TITLE
        }
        val known = repository.categories.value.any { it.id == draft.categoryId }
        if (!known) {
            return SaveResult.UNKNOWN_CATEGORY
        }
        val existing = repository.items.value.firstOrNull { it.id == draft.id }
        val stamped = draft.copy(
            title = title,
            reference = draft.reference.trim(),
            location = draft.location.trim(),
            note = draft.note.trim().take(GameConfig.MAX_NOTE_CHARS),
            createdAt = existing?.createdAt ?: now,
            updatedAt = now,
        )
        repository.save(stamped)
        return SaveResult.SAVED
    }
}
