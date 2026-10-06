package com.tinyarchive.app.presentation.game

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinyarchive.app.core.config.GameConfig
import com.tinyarchive.app.core.util.SpriteCatalog
import com.tinyarchive.app.core.util.TextFormatter
import com.tinyarchive.app.databinding.ItemArchiveRowBinding
import com.tinyarchive.app.databinding.ItemArchiveRowCompactBinding
import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.Category

class ArchiveAdapter(
    private val onOpen: (ArchiveItem) -> Unit,
    private val onRemove: (ArchiveItem) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ArchiveItem> = emptyList()
    private var categories: List<Category> = emptyList()
    private var showNote: Boolean = true
    private var compact: Boolean = false
    private var flashId: String? = null
    private var animatedRows = 0

    private val running = mutableListOf<ValueAnimator>()

    fun submit(
        newItems: List<ArchiveItem>,
        newCategories: List<Category>,
        notePreview: Boolean,
        compactRows: Boolean,
    ) {
        val layoutChanged = compact != compactRows
        items = newItems
        categories = newCategories
        showNote = notePreview
        compact = compactRows
        if (layoutChanged) {
            animatedRows = 0
        }
        notifyDataSetChanged()
    }

    fun flash(id: String) {
        flashId = id
        notifyDataSetChanged()
    }

    fun cancelAnimations() {
        running.forEach { animator ->
            animator.cancel()
        }
        running.clear()
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int =
        if (compact) TYPE_COMPACT else TYPE_FULL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_COMPACT) {
            CompactHolder(ItemArchiveRowCompactBinding.inflate(inflater, parent, false))
        } else {
            FullHolder(ItemArchiveRowBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val category = categories.firstOrNull { it.id == item.categoryId }
        val accent = SpriteCatalog.parseColor(category?.colorHex.orEmpty(), FALLBACK_ACCENT)
        val sprite = SpriteCatalog.drawableFor(category?.spriteKey.orEmpty())
        val categoryName = category?.name.orEmpty()
        when (holder) {
            is FullHolder -> bindFull(holder, item, accent, sprite, categoryName)
            is CompactHolder -> bindCompact(holder, item, accent, sprite, categoryName)
        }
        animateEntry(holder.itemView)
    }

    private fun bindFull(
        holder: FullHolder,
        item: ArchiveItem,
        accent: Int,
        sprite: Int,
        categoryName: String,
    ) {
        val binding = holder.binding
        binding.rowTitle.text = item.title
        binding.rowMeta.text = TextFormatter.metaLine(categoryName, item.reference, item.location)
        binding.rowSprite.setImageResource(sprite)
        binding.rowSprite.contentDescription = categoryName
        binding.rowAccentBar.setBackgroundColor(accent)
        val note = TextFormatter.preview(item.note, NOTE_PREVIEW_CHARS)
        if (showNote && note.isNotEmpty()) {
            binding.rowNote.visibility = View.VISIBLE
            binding.rowNote.text = note
        } else {
            binding.rowNote.visibility = View.GONE
        }
        binding.rowCard.setOnClickListener {
            try {
                onOpen(item)
            } catch (e: Exception) {
                binding.rowCard.isEnabled = true
            }
        }
        binding.rowCard.setOnLongClickListener {
            try {
                onRemove(item)
            } catch (e: Exception) {
                binding.rowCard.isEnabled = true
            }
            true
        }
        if (flashId == item.id) {
            flashId = null
            flashAccent(binding.rowAccentBar, accent)
        }
    }

    private fun bindCompact(
        holder: CompactHolder,
        item: ArchiveItem,
        accent: Int,
        sprite: Int,
        categoryName: String,
    ) {
        val binding = holder.binding
        binding.rowCompactTitle.text = item.title
        binding.rowCompactMeta.text = TextFormatter.shortMeta(categoryName, item.reference)
        binding.rowCompactSprite.setImageResource(sprite)
        binding.rowCompactSprite.contentDescription = categoryName
        binding.rowCompactAccent.setBackgroundColor(accent)
        binding.rowCompactCard.setOnClickListener {
            try {
                onOpen(item)
            } catch (e: Exception) {
                binding.rowCompactCard.isEnabled = true
            }
        }
        binding.rowCompactCard.setOnLongClickListener {
            try {
                onRemove(item)
            } catch (e: Exception) {
                binding.rowCompactCard.isEnabled = true
            }
            true
        }
    }

    private fun animateEntry(target: View) {
        if (animatedRows >= GameConfig.ROW_ENTER_LIMIT) {
            target.alpha = 1f
            target.translationY = 0f
            return
        }
        val delay = animatedRows * 40L
        animatedRows += 1
        target.alpha = 0f
        target.translationY = 14f * target.resources.displayMetrics.density
        target.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delay)
            .setDuration(GameConfig.ROW_ENTER_MS)
            .start()
    }

    private fun flashAccent(target: View, accent: Int) {
        val animator = ValueAnimator.ofObject(ArgbEvaluator(), FLASH_COLOR, accent)
        animator.duration = GameConfig.FLASH_MS
        animator.addUpdateListener { value ->
            try {
                val color = value.animatedValue as? Int ?: accent
                target.setBackgroundColor(color)
            } catch (e: Exception) {
                target.setBackgroundColor(accent)
            }
        }
        animator.start()
        running.add(animator)
    }

    private class FullHolder(val binding: ItemArchiveRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class CompactHolder(val binding: ItemArchiveRowCompactBinding) :
        RecyclerView.ViewHolder(binding.root)

    private companion object {
        const val TYPE_FULL = 0
        const val TYPE_COMPACT = 1
        const val NOTE_PREVIEW_CHARS = 64
        val FALLBACK_ACCENT = Color.parseColor("#C58B57")
        val FLASH_COLOR = Color.parseColor("#C58B57")
    }
}
