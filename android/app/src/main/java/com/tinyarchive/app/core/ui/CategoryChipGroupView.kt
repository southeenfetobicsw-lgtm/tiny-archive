package com.tinyarchive.app.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.tinyarchive.app.R
import com.tinyarchive.app.core.util.SpriteCatalog
import com.tinyarchive.app.core.util.ViewExtensions
import com.tinyarchive.app.databinding.ViewCategoryChipsBinding
import com.tinyarchive.app.domain.model.Category

class CategoryChipGroupView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewCategoryChipsBinding.inflate(LayoutInflater.from(context), this)

    private var ids: List<String?> = emptyList()
    private var listener: ((String?) -> Unit)? = null
    private var suppress = false

    fun setOnCategorySelected(block: (String?) -> Unit) {
        listener = block
    }

    fun bind(categories: List<Category>, includeAll: Boolean, selectedId: String?) {
        suppress = true
        binding.chipGroup.removeAllViews()
        val keys = mutableListOf<String?>()
        if (includeAll) {
            keys.add(null)
            binding.chipGroup.addView(
                buildChip(
                    label = context.getString(R.string.chip_all),
                    accent = ContextCompat.getColor(context, R.color.sage_primary),
                    checked = selectedId == null,
                )
            )
        }
        categories.forEach { category ->
            keys.add(category.id)
            val accent = SpriteCatalog.parseColor(
                category.colorHex,
                ContextCompat.getColor(context, R.color.terracotta_accent),
            )
            binding.chipGroup.addView(
                buildChip(
                    label = category.name,
                    accent = accent,
                    checked = category.id == selectedId,
                )
            )
        }
        ids = keys
        suppress = false
    }

    fun selected(): String? {
        val index = indexOfChecked()
        return if (index in ids.indices) ids[index] else null
    }

    fun select(id: String?) {
        val index = ids.indexOf(id)
        if (index !in ids.indices) {
            return
        }
        suppress = true
        val chip = binding.chipGroup.getChildAt(index) as? Chip
        chip?.isChecked = true
        suppress = false
    }

    private fun indexOfChecked(): Int {
        for (index in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(index) as? Chip
            if (chip != null && chip.isChecked) {
                return index
            }
        }
        return -1
    }

    private fun buildChip(label: String, accent: Int, checked: Boolean): Chip {
        val chip = Chip(context)
        chip.setTextAppearanceResource(R.style.TextAppearance_App_Meta)
        chip.text = label
        chip.isCheckable = true
        chip.isCheckedIconVisible = false
        chip.isChecked = checked
        chip.chipMinHeight = resources.getDimension(R.dimen.chip_height)
        chip.chipCornerRadius = resources.getDimension(R.dimen.radius_chip)
        chip.chipStrokeWidth = resources.getDimension(R.dimen.hairline)
        chip.chipStrokeColor = ColorStateList.valueOf(accent)
        applyChecked(chip, accent, checked)
        chip.setOnCheckedChangeListener { button, isChecked ->
            try {
                applyChecked(button as Chip, accent, isChecked)
                if (!suppress && isChecked) {
                    listener?.invoke(selected())
                }
            } catch (e: Exception) {
                chip.isEnabled = true
            }
        }
        return chip
    }

    private fun applyChecked(chip: Chip, accent: Int, checked: Boolean) {
        val fill = if (checked) {
            ContextCompat.getColor(context, R.color.chip_selected_fill)
        } else {
            ContextCompat.getColor(context, R.color.transparent)
        }
        chip.chipBackgroundColor = ColorStateList.valueOf(fill)
        chip.setTextColor(
            if (checked) accent else ContextCompat.getColor(context, R.color.ink_secondary)
        )
        ViewExtensions.describeState(
            chip,
            context.getString(
                if (checked) R.string.state_selected else R.string.state_not_selected
            ),
        )
    }
}
