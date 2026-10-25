package com.aescalantedev.tenyo.ui.screens.progress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescalantedev.tenyo.core.domain.model.ProgressSummary
import com.aescalantedev.tenyo.core.domain.model.UserGender
import com.aescalantedev.tenyo.core.domain.usecase.ManageUserPreferencesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveProgressSummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth

/**
 * Estado de la pantalla de Progreso y Evolucion Fisica.
 *
 * Pestañas (`selectedSubTabIndex`):
 * - 0 = Resumen (KPIs entendibles: Entrenamientos, Volumen kg, Duracion + Rango Tenyo + Grafico semanal)
 * - 1 = Calendario (Calendario interactivo de dias entrenados con detalle por fecha)
 * - 2 = Recuperacion Muscular (Modelo anatómico interactivo con musculos estimulados + % de recuperacion con imagenes)
 * - 3 = Records PR (Marcas maximas reales registradas en Room)
 */
data class ProgressUiState(
    val selectedSubTabIndex: Int = 0,
    val currentCalendarMonth: YearMonth = YearMonth.now(),
    val selectedDayEpoch: Long? = null,
    val isTojiBackView: Boolean = false,
    val gender: UserGender = UserGender.MALE,
    val summary: ProgressSummary? = null
)

/**
 * ViewModel para la pantalla de Progreso y Records Personales.
 * Reactivo al 100% a la base de datos Room y preferencias del usuario, libre de datos mock.
 */
class ProgressViewModel(
    observeProgressSummaryUseCase: ObserveProgressSummaryUseCase,
    manageUserPreferencesUseCase: ManageUserPreferencesUseCase
) : ViewModel() {

    private val selectedSubTabIndex = MutableStateFlow(0)
    private val currentCalendarMonth = MutableStateFlow(YearMonth.now())
    private val selectedDayEpoch = MutableStateFlow<Long?>(null)
    private val isTojiBackView = MutableStateFlow(false)

    val uiState: StateFlow<ProgressUiState> = combine(
        combine(
            selectedSubTabIndex,
            currentCalendarMonth,
            selectedDayEpoch,
            isTojiBackView
        ) { tabIndex, month, selectedDay, tojiBackView ->
            ProgressUiState(
                selectedSubTabIndex = tabIndex,
                currentCalendarMonth = month,
                selectedDayEpoch = selectedDay,
                isTojiBackView = tojiBackView
            )
        },
        observeProgressSummaryUseCase(),
        manageUserPreferencesUseCase.preferences.map { it.gender }
    ) { baseState, summary, gender ->
        baseState.copy(
            gender = gender,
            summary = summary
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ProgressUiState()
    )

    fun selectSubTab(index: Int) {
        selectedSubTabIndex.value = index.coerceIn(0, 3)
    }

    fun nextMonth() {
        currentCalendarMonth.value = currentCalendarMonth.value.plusMonths(1)
    }

    fun previousMonth() {
        currentCalendarMonth.value = currentCalendarMonth.value.minusMonths(1)
    }

    fun selectCalendarDay(epochDay: Long?) {
        selectedDayEpoch.value = if (selectedDayEpoch.value == epochDay) null else epochDay
    }

    fun toggleTojiViewSide() {
        isTojiBackView.value = !isTojiBackView.value
    }
}
