package com.tinyarchive.app.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinyarchive.app.R
import com.tinyarchive.app.core.util.SpriteCatalog
import com.tinyarchive.app.databinding.ItemCategoryRowBinding
import com.tinyarchive.app.domain.model.Category

class CategoryAdapter(
    private val countFor: (String) -> Int,
    private val onEdit: (Category) -> Unit,
) : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    private var items: List<Category> = emptyList()

    fun submit(newItems: List<Category>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CategoryHolder(ItemCategoryRowBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val category = items[position]
        val binding = holder.binding
        val accent = SpriteCatalog.parseColor(category.colorHex, FALLBACK_ACCENT)
        val swatch = GradientDrawable()
        swatch.shape = GradientDrawable.OVAL
        swatch.setColor(accent)
        binding.categorySwatch.background = swatch
        binding.categoryName.text = category.name
        binding.categoryCount.text = binding.root.context.getString(
            R.string.category_count_format,
            countFor(category.id),
        )
        binding.categoryEdit.setOnClickListener {
            try {
                onEdit(category)
            } catch (e: Exception) {
                binding.categoryEdit.isEnabled = true
            }
        }
    }

    class CategoryHolder(val binding: ItemCategoryRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    private companion object {
        val FALLBACK_ACCENT = Color.parseColor("#5B5F4A")
    }
}
