package com.aescalantedev.tenyo.ui.screens.plans.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.components.TenyoSkeletonBox
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow

/**
 * Vista Skeleton / Shimmer para la pantalla de Planes de Entrenamiento ([WorkoutPlansScreen]).
 * Proporciona un placeholder animado y suave idéntico a la estructura de la pantalla
 * mientras se leen los planes desde Room.
 */
@Composable
fun WorkoutPlansSkeleton(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TenyoObsidianBackground)
    ) {
        // 1. Cabecera superior: Título "Planes" + botón circular de búsqueda
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 16.dp, top = 8.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TenyoSkeletonBox(
                modifier = Modifier
                    .width(130.dp)
                    .height(34.dp),
                shape = RoundedCornerShape(8.dp)
            )

            TenyoSkeletonBox(
                modifier = Modifier.size(38.dp),
                shape = CircleShape
            )
        }

        // 2. Pestañas indicadoras (Mis rutinas / Explorar catálogo)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            TenyoSkeletonBox(
                modifier = Modifier
                    .width(90.dp)
                    .height(18.dp),
                shape = RoundedCornerShape(6.dp)
            )
            TenyoSkeletonBox(
                modifier = Modifier
                    .width(130.dp)
                    .height(18.dp),
                shape = RoundedCornerShape(6.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Contenido scrollable
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = contentPadding.calculateBottomPadding() + 92.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero card del plan activo
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TenyoSurfaceContainerLow,
                    border = BorderStroke(1.dp, HtmlBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            TenyoSkeletonBox(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(14.dp),
                                shape = RoundedCornerShape(4.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TenyoSkeletonBox(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(20.dp),
                                shape = RoundedCornerShape(6.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TenyoSkeletonBox(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(14.dp),
                                shape = RoundedCornerShape(4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        TenyoSkeletonBox(
                            modifier = Modifier.size(46.dp),
                            shape = CircleShape
                        )
                    }
                }
            }

            // Chips horizontales de objetivo
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(5) { index ->
                        val chipWidth = when (index) {
                            0 -> 64.dp
                            1 -> 80.dp
                            2 -> 96.dp
                            3 -> 90.dp
                            else -> 120.dp
                        }
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .width(chipWidth)
                                .height(36.dp),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // Lista de tarjetas de rutinas
            items(3) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = TenyoSurfaceContainerLow,
                    border = BorderStroke(1.dp, HtmlBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Recuadro izquierdo de días
                        TenyoSkeletonBox(
                            modifier = Modifier.size(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        // Título y duración
                        Column(modifier = Modifier.weight(1f)) {
                            TenyoSkeletonBox(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(18.dp),
                                shape = RoundedCornerShape(6.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TenyoSkeletonBox(
                                modifier = Modifier
                                    .fillMaxWidth(0.45f)
                                    .height(14.dp),
                                shape = RoundedCornerShape(4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Indicador icono 3 puntos
                        TenyoSkeletonBox(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape
                        )
                    }
                }
            }
        }
    }
}
