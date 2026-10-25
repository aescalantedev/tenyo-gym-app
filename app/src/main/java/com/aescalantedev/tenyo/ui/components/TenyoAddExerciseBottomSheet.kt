package com.aescalantedev.tenyo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutline
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.X

/**
 * Hoja modal reutilizable para buscar y anadir ejercicios a la rutina activa en tiempo real.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenyoAddExerciseBottomSheet(
    exercises: List<Exercise>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: BodyPartCategory?,
    onSelectCategory: (BodyPartCategory?) -> Unit,
    onAddExercise: (Exercise) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = TenyoObsidianBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .navigationBarsPadding()
        ) {
            // Drag handle superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp),
                    shape = RoundedCornerShape(2.dp),
                    color = TenyoOutlineSubtle
                ) {}
            }

            // Cabecera con titulo y boton cerrar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Añadir ejercicios",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = TenyoTextPrimary
                )
                Surface(
                    shape = CircleShape,
                    color = TenyoSurfaceContainerHigh,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDismissRequest)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Cerrar",
                            tint = TenyoTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Barra de busqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = {
                    Text(
                        text = "Buscar por nombre o musculo...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TenyoTextTertiary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = null,
                        tint = TenyoTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Limpiar busqueda",
                            tint = TenyoTextSecondary,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onSearchQueryChange("") }
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TenyoSurfaceContainer,
                    unfocusedContainerColor = TenyoSurfaceContainer,
                    focusedBorderColor = TenyoJadePrimary,
                    unfocusedBorderColor = TenyoOutlineSubtle,
                    focusedTextColor = TenyoTextPrimary,
                    unfocusedTextColor = TenyoTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Chips horizontales de categoria
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isAll = selectedCategory == null
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectCategory(null) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isAll) TenyoJadePrimary else TenyoSurfaceContainerHigh,
                        border = BorderStroke(1.dp, if (isAll) TenyoJadePrimary else TenyoOutlineSubtle)
                    ) {
                        Text(
                            text = "Todos",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isAll) TenyoOnJadePrimary else TenyoTextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }

                items(BodyPartCategory.entries) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectCategory(if (isSelected) null else category) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) TenyoJadePrimary else TenyoSurfaceContainerHigh,
                        border = BorderStroke(1.dp, if (isSelected) TenyoJadePrimary else TenyoOutlineSubtle)
                    ) {
                        Text(
                            text = category.nameEs,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) TenyoOnJadePrimary else TenyoTextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Listado de ejercicios
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = exercises,
                    key = { it.id }
                ) { exercise ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = TenyoSurfaceContainer,
                        border = BorderStroke(1.dp, TenyoOutlineSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TenyoSurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                if (exercise.imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = exercise.imageUrl,
                                        contentDescription = exercise.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(48.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Lucide.Dumbbell,
                                        contentDescription = null,
                                        tint = TenyoTextTertiary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exercise.name.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TenyoTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${exercise.bodyPart.nameEs} · ${exercise.equipment}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TenyoTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        onAddExercise(exercise)
                                        onDismissRequest()
                                    },
                                shape = CircleShape,
                                color = TenyoJadePrimary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Lucide.Plus,
                                        contentDescription = "Añadir a la sesión",
                                        tint = TenyoOnJadePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
