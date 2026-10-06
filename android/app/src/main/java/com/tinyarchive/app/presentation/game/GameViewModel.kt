package com.tinyarchive.app.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinyarchive.app.core.config.GameConfig
import com.tinyarchive.app.domain.model.SessionSummary
import com.tinyarchive.app.domain.repository.ArchiveRepository
import com.tinyarchive.app.domain.repository.SettingsRepository
import com.tinyarchive.app.domain.usecase.BuildSessionSummaryUseCase
import com.tinyarchive.app.domain.usecase.GetItemsUseCase
import com.tinyarchive.app.domain.usecase.SearchItemsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(
    private val archiveRepository: ArchiveRepository,
    private val settingsRepository: SettingsRepository,
    private val getItems: GetItemsUseCase,
    private val searchItems: SearchItemsUseCase,
    private val buildSummary: BuildSessionSummaryUseCase,
) : ViewModel() {

    private val queryState = MutableStateFlow("")
    private val categoryState = MutableStateFlow<String?>(null)
    private val summaryState = MutableStateFlow<SessionSummary?>(null)
    private val elapsedState = MutableStateFlow(0)

    private val touched = mutableListOf<String>()
    private var ticker: Job? = null

    val summaryEvent: StateFlow<SessionSummary?> = summaryState.asStateFlow()

    val uiState: StateFlow<GameUiState> = combine(
        archiveRepository.items,
        archiveRepository.categories,
        queryState,
        categoryState,
        settingsRepository.sortOrder,
    ) { items, categories, query, categoryId, order ->
        val filtered = searchItems(items, categories, query, categoryId)
        val ordered = getItems(filtered, categories, order)
        GameUiState(
            visible = ordered,
            categories = categories,
            totalCount = items.size,
            noteCount = items.count { it.note.isNotBlank() },
            query = query,
            selectedCategoryId = categoryId,
            showNotePreview = settingsRepository.showNotePreview.value,
            compactRows = settingsRepository.compactRows.value,
            unreadable = archiveRepository.unreadable.value,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = GameUiState(),
    )

    val preferencesState: StateFlow<Boolean> = settingsRepository.showNotePreview

    val compactState: StateFlow<Boolean> = settingsRepository.compactRows

    val unreadableState: StateFlow<Boolean> = archiveRepository.unreadable

    fun startSession() {
        if (ticker != null) {
            return
        }
        ticker = viewModelScope.launch {
            while (elapsedState.value * 1000L < GameConfig.SESSION_AUTO_FINISH_MS) {
                delay(GameConfig.SESSION_TICK_MS)
                elapsedState.value = elapsedState.value + 1
            }
            finishSession()
        }
    }

    fun stopSession() {
        ticker?.cancel()
        ticker = null
    }

    fun onQueryChanged(value: String) {
        queryState.value = value
    }

    fun onCategorySelected(id: String?) {
        categoryState.value = id
    }

    fun onItemTouched(id: String) {
        touched.remove(id)
        touched.add(0, id)
        if (touched.size > GameConfig.RECENT_LIMIT * 2) {
            touched.removeAt(touched.size - 1)
        }
    }

    fun clearFilters() {
        queryState.value = ""
        categoryState.value = null
    }

    fun restoreSamples() {
        archiveRepository.reset()
    }

    fun finishSession() {
        stopSession()
        val items = archiveRepository.items.value
        summaryState.value = buildSummary(items, touched.toList(), elapsedState.value)
    }

    fun consumeSummary() {
        summaryState.value = null
    }

    override fun onCleared() {
        stopSession()
        super.onCleared()
    }
}
