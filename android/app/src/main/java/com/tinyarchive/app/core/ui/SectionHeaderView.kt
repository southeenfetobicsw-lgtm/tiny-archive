package com.tinyarchive.app.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.tinyarchive.app.R
import com.tinyarchive.app.databinding.ViewSectionHeaderBinding

class SectionHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSectionHeaderBinding.inflate(LayoutInflater.from(context), this)

    init {
        val typed = context.obtainStyledAttributes(attrs, R.styleable.SectionHeaderView)
        val overline = typed.getString(R.styleable.SectionHeaderView_sectionOverline).orEmpty()
        val title = typed.getString(R.styleable.SectionHeaderView_sectionTitle).orEmpty()
        val iconRes = typed.getResourceId(R.styleable.SectionHeaderView_sectionTrailingIcon, 0)
        val showTrailing = typed.getBoolean(R.styleable.SectionHeaderView_sectionShowTrailing, false)
        typed.recycle()

        binding.sectionOverline.text = overline
        setTitle(title)
        if (iconRes != 0) {
            binding.sectionTrailing.setIconResource(iconRes)
        }
        binding.sectionTrailing.visibility = if (showTrailing) View.VISIBLE else View.GONE
    }

    fun setOverline(text: CharSequence) {
        binding.sectionOverline.text = text
    }

    fun setTitle(text: CharSequence) {
        binding.sectionTitle.text = text
        binding.sectionTitle.visibility = if (text.isBlank()) View.GONE else View.VISIBLE
    }

    fun setTrailingAction(visible: Boolean, action: () -> Unit) {
        binding.sectionTrailing.visibility = if (visible) View.VISIBLE else View.GONE
        binding.sectionTrailing.setOnClickListener {
            try {
                action()
            } catch (e: Exception) {
                binding.sectionTrailing.isEnabled = true
            }
        }
    }
}
