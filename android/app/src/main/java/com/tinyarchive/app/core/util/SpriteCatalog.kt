package com.tinyarchive.app.core.util

import android.graphics.Color
import com.tinyarchive.app.R

object SpriteCatalog {

    fun drawableFor(spriteKey: String): Int = when (spriteKey) {
        "book" -> R.drawable.sprite_book
        "vinyl" -> R.drawable.sprite_vinyl
        "coin" -> R.drawable.sprite_coin
        "postcard" -> R.drawable.sprite_postcard
        "botanical" -> R.drawable.sprite_botanical
        "stamp" -> R.drawable.sprite_stamp
        else -> R.drawable.sprite_drawer
    }

    fun parseColor(hex: String, fallback: Int): Int = try {
        Color.parseColor(hex)
    } catch (e: Exception) {
        fallback
    }
}
