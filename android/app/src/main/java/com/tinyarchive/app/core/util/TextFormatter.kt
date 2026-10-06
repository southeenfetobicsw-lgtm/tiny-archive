package com.tinyarchive.app.core.util

object TextFormatter {

    fun metaLine(categoryName: String, reference: String, location: String): String {
        val parts = listOf(categoryName, reference, location)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        return parts.joinToString(" . ").uppercase()
    }

    fun shortMeta(categoryName: String, reference: String): String {
        val parts = listOf(categoryName, reference)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        return parts.joinToString(" . ").uppercase()
    }

    fun preview(note: String, limit: Int): String {
        val trimmed = note.trim()
        if (trimmed.length <= limit) {
            return trimmed
        }
        return trimmed.take(limit).trimEnd() + "..."
    }
}
