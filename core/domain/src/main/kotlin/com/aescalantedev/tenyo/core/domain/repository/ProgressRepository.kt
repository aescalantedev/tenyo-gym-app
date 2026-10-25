package com.aescalantedev.tenyo.core.domain.repository

import com.aescalantedev.tenyo.core.domain.model.ProgressSummary
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para estadísticas de Progreso, Distribución Muscular,
 * Actividad Semanal, Récords Personales (PRs) y Rango Físico Tenyo (Stitch 08).
 */
interface ProgressRepository {
    /**
     * Observa en tiempo real todas las métricas de evolución física del usuario.
     */
    fun observeProgressSummary(): Flow<ProgressSummary>
}
