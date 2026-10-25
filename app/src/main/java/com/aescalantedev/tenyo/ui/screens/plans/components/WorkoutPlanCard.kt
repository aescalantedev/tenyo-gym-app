package com.aescalantedev.tenyo.ui.screens.plans.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import com.aescalantedev.tenyo.ui.theme.TenyoError
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.EllipsisVertical
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play
import com.composables.icons.lucide.Trash2

/**
 * Tarjeta horizontal compacta y minimalista de Plan de Entrenamiento
 * idéntica a la maqueta de referencia.
 *
 * Responsabilidad única:
 * - Muestra la caja con el número de días ("4 DÍAS").
 * - Nombre del plan con etiqueta "ACTIVO" si corresponde.
 * - Subtítulo conciso (dificultad • semanas).
 * - Menú desplegable de 3 puntos (Activar, Ver rutina, Entrenar).
 */
@Composable
fun WorkoutPlanCard(
    plan: WorkoutPlan,
    onNavigateToDetail: () -> Unit,
    onActivatePlan: () -> Unit,
    onStartSession: () -> Unit,
    onDeletePlan: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = TenyoSurfaceContainerLow,
        border = BorderStroke(
            width = 1.dp,
            color = if (plan.isActivePlan) HtmlAppPrimary.copy(alpha = 0.35f) else HtmlBorderSubtle
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onNavigateToDetail)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Caja izquierda con número de días
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = TenyoSurfaceContainerHigh,
                border = BorderStroke(1.dp, TenyoOutlineSubtle),
                modifier = Modifier.size(52.dp)
            ) {
                Column(
                    modifier = Modifier.padding(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${plan.daysPerWeek}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp
                        ),
                        color = TenyoTextPrimary
                    )
                    Text(
                        text = "DÍAS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 0.8.sp
                        ),
                        color = TenyoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // 2. Información del plan
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = plan.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = TenyoTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (plan.isActivePlan) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = HtmlAppPrimary.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, HtmlAppPrimary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "ACTIVO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = HtmlAppPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                val difficultyLabel = plan.difficulty.labelEs.substringBefore(" · ").trim()
                Text(
                    text = "$difficultyLabel • ${plan.durationWeeks} semanas",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = TenyoTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3. Menú de opciones (3 puntos)
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Lucide.EllipsisVertical,
                        contentDescription = "Opciones",
                        tint = TenyoTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    DropdownMenuItem(
                        text = { Text("Ver rutina semanal") },
                        onClick = {
                            showMenu = false
                            onNavigateToDetail()
                        }
                    )

                    if (!plan.isActivePlan) {
                        DropdownMenuItem(
                            text = { Text("Activar como plan principal") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Lucide.Check,
                                    contentDescription = null,
                                    tint = HtmlAppPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onActivatePlan()
                            }
                        )
                    }

                    DropdownMenuItem(
                        text = { Text("Entrenar hoy") },
                        leadingIcon = {
                            Icon(
                                imageVector = Lucide.Play,
                                contentDescription = null,
                                tint = HtmlAppPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            onStartSession()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Eliminar rutina", color = TenyoError) },
                        leadingIcon = {
                            Icon(
                                imageVector = Lucide.Trash2,
                                contentDescription = null,
                                tint = TenyoError,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            onDeletePlan()
                        }
                    )
                }
            }
        }
    }
}
