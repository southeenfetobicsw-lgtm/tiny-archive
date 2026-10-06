package com.tinyarchive.app.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.tinyarchive.app.R
import com.tinyarchive.app.core.util.ViewExtensions
import com.tinyarchive.app.databinding.ViewStatCardBinding

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    private var accentColor: Int = ContextCompat.getColor(context, R.color.sage_primary)
    private var compact: Boolean = false

    init {
        val typed = context.obtainStyledAttributes(attrs, R.styleable.StatCardView)
        val label = typed.getString(R.styleable.StatCardView_statLabel).orEmpty()
        val value = typed.getString(R.styleable.StatCardView_statValue).orEmpty()
        accentColor = typed.getColor(R.styleable.StatCardView_statAccent, accentColor)
        compact = typed.getBoolean(R.styleable.StatCardView_statCompact, false)
        typed.recycle()

        binding.statLabel.text = label
        binding.statValue.text = value
        applyAccent()
        applyDensity()
    }

    fun setLabel(text: CharSequence) {
        binding.statLabel.text = text
    }

    fun setAccent(color: Int) {
        accentColor = color
        applyAccent()
    }

    fun setValueText(text: String) {
        binding.statValue.text = text
        visibility = View.VISIBLE
    }

    fun setValue(amount: Int) {
        if (amount <= 0) {
            visibility = View.GONE
            return
        }
        visibility = View.VISIBLE
        val next = amount.toString()
        if (binding.statValue.text.toString() == next) {
            return
        }
        binding.statValue.text = next
        pulse()
    }

    private fun pulse() {
        val value = binding.statValue
        value.animate().cancel()
        value.alpha = 0.3f
        value.scaleX = 1f
        value.scaleY = 1f
        value.animate()
            .alpha(1f)
            .scaleX(1.06f)
            .scaleY(1.06f)
            .setDuration(130L)
            .start()
        value.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(130L)
            .setDuration(130L)
            .start()
    }

    private fun applyAccent() {
        binding.statValue.setTextColor(accentColor)
        binding.statAccentBar.setBackgroundColor(accentColor)
    }

    private fun applyDensity() {
        val heightRes = if (compact) R.dimen.stat_card_height_compact else R.dimen.stat_card_height
        val sizeRes = if (compact) R.dimen.text_stat_compact else R.dimen.text_stat
        binding.statContent.minimumHeight = resources.getDimensionPixelSize(heightRes)
        binding.statValue.setTextSize(
            android.util.TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(sizeRes),
        )
    }

    override fun onDetachedFromWindow() {
        ViewExtensions.stopAnimations(binding.statValue)
        super.onDetachedFromWindow()
    }
}
