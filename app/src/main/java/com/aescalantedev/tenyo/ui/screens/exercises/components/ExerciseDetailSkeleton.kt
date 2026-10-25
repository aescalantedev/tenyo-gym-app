package com.aescalantedev.tenyo.ui.screens.exercises.components

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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow

/**
 * Vista Skeleton / Shimmer para la pantalla rediseñada de Detalle del Ejercicio.
 * Sincronizada con el Hero Media inmersivo a sangre, TopBar flotante, Tabs y Bento grid.
 */
@Composable
fun ExerciseDetailSkeleton(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TenyoObsidianBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Hero Media Inmersivo Skeleton (300.dp)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    TenyoSkeletonBox(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(0.dp)
                    )
                }
            }

            // 2. Título, Badges y Selector de Pestañas
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TenyoSkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(28.dp),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .width(80.dp)
                                .height(26.dp),
                            shape = RoundedCornerShape(999.dp)
                        )
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .width(95.dp)
                                .height(26.dp),
                            shape = RoundedCornerShape(999.dp)
                        )
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .width(110.dp)
                                .height(26.dp),
                            shape = RoundedCornerShape(999.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Segmented Tabs Placeholder
                    TenyoSkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            // 3. Bento Grid (2x2) de Métricas
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .weight(1f)
                                .height(68.dp),
                            shape = RoundedCornerShape(16.dp)
                        )
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .weight(1f)
                                .height(68.dp),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .weight(1f)
                                .height(68.dp),
                            shape = RoundedCornerShape(16.dp)
                        )
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .weight(1f)
                                .height(68.dp),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            // 4. Músculos Involucrados - Fila desplazable Skeleton
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .width(160.dp)
                                .height(20.dp),
                            shape = RoundedCornerShape(6.dp)
                        )
                    }

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(3) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = TenyoSurfaceContainer,
                                border = BorderStroke(1.dp, TenyoOutlineSubtle),
                                modifier = Modifier.width(150.dp)
                            ) {
                                Column {
                                    TenyoSkeletonBox(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(105.dp),
                                        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                                    )
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        TenyoSkeletonBox(
                                            modifier = Modifier
                                                .width(80.dp)
                                                .height(14.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        TenyoSkeletonBox(
                                            modifier = Modifier
                                                .width(60.dp)
                                                .height(10.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Stepper vertical Skeleton
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TenyoSurfaceContainer,
                    border = BorderStroke(1.dp, TenyoOutlineSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .width(200.dp)
                                .height(20.dp),
                            shape = RoundedCornerShape(6.dp)
                        )

                        repeat(3) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                TenyoSkeletonBox(
                                    modifier = Modifier.size(30.dp),
                                    shape = CircleShape
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TenyoSkeletonBox(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(14.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    TenyoSkeletonBox(
                                        modifier = Modifier
                                            .fillMaxWidth(0.7f)
                                            .height(14.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // TopBar Flotante Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TenyoSkeletonBox(
                modifier = Modifier.size(42.dp),
                shape = CircleShape
            )
            TenyoSkeletonBox(
                modifier = Modifier.size(42.dp),
                shape = CircleShape
            )
        }

        // Barra Inferior de Acciones Skeleton
        Surface(
            color = TenyoSurfaceContainerLow,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TenyoSkeletonBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                )
                TenyoSkeletonBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }
    }
}
