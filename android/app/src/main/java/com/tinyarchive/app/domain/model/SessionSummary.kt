package com.tinyarchive.app.domain.model

data class SessionSummary(
    val itemsFiled: Int,
    val categoriesUsed: Int,
    val notesWritten: Int,
    val minutesSpent: Int,
    val recentIds: List<String>,
)
