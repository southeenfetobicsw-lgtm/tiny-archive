package com.tinyarchive.app.domain.usecase

import com.tinyarchive.app.domain.repository.ArchiveRepository

class DeleteItemUseCase(private val repository: ArchiveRepository) {

    operator fun invoke(id: String) {
        if (id.isBlank()) {
            return
        }
        repository.delete(id)
    }
}
