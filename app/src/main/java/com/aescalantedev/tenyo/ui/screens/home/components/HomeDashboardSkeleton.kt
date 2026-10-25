package com.aescalantedev.tenyo.ui.screens.home.components

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
 * Vista Skeleton / Shimmer para la pantalla de Inicio ([HomeScreen]).
 * Provee una transicion visual fluida y sin saltos de layout mientras se cargan
 * los datos desde Room y DataStore.
 */
@Composable
fun HomeDashboardSkeleton(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TenyoObsidianBackground)
    ) {
        // Cabecera Skeleton con statusBarsPadding
        Surface(
            color = TenyoSurfaceContainerLow,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TenyoSkeletonBox(
                        modifier = Modifier
                            .width(110.dp)
                            .height(18.dp),
                        shape = RoundedCornerShape(999.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TenyoSkeletonBox(
                        modifier = Modifier
                            .width(160.dp)
                            .height(26.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TenyoSkeletonBox(
                        modifier = Modifier
                            .width(220.dp)
                            .height(14.dp),
                        shape = RoundedCornerShape(6.dp)
                    )
                }

                TenyoSkeletonBox(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape
                )
            }
        }

        // Lista de elementos Shimmer
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 92.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Perfil Biometrico Skeleton
            item {
                TenyoSkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    shape = RoundedCornerShape(18.dp)
                )
            }

            // 2. Resumen Semanal Skeleton
            item {
                TenyoSkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
                    shape = RoundedCornerShape(18.dp)
                )
            }

            // 3. Tarjeta de Sesion Reciente Skeleton
            item {
                TenyoSkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(20.dp)
                )
            }

            // 4. Categorias Musculares Skeleton (Fila)
            item {
                Column {
                    TenyoSkeletonBox(
                        modifier = Modifier
                            .width(160.dp)
                            .height(18.dp),
                        shape = RoundedCornerShape(6.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        repeat(3) {
                            TenyoSkeletonBox(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp)
                            )
                        }
                    }
                }
            }

            // 5. Ejercicios Clave Skeleton (3 filas)
            item {
                TenyoSkeletonBox(
                    modifier = Modifier
                        .width(180.dp)
                        .height(18.dp),
                    shape = RoundedCornerShape(6.dp)
                )
            }

            items(3) {
                TenyoSkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}
