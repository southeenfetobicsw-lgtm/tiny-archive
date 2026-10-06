package com.tinyarchive.app.presentation.gameover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tinyarchive.app.MainActivity
import com.tinyarchive.app.core.config.GameConfig
import com.tinyarchive.app.core.util.ViewExtensions
import com.tinyarchive.app.databinding.FragmentGameoverBinding
import com.tinyarchive.app.domain.model.SessionSummary
import com.tinyarchive.app.presentation.common.ViewModelFactory
import kotlinx.coroutines.launch

class GameOverFragment : Fragment() {

    private var _binding: FragmentGameoverBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: GameOverViewModel by viewModels { ViewModelFactory }

    private var adapter: RecentAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGameoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val created = RecentAdapter()
        adapter = created
        binding.listRecent.layoutManager = LinearLayoutManager(requireContext())
        binding.listRecent.adapter = created
        binding.listRecent.isNestedScrollingEnabled = false
        bindActions()
        playEntrance()
        viewModel.applySummary(readSummary())
        observeState()
    }

    private fun readSummary(): SessionSummary {
        val args = arguments ?: return SessionSummary(0, 0, 0, 0, emptyList())
        return SessionSummary(
            itemsFiled = args.getInt(ARG_FILED, 0),
            categoriesUsed = args.getInt(ARG_USED, 0),
            notesWritten = args.getInt(ARG_NOTES, 0),
            minutesSpent = args.getInt(ARG_MINUTES, 0),
            recentIds = args.getStringArrayList(ARG_RECENT)?.toList().orEmpty(),
        )
    }

    private fun bindActions() {
        binding.btnSummaryAgain.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            host.navigator.back()
        }
        binding.btnSummaryMenu.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            host.navigator.backToMenu()
        }
    }

    private fun playEntrance() {
        val density = resources.displayMetrics.density
        val step = GameConfig.STAGGER_MS
        ViewExtensions.riseIn(binding.txtSummaryOverline, 0L, 12f * density, 320L)
        ViewExtensions.riseIn(binding.txtSummaryTitle, step, 16f * density, 380L)
        ViewExtensions.riseIn(binding.statFiled, step * 2, 16f * density, 360L)
        ViewExtensions.riseIn(binding.statUsed, step * 3, 16f * density, 360L)
        ViewExtensions.riseIn(binding.statWritten, step * 4, 16f * density, 360L)
        ViewExtensions.riseIn(binding.statMinutes, step * 5, 16f * density, 360L)
        ViewExtensions.riseIn(binding.headerRecent, step * 6, 14f * density, 340L)
        ViewExtensions.popIn(binding.btnSummaryAgain, step * 6, 340L)
        stampEntrance()
    }

    private fun stampEntrance() {
        val stamp = binding.imgSummaryStamp
        stamp.rotation = -6f
        stamp.alpha = 0f
        stamp.animate()
            .alpha(0.9f)
            .rotation(0f)
            .setStartDelay(GameConfig.STAGGER_MS * 2)
            .setDuration(480L)
            .setInterpolator(OvershootInterpolator(1.4f))
            .start()
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

    private fun render(state: GameOverUiState) {
        val binding = _binding ?: return
        binding.statFiled.setValue(state.itemsFiled)
        binding.statUsed.setValue(state.categoriesUsed)
        binding.statWritten.setValue(state.notesWritten)
        binding.statMinutes.setValue(state.minutesSpent)
        val values = listOf(
            state.itemsFiled,
            state.categoriesUsed,
            state.notesWritten,
            state.minutesSpent,
        )
        val visible = values.count { it > 0 }
        val showGrid = visible >= 2
        binding.rowSummaryTop.visibility = if (showGrid) View.VISIBLE else View.GONE
        binding.rowSummaryBottom.visibility =
            if (showGrid && (state.notesWritten > 0 || state.minutesSpent > 0)) {
                View.VISIBLE
            } else {
                View.GONE
            }
        binding.txtSummaryQuiet.visibility = if (showGrid) View.GONE else View.VISIBLE
        adapter?.submit(state.recent, state.categories)
        binding.headerRecent.visibility = if (state.recent.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        val binding = _binding
        if (binding != null) {
            ViewExtensions.stopAnimations(
                binding.txtSummaryOverline,
                binding.txtSummaryTitle,
                binding.statFiled,
                binding.statUsed,
                binding.statWritten,
                binding.statMinutes,
                binding.headerRecent,
                binding.btnSummaryAgain,
                binding.imgSummaryStamp,
            )
            binding.listRecent.adapter = null
        }
        adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val ARG_FILED = "filed"
        private const val ARG_USED = "used"
        private const val ARG_NOTES = "notes"
        private const val ARG_MINUTES = "minutes"
        private const val ARG_RECENT = "recent"

        fun newInstance(summary: SessionSummary): GameOverFragment {
            val fragment = GameOverFragment()
            val args = Bundle()
            args.putInt(ARG_FILED, summary.itemsFiled)
            args.putInt(ARG_USED, summary.categoriesUsed)
            args.putInt(ARG_NOTES, summary.notesWritten)
            args.putInt(ARG_MINUTES, summary.minutesSpent)
            args.putStringArrayList(ARG_RECENT, ArrayList(summary.recentIds))
            fragment.arguments = args
            return fragment
        }
    }
}
