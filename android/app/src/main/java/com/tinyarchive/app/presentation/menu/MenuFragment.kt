package com.tinyarchive.app.presentation.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tinyarchive.app.MainActivity
import com.tinyarchive.app.core.config.GameConfig
import com.tinyarchive.app.core.util.ViewExtensions
import com.tinyarchive.app.databinding.FragmentMenuBinding
import com.tinyarchive.app.presentation.common.ViewModelFactory
import com.tinyarchive.app.presentation.dialog.CategoriesDialog
import com.tinyarchive.app.presentation.dialog.SettingsDialog
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: MenuViewModel by viewModels { ViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindActions()
        playEntrance()
        observeState()
    }

    private fun bindActions() {
        binding.btnMenuStart.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            host.navigator.showArchive()
        }
        binding.btnMenuCategories.setOnClickListener {
            if (!isAdded) {
                return@setOnClickListener
            }
            CategoriesDialog().show(parentFragmentManager, CategoriesDialog.TAG)
        }
        binding.btnMenuSettings.setOnClickListener {
            if (!isAdded) {
                return@setOnClickListener
            }
            SettingsDialog().show(parentFragmentManager, SettingsDialog.TAG)
        }
    }

    private fun playEntrance() {
        val density = resources.displayMetrics.density
        val step = GameConfig.STAGGER_MS
        ViewExtensions.riseIn(binding.headerMenu, 0L, 12f * density, 360L)
        ViewExtensions.riseIn(binding.txtHeroTop, step, 20f * density, 420L)
        ViewExtensions.riseIn(binding.txtHeroBottom, step * 2, 20f * density, 420L)
        ViewExtensions.riseIn(binding.txtMenuTagline, step * 3, 14f * density, 380L)
        ViewExtensions.popIn(binding.btnMenuStart, step * 3, 360L)
        ViewExtensions.riseIn(binding.statMenuItems, step * 4, 14f * density, 320L)
        ViewExtensions.riseIn(binding.statMenuCategories, step * 5, 14f * density, 320L)
        ViewExtensions.riseIn(binding.statMenuNotes, step * 6, 14f * density, 320L)
        ViewExtensions.riseIn(binding.rowMenuSecondary, step * 7, 14f * density, 320L)
        ViewExtensions.popIn(binding.imgMenuArt, step * 4, 520L)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: MenuUiState) {
        val binding = _binding ?: return
        binding.statMenuItems.setValue(state.itemCount)
        binding.statMenuCategories.setValue(state.categoryCount)
        binding.statMenuNotes.setValue(state.noteCount)
        val visible = listOf(state.itemCount, state.categoryCount, state.noteCount)
            .count { it > 0 }
        binding.rowMenuStats.visibility = if (visible >= 2) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        val binding = _binding
        if (binding != null) {
            ViewExtensions.stopAnimations(
                binding.headerMenu,
                binding.txtHeroTop,
                binding.txtHeroBottom,
                binding.txtMenuTagline,
                binding.btnMenuStart,
                binding.statMenuItems,
                binding.statMenuCategories,
                binding.statMenuNotes,
                binding.rowMenuSecondary,
                binding.imgMenuArt,
            )
        }
        _binding = null
        super.onDestroyView()
    }
}
