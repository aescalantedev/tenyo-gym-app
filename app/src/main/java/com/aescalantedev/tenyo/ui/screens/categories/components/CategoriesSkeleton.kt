package com.aescalantedev.tenyo.ui.screens.categories.components

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
 * Vista Skeleton / Shimmer para la pantalla de Explorar Categorías ([CategoriesScreen]).
 * Provee una transición visual fluida y sin saltos de layout mientras se cargan los datos.
 */
@Composable
fun CategoriesSkeleton(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TenyoObsidianBackground)
    ) {
        // Cabecera superior Skeleton
        Surface(
            color = TenyoObsidianBackground,
            border = BorderStroke(1.dp, TenyoOutlineSubtle.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Barra de búsqueda Skeleton
                TenyoSkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Pestañas Skeleton
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            TenyoSkeletonBox(
                                modifier = Modifier.size(20.dp),
                                shape = CircleShape
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TenyoSkeletonBox(
                                modifier = Modifier
                                    .width(64.dp)
                                    .height(12.dp),
                                shape = RoundedCornerShape(4.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TenyoSkeletonBox(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(2.5.dp),
                                shape = RoundedCornerShape(2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Grilla Skeleton de categorías y músculos
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título de Sección Skeleton
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TenyoSkeletonBox(
                        modifier = Modifier
                            .width(140.dp)
                            .height(20.dp),
                        shape = RoundedCornerShape(6.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TenyoSkeletonBox(
                        modifier = Modifier
                            .width(28.dp)
                            .height(18.dp),
                        shape = CircleShape
                    )
                }
            }

            // 4 filas de 3 tarjetas de músculos skeleton (12 tarjetas)
            items(4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = TenyoSurfaceContainerLow,
                            border = BorderStroke(1.dp, TenyoOutlineSubtle),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TenyoSkeletonBox(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(96.dp),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TenyoSkeletonBox(
                                    modifier = Modifier
                                        .width(55.dp)
                                        .height(12.dp),
                                    shape = RoundedCornerShape(4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Título de Sección "Por equipamiento" Skeleton
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 20.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TenyoSkeletonBox(
                        modifier = Modifier
                            .width(130.dp)
                            .height(20.dp),
                        shape = RoundedCornerShape(6.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TenyoSkeletonBox(
                        modifier = Modifier
                            .width(28.dp)
                            .height(18.dp),
                        shape = CircleShape
                    )
                }
            }

            // 2 filas de 3 tarjetas de equipamiento skeleton
            items(2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = TenyoSurfaceContainerLow,
                            border = BorderStroke(1.dp, TenyoOutlineSubtle),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TenyoSkeletonBox(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(96.dp),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TenyoSkeletonBox(
                                    modifier = Modifier
                                        .width(60.dp)
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
