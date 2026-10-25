package com.aescalantedev.tenyo.ui.screens.categories.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescalantedev.tenyo.core.domain.model.CategoryRegion
import com.aescalantedev.tenyo.core.domain.model.CategorySummary
import com.aescalantedev.tenyo.core.domain.usecase.ObserveCategorySummariesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/**
 * Estado de la pantalla de Exploración de Categorías (Stitch 03).
 */
data class CategoriesUiState(
    val isLoading: Boolean = false,
    val selectedRegion: CategoryRegion = CategoryRegion.ALL,
    val searchQuery: String = "",
    val categories: List<CategorySummary> = emptyList(),
    val totalCatalogCount: Int = 1324
)

/**
 * ViewModel para la pantalla de Explorar Categorías (Stitch 03).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModel(
    private val observeCategorySummariesUseCase: ObserveCategorySummariesUseCase
) : ViewModel() {

    private val selectedRegion = MutableStateFlow(CategoryRegion.ALL)
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<CategoriesUiState> = combine(
        selectedRegion,
        searchQuery
    ) { region, query ->
        region to query
    }.flatMapLatest { (region, query) ->
        observeCategorySummariesUseCase(region = region, searchQuery = query)
            .combine(flowOf(region to query)) { list, (reg, q) ->
                CategoriesUiState(
                    isLoading = false,
                    selectedRegion = reg,
                    searchQuery = q,
                    categories = list,
                    totalCatalogCount = list.sumOf { it.exerciseCount }.takeIf { it > 0 } ?: 1324
                )
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoriesUiState(isLoading = true)
    )

    fun selectRegion(region: CategoryRegion) {
        selectedRegion.value = region
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }
}
