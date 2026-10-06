package com.tinyarchive.app.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.tinyarchive.app.R
import com.tinyarchive.app.core.di.ServiceLocator
import com.tinyarchive.app.databinding.DialogItemEditorBinding
import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.SaveResult

class ItemEditorDialog : DialogFragment() {

    private var _binding: DialogItemEditorBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var editing: ArchiveItem? = null
    private var deleteArmed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogItemEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = ServiceLocator.archiveRepository()
        val itemId = arguments?.getString(ARG_ITEM_ID).orEmpty()
        editing = repository.items.value.firstOrNull { it.id == itemId }
        binding.chipsEditorCategory.bind(
            repository.categories.value,
            false,
            editing?.categoryId ?: repository.categories.value.firstOrNull()?.id,
        )
        fillFields()
        bindActions()
        if (arguments?.getBoolean(ARG_ARM_DELETE, false) == true && editing != null) {
            armDelete()
        }
    }

    private fun fillFields() {
        val current = editing
        if (current == null) {
            binding.editorTitle.setText(R.string.dialog_item_new)
            binding.btnEditorDelete.visibility = View.GONE
            return
        }
        binding.editorTitle.setText(R.string.dialog_item_edit)
        binding.btnEditorDelete.visibility = View.VISIBLE
        binding.inputEditorTitle.setText(current.title)
        binding.inputEditorReference.setText(current.reference)
        binding.inputEditorLocation.setText(current.location)
        binding.inputEditorNote.setText(current.note)
    }

    private fun bindActions() {
        binding.btnEditorCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding.btnEditorSave.setOnClickListener {
            save()
        }
        binding.btnEditorDelete.setOnClickListener {
            if (deleteArmed) {
                remove()
            } else {
                armDelete()
            }
        }
    }

    private fun armDelete() {
        deleteArmed = true
        binding.btnEditorDelete.setText(R.string.action_delete_confirm)
        binding.btnEditorDelete.setIconResource(R.drawable.ic_trash)
    }

    private fun save() {
        val binding = _binding ?: return
        val categoryId = binding.chipsEditorCategory.selected()
        if (categoryId == null) {
            binding.txtEditorCategoryError.visibility = View.VISIBLE
            return
        }
        binding.txtEditorCategoryError.visibility = View.GONE
        val now = System.currentTimeMillis()
        val current = editing
        val draft = ArchiveItem(
            id = current?.id ?: "item_" + now.toString(),
            title = binding.inputEditorTitle.text?.toString().orEmpty(),
            categoryId = categoryId,
            reference = binding.inputEditorReference.text?.toString().orEmpty(),
            location = binding.inputEditorLocation.text?.toString().orEmpty(),
            note = binding.inputEditorNote.text?.toString().orEmpty(),
            createdAt = current?.createdAt ?: now,
            updatedAt = now,
        )
        when (ServiceLocator.saveItemUseCase().invoke(draft, now)) {
            SaveResult.SAVED -> {
                publish(draft.id)
                dismissAllowingStateLoss()
            }
            SaveResult.BLANK_TITLE -> {
                binding.tilEditorTitle.error = getString(R.string.error_title_blank)
            }
            SaveResult.UNKNOWN_CATEGORY -> {
                binding.txtEditorCategoryError.visibility = View.VISIBLE
            }
        }
    }

    private fun remove() {
        val current = editing ?: return
        ServiceLocator.deleteItemUseCase().invoke(current.id)
        publish("")
        dismissAllowingStateLoss()
    }

    private fun publish(itemId: String) {
        if (!isAdded) {
            return
        }
        val result = Bundle()
        result.putString(RESULT_ITEM_ID, itemId)
        setFragmentResult(RESULT_KEY, result)
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

        const val TAG = "item_editor"
        const val RESULT_KEY = "item_editor_result"
        const val RESULT_ITEM_ID = "item_editor_item_id"

        private const val ARG_ITEM_ID = "item_id"
        private const val ARG_ARM_DELETE = "arm_delete"

        fun newInstance(itemId: String?, armDelete: Boolean): ItemEditorDialog {
            val fragment = ItemEditorDialog()
            val args = Bundle()
            args.putString(ARG_ITEM_ID, itemId.orEmpty())
            args.putBoolean(ARG_ARM_DELETE, armDelete)
            fragment.arguments = args
            return fragment
        }
    }
}
