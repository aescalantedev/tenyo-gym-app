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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.components.TenyoSkeletonBox
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow

/**
 * Vista Skeleton / Shimmer para la pantalla de Detalle y Editor del Plan ([PlanDetailScreen]).
 * Ofrece retroalimentación visual inmediata con la estructura idéntica de la cabecera,
 * la tira semanal de 7 días, las tarjetas de configuración y los ejercicios programados.
 */
@Composable
fun PlanDetailSkeleton(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HtmlAppBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = contentPadding.calculateBottomPadding() + 92.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Cabecera superior: Botón Volver + Subtítulo + Título + Badge Activo
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TenyoSkeletonBox(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .width(110.dp)
                                .height(12.dp),
                            shape = RoundedCornerShape(4.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .height(22.dp),
                            shape = RoundedCornerShape(6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    TenyoSkeletonBox(
                        modifier = Modifier
                            .width(64.dp)
                            .height(26.dp),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            // 2. Tira semanal horizontal de 7 días
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(7) {
                        TenyoSkeletonBox(
                            modifier = Modifier
                                .width(46.dp)
                                .height(68.dp),
                            shape = RoundedCornerShape(18.dp)
                        )
                    }
                }
            }

            // 3. Tarjeta de resumen y enfoque del día
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = HtmlAppSurface,
                    border = BorderStroke(1.dp, HtmlBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            TenyoSkeletonBox(
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(11.dp),
                                shape = RoundedCornerShape(4.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TenyoSkeletonBox(
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(18.dp),
                                shape = RoundedCornerShape(6.dp)
                            )
                        }

                        TenyoSkeletonBox(
                            modifier = Modifier
                                .width(82.dp)
                                .height(32.dp),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // 4. Tarjeta de recordatorio y horario
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = HtmlAppSurface,
                    border = BorderStroke(1.dp, HtmlBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TenyoSkeletonBox(
                                modifier = Modifier.size(42.dp),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                TenyoSkeletonBox(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .height(16.dp),
                                shape = RoundedCornerShape(4.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                TenyoSkeletonBox(
                                    modifier = Modifier
                                        .width(75.dp)
                                        .height(14.dp),
                                    shape = RoundedCornerShape(4.dp)
                                )
                            }
                        }

                        TenyoSkeletonBox(
                            modifier = Modifier
                                .width(46.dp)
                                .height(26.dp),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
            }

            // 5. Título de sección: Ejercicios Programados
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TenyoSkeletonBox(
                        modifier = Modifier
                            .width(170.dp)
                            .height(20.dp),
                        shape = RoundedCornerShape(6.dp)
                    )

                    TenyoSkeletonBox(
                        modifier = Modifier
                            .width(80.dp)
                            .height(22.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 6. Tarjetas Skeleton de Ejercicios
            items(2) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = TenyoSurfaceContainerLow,
                    border = BorderStroke(1.dp, HtmlBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Cabecera ejercicio
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TenyoSkeletonBox(
                                modifier = Modifier.size(46.dp),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

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
                                        .fillMaxWidth(0.5f)
                                        .height(13.dp),
                                    shape = RoundedCornerShape(4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            TenyoSkeletonBox(
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Filas de series
                        repeat(3) {
                            TenyoSkeletonBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}
