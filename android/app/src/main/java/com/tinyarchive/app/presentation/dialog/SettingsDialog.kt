package com.tinyarchive.app.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.tinyarchive.app.R
import com.tinyarchive.app.core.di.ServiceLocator
import com.tinyarchive.app.core.util.ViewExtensions
import com.tinyarchive.app.databinding.DialogSettingsBinding
import com.tinyarchive.app.domain.model.SortOrder

class SettingsDialog : DialogFragment() {

    private var _binding: DialogSettingsBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var pendingOrder: SortOrder = SortOrder.NEWEST
    private var pendingNotePreview: Boolean = true
    private var pendingCompact: Boolean = false
    private var resetArmed: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val settings = ServiceLocator.settingsRepository()
        pendingOrder = settings.sortOrder.value
        pendingNotePreview = settings.showNotePreview.value
        pendingCompact = settings.compactRows.value
        renderSort()
        renderSwitches()
        bindActions()
    }

    private fun renderSort() {
        val target = when (pendingOrder) {
            SortOrder.NEWEST -> binding.chipSortNewest
            SortOrder.TITLE_AZ -> binding.chipSortTitle
            SortOrder.CATEGORY -> binding.chipSortCategory
        }
        target.isChecked = true
    }

    private fun renderSwitches() {
        binding.switchNotePreview.isChecked = pendingNotePreview
        binding.switchCompactRows.isChecked = pendingCompact
        describe(binding.switchNotePreview, pendingNotePreview)
        describe(binding.switchCompactRows, pendingCompact)
    }

    private fun describe(target: View, enabled: Boolean) {
        ViewExtensions.describeState(
            target,
            getString(if (enabled) R.string.state_on else R.string.state_off),
        )
    }

    private fun bindActions() {
        binding.chipsSort.setOnCheckedStateChangeListener { _, checked ->
            val id = checked.firstOrNull() ?: return@setOnCheckedStateChangeListener
            pendingOrder = when (id) {
                R.id.chipSortTitle -> SortOrder.TITLE_AZ
                R.id.chipSortCategory -> SortOrder.CATEGORY
                else -> SortOrder.NEWEST
            }
        }
        binding.switchNotePreview.setOnCheckedChangeListener { button, isChecked ->
            pendingNotePreview = isChecked
            describe(button, isChecked)
        }
        binding.switchCompactRows.setOnCheckedChangeListener { button, isChecked ->
            pendingCompact = isChecked
            describe(button, isChecked)
        }
        binding.btnSettingsReset.setOnClickListener {
            if (resetArmed) {
                ServiceLocator.archiveRepository().reset()
                dismissAllowingStateLoss()
            } else {
                resetArmed = true
                binding.btnSettingsReset.setText(R.string.settings_reset_confirm)
            }
        }
        binding.btnSettingsCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding.btnSettingsDone.setOnClickListener {
            val settings = ServiceLocator.settingsRepository()
            settings.setSortOrder(pendingOrder)
            settings.setShowNotePreview(pendingNotePreview)
            settings.setCompactRows(pendingCompact)
            dismissAllowingStateLoss()
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "settings_dialog"
    }
}
