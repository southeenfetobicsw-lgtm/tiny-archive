package com.tinyarchive.app.presentation.game

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tinyarchive.app.MainActivity
import com.tinyarchive.app.R
import com.tinyarchive.app.core.util.ViewExtensions
import com.tinyarchive.app.databinding.FragmentGameBinding
import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.presentation.common.ViewModelFactory
import com.tinyarchive.app.presentation.dialog.CategoriesDialog
import com.tinyarchive.app.presentation.dialog.ItemEditorDialog
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: GameViewModel by viewModels { ViewModelFactory }

    private var adapter: ArchiveAdapter? = null
    private var searchWatcher: TextWatcher? = null
    private var categorySignature: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpList()
        setUpSearch()
        setUpChips()
        setUpActions()
        observeResults()
        observeState()
        viewModel.startSession()
    }

    private fun setUpList() {
        val created = ArchiveAdapter(
            onOpen = { item -> openEditor(item) },
            onRemove = { item -> confirmRemove(item) },
        )
        adapter = created
        binding.listItems.layoutManager = LinearLayoutManager(requireContext())
        binding.listItems.adapter = created
        binding.listItems.itemAnimator = null
    }

    private fun setUpSearch() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onQueryChanged(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) {
                return
            }
        }
        searchWatcher = watcher
        binding.inputSearch.addTextChangedListener(watcher)
    }

    private fun setUpChips() {
        binding.chipsCategories.setOnCategorySelected { id ->
            viewModel.onCategorySelected(id)
        }
    }

    private fun setUpActions() {
        binding.btnGameBack.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            host.navigator.back()
        }
        binding.btnGameAdd.setOnClickListener {
            openEditor(null)
        }
        binding.btnEmptyAction.setOnClickListener {
            if (viewModel.uiState.value.totalCount == 0) {
                openEditor(null)
            } else {
                clearFilters()
            }
        }
        binding.btnErrorRestore.setOnClickListener {
            viewModel.restoreSamples()
        }
        binding.btnGameCategories.setOnClickListener {
            if (!isAdded) {
                return@setOnClickListener
            }
            CategoriesDialog().show(parentFragmentManager, CategoriesDialog.TAG)
        }
        binding.btnGameReview.setOnClickListener {
            viewModel.finishSession()
        }
    }

    private fun clearFilters() {
        viewModel.clearFilters()
        binding.inputSearch.setText("")
        binding.chipsCategories.select(null)
    }

    private fun openEditor(item: ArchiveItem?) {
        if (!isAdded) {
            return
        }
        if (item != null) {
            viewModel.onItemTouched(item.id)
        }
        ItemEditorDialog.newInstance(item?.id, false)
            .show(parentFragmentManager, ItemEditorDialog.TAG)
    }

    private fun confirmRemove(item: ArchiveItem) {
        if (!isAdded) {
            return
        }
        viewModel.onItemTouched(item.id)
        ItemEditorDialog.newInstance(item.id, true)
            .show(parentFragmentManager, ItemEditorDialog.TAG)
    }

    private fun observeResults() {
        setFragmentResultListener(ItemEditorDialog.RESULT_KEY) { _, bundle ->
            val savedId = bundle.getString(ItemEditorDialog.RESULT_ITEM_ID).orEmpty()
            if (savedId.isNotEmpty()) {
                viewModel.onItemTouched(savedId)
                adapter?.flash(savedId)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.unreadableState.collect { broken ->
                    val binding = _binding ?: return@collect
                    binding.cardError.visibility = if (broken) View.VISIBLE else View.GONE
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.preferencesState.collect {
                    render(viewModel.uiState.value)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.compactState.collect {
                    render(viewModel.uiState.value)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.summaryEvent.collect { summary ->
                    if (summary != null) {
                        val host = activity as? MainActivity ?: return@collect
                        if (host.navigator.showSummary(summary)) {
                            viewModel.consumeSummary()
                        }
                    }
                }
            }
        }
    }

    private fun render(state: GameUiState) {
        val binding = _binding ?: return
        val signature = state.categories.joinToString("|") { it.id + it.name }
        if (signature != categorySignature) {
            categorySignature = signature
            binding.chipsCategories.bind(state.categories, true, state.selectedCategoryId)
        }
        adapter?.submit(
            state.visible,
            state.categories,
            viewModel.preferencesState.value,
            viewModel.compactState.value,
        )
        binding.txtGameCount.text = getString(
            R.string.count_format,
            state.totalCount,
            state.categories.size,
        )
        binding.statShowing.setValue(state.visible.size)
        binding.statTotal.setValue(state.totalCount)
        binding.statWithNotes.setValue(state.noteCount)
        val filtering = state.query.isNotEmpty() || state.selectedCategoryId != null
        renderEmptyState(state, filtering)
    }

    private fun renderEmptyState(state: GameUiState, filtering: Boolean) {
        val binding = _binding ?: return
        if (state.visible.isNotEmpty()) {
            binding.groupEmpty.visibility = View.GONE
            return
        }
        binding.groupEmpty.visibility = View.VISIBLE
        if (filtering) {
            binding.txtEmptyTitle.setText(R.string.no_match_title)
            binding.txtEmptyBody.setText(R.string.no_match_body)
            binding.btnEmptyAction.setText(R.string.no_match_action)
            binding.btnEmptyAction.setIconResource(R.drawable.ic_close)
        } else {
            binding.txtEmptyTitle.setText(R.string.empty_title)
            binding.txtEmptyBody.setText(R.string.empty_body)
            binding.btnEmptyAction.setText(R.string.empty_action)
            binding.btnEmptyAction.setIconResource(R.drawable.ic_plus)
        }
    }

    override fun onDestroyView() {
        val binding = _binding
        adapter?.cancelAnimations()
        if (binding != null) {
            val watcher = searchWatcher
            if (watcher != null) {
                binding.inputSearch.removeTextChangedListener(watcher)
            }
            binding.listItems.adapter = null
            ViewExtensions.stopAnimations(
                binding.statShowing,
                binding.statTotal,
                binding.statWithNotes,
            )
        }
        searchWatcher = null
        adapter = null
        categorySignature = ""
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        viewModel.stopSession()
        super.onDestroy()
    }
}
