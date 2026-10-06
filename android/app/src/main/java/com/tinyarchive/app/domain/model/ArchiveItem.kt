package com.tinyarchive.app.domain.model

data class ArchiveItem(
    val id: String,
    val title: String,
    val categoryId: String,
    val reference: String,
    val location: String,
    val note: String,
    val createdAt: Long,
    val updatedAt: Long,
)
