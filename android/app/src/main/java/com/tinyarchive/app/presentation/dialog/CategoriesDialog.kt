package com.tinyarchive.app.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tinyarchive.app.R
import com.tinyarchive.app.core.di.ServiceLocator
import com.tinyarchive.app.databinding.DialogCategoriesBinding
import com.tinyarchive.app.domain.model.Category
import com.tinyarchive.app.domain.model.CategoryResult
import kotlinx.coroutines.launch

class CategoriesDialog : DialogFragment() {

    private var _binding: DialogCategoriesBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var adapter: CategoryAdapter? = null
    private var renamingId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manage = ServiceLocator.manageCategoriesUseCase()
        val created = CategoryAdapter(
            countFor = { id -> manage.countFor(id) },
            onEdit = { category -> startRename(category) },
        )
        adapter = created
        binding.listCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.listCategories.adapter = created
        binding.btnCategoryAdd.setOnClickListener {
            submitName()
        }
        binding.btnCategoriesDone.setOnClickListener {
            dismissAllowingStateLoss()
        }
        observeCategories()
    }

    private fun startRename(category: Category) {
        val binding = _binding ?: return
        renamingId = category.id
        binding.inputCategoryName.setText(category.name)
        binding.btnCategoryAdd.setText(R.string.action_save)
        binding.tilCategoryName.error = null
    }

    private fun submitName() {
        val binding = _binding ?: return
        val typed = binding.inputCategoryName.text?.toString().orEmpty()
        val manage = ServiceLocator.manageCategoriesUseCase()
        val outcome = if (renamingId.isEmpty()) {
            manage.add(typed, System.currentTimeMillis())
        } else {
            manage.rename(renamingId, typed)
        }
        when (outcome) {
            CategoryResult.DONE -> {
                renamingId = ""
                binding.inputCategoryName.setText("")
                binding.btnCategoryAdd.setText(R.string.action_add)
                binding.tilCategoryName.error = null
            }
            CategoryResult.BLANK_NAME -> {
                binding.tilCategoryName.error = getString(R.string.error_category_blank)
            }
            CategoryResult.DUPLICATE -> {
                binding.tilCategoryName.error = getString(R.string.error_category_duplicate)
            }
            CategoryResult.IN_USE -> {
                binding.tilCategoryName.error = getString(R.string.error_category_in_use)
            }
        }
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                ServiceLocator.archiveRepository().categories.collect { categories ->
                    adapter?.submit(categories)
                }
            }
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
        _binding?.listCategories?.adapter = null
        adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "categories_dialog"
    }
}
