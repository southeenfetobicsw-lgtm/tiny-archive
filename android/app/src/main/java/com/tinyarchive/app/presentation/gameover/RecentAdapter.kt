package com.tinyarchive.app.presentation.gameover

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinyarchive.app.core.util.SpriteCatalog
import com.tinyarchive.app.core.util.TextFormatter
import com.tinyarchive.app.databinding.ItemArchiveRowCompactBinding
import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.Category

class RecentAdapter : RecyclerView.Adapter<RecentAdapter.RecentHolder>() {

    private var items: List<ArchiveItem> = emptyList()
    private var categories: List<Category> = emptyList()

    fun submit(newItems: List<ArchiveItem>, newCategories: List<Category>) {
        items = newItems
        categories = newCategories
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecentHolder(ItemArchiveRowCompactBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecentHolder, position: Int) {
        val item = items[position]
        val category = categories.firstOrNull { it.id == item.categoryId }
        val accent = SpriteCatalog.parseColor(category?.colorHex.orEmpty(), FALLBACK_ACCENT)
        val binding = holder.binding
        binding.rowCompactTitle.text = item.title
        binding.rowCompactMeta.text = TextFormatter.shortMeta(
            category?.name.orEmpty(),
            item.reference,
        )
        binding.rowCompactSprite.setImageResource(
            SpriteCatalog.drawableFor(category?.spriteKey.orEmpty())
        )
        binding.rowCompactSprite.contentDescription = category?.name.orEmpty()
        binding.rowCompactAccent.setBackgroundColor(accent)
    }

    class RecentHolder(val binding: ItemArchiveRowCompactBinding) :
        RecyclerView.ViewHolder(binding.root)

    private companion object {
        val FALLBACK_ACCENT = Color.parseColor("#C58B57")
    }
}
