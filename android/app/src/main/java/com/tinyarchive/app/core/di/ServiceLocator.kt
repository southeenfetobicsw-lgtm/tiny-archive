package com.tinyarchive.app.core.di

import android.content.Context
import com.tinyarchive.app.data.local.ArchivePreferences
import com.tinyarchive.app.data.local.SettingsPreferences
import com.tinyarchive.app.data.repository.ArchiveRepositoryImpl
import com.tinyarchive.app.data.repository.SettingsRepositoryImpl
import com.tinyarchive.app.domain.repository.ArchiveRepository
import com.tinyarchive.app.domain.repository.SettingsRepository
import com.tinyarchive.app.domain.usecase.BuildSessionSummaryUseCase
import com.tinyarchive.app.domain.usecase.DeleteItemUseCase
import com.tinyarchive.app.domain.usecase.GetItemsUseCase
import com.tinyarchive.app.domain.usecase.GetStatsUseCase
import com.tinyarchive.app.domain.usecase.ManageCategoriesUseCase
import com.tinyarchive.app.domain.usecase.SaveItemUseCase
import com.tinyarchive.app.domain.usecase.SearchItemsUseCase

object ServiceLocator {

    private var archive: ArchiveRepository? = null
    private var settings: SettingsRepository? = null
    private var saveItem: SaveItemUseCase? = null
    private var deleteItem: DeleteItemUseCase? = null
    private var manageCategories: ManageCategoriesUseCase? = null

    private val getItems = GetItemsUseCase()
    private val searchItems = SearchItemsUseCase()
    private val getStats = GetStatsUseCase()
    private val buildSummary = BuildSessionSummaryUseCase()

    fun init(context: Context) {
        if (archive != null) {
            return
        }
        val repository = ArchiveRepositoryImpl(ArchivePreferences(context))
        archive = repository
        settings = SettingsRepositoryImpl(SettingsPreferences(context))
        saveItem = SaveItemUseCase(repository)
        deleteItem = DeleteItemUseCase(repository)
        manageCategories = ManageCategoriesUseCase(repository)
    }

    fun archiveRepository(): ArchiveRepository =
        requireNotNull(archive) { "ServiceLocator not initialised" }

    fun settingsRepository(): SettingsRepository =
        requireNotNull(settings) { "ServiceLocator not initialised" }

    fun saveItemUseCase(): SaveItemUseCase =
        requireNotNull(saveItem) { "ServiceLocator not initialised" }

    fun deleteItemUseCase(): DeleteItemUseCase =
        requireNotNull(deleteItem) { "ServiceLocator not initialised" }

    fun manageCategoriesUseCase(): ManageCategoriesUseCase =
        requireNotNull(manageCategories) { "ServiceLocator not initialised" }

    fun getItemsUseCase(): GetItemsUseCase = getItems

    fun searchItemsUseCase(): SearchItemsUseCase = searchItems

    fun getStatsUseCase(): GetStatsUseCase = getStats

    fun buildSessionSummaryUseCase(): BuildSessionSummaryUseCase = buildSummary
}
