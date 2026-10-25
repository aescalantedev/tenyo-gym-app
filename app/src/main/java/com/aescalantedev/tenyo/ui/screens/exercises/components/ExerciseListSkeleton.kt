package com.aescalantedev.tenyo.ui.screens.exercises.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.components.TenyoSkeletonBox
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow

/**
 * Vista Skeleton / Shimmer para la pantalla del Catálogo de Ejercicios ([ExerciseListScreen]).
 * Se muestra mientras se consultan y ordenan los ejercicios en la base de datos Room.
 */
@Composable
fun ExerciseListSkeleton(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TenyoObsidianBackground)
    ) {
        // 1. Barra Superior Skeleton
        Surface(
            color = TenyoSurfaceContainerLow,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TenyoSkeletonBox(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape
                )
                TenyoSkeletonBox(
                    modifier = Modifier
                        .width(130.dp)
                        .height(20.dp),
                    shape = RoundedCornerShape(6.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TenyoSkeletonBox(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape
                    )
                    TenyoSkeletonBox(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Carrusel Horizontal Skeleton (Fichas musculares)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(7) {
                item {
                    TenyoSkeletonBox(
                        modifier = Modifier.size(58.dp),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Barra de Subtítulo Skeleton (Ordenamiento + Botón toggle vista)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TenyoSkeletonBox(
                modifier = Modifier
                    .width(120.dp)
                    .height(30.dp),
                shape = RoundedCornerShape(8.dp)
            )
            TenyoSkeletonBox(
                modifier = Modifier.size(30.dp),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // 4. Cuadrícula Skeleton (2 columnas de tarjetas de ejercicios)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    repeat(2) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = TenyoSurfaceContainerLow,
                            border = BorderStroke(1.dp, TenyoOutlineSubtle),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                // Miniatura del ejercicio
                                TenyoSkeletonBox(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(115.dp),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                // Nombre
                                TenyoSkeletonBox(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(14.dp),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                // Músculo objetivo
                                TenyoSkeletonBox(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .height(12.dp),
                                    shape = RoundedCornerShape(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
