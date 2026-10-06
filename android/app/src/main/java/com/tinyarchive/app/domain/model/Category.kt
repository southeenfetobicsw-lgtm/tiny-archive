package com.tinyarchive.app.domain.model

data class Category(
    val id: String,
    val name: String,
    val colorHex: String,
    val spriteKey: String,
    val builtIn: Boolean,
)
