package com.tinyarchive.app.domain.usecase

import com.tinyarchive.app.core.config.GameConfig
import com.tinyarchive.app.domain.model.Category
import com.tinyarchive.app.domain.model.CategoryResult
import com.tinyarchive.app.domain.repository.ArchiveRepository

class ManageCategoriesUseCase(private val repository: ArchiveRepository) {

    private val palette = listOf("#5B5F4A", "#C58B57", "#7FA46B", "#BFC0A8", "#B4553E")

    fun add(rawName: String, now: Long): CategoryResult {
        val name = rawName.trim().take(GameConfig.MAX_CATEGORY_CHARS)
        if (name.isEmpty()) {
            return CategoryResult.BLANK_NAME
        }
        val current = repository.categories.value
        if (current.any { it.name.equals(name, ignoreCase = true) }) {
            return CategoryResult.DUPLICATE
        }
        val color = palette[current.size % palette.size]
        repository.upsertCategory(
            Category(
                id = "cat_" + now.toString(),
                name = name.uppercase(),
                colorHex = color,
                spriteKey = "drawer",
                builtIn = false,
            )
        )
        return CategoryResult.DONE
    }

    fun rename(id: String, rawName: String): CategoryResult {
        val name = rawName.trim().take(GameConfig.MAX_CATEGORY_CHARS)
        if (name.isEmpty()) {
            return CategoryResult.BLANK_NAME
        }
        val current = repository.categories.value
        if (current.any { it.id != id && it.name.equals(name, ignoreCase = true) }) {
            return CategoryResult.DUPLICATE
        }
        val target = current.firstOrNull { it.id == id } ?: return CategoryResult.BLANK_NAME
        repository.upsertCategory(target.copy(name = name.uppercase()))
        return CategoryResult.DONE
    }

    fun remove(id: String): CategoryResult {
        val target = repository.categories.value.firstOrNull { it.id == id }
            ?: return CategoryResult.DONE
        if (target.builtIn) {
            return CategoryResult.IN_USE
        }
        val removed = repository.removeCategory(id)
        return if (removed) CategoryResult.DONE else CategoryResult.IN_USE
    }

    fun countFor(id: String): Int = repository.items.value.count { it.categoryId == id }
}
