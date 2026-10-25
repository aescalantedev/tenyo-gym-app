package com.aescalantedev.tenyo.ui.screens.exercises.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary

/**
 * Diálogo modal para registrar un nuevo ejercicio personalizado en la base de datos local.
 */
@Composable
fun CreateCustomExerciseDialog(
    initialBodyPart: BodyPartCategory,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        bodyPart: BodyPartCategory,
        equipment: String,
        targetMuscle: String,
        instructions: String
    ) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var equipment by rememberSaveable { mutableStateOf("barbell") }
    var targetMuscle by rememberSaveable { mutableStateOf(initialBodyPart.nameEs) }
    var instructions by rememberSaveable {
        mutableStateOf("1. Adoptar postura firme con core activo.\n2. Ejecutar fase concéntrica explosiva y excéntrica controlada.")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TenyoSurfaceContainerHigh,
        title = {
            Text(
                text = "Nuevo ejercicio Tenyo",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TenyoTextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del ejercicio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = equipment,
                    onValueChange = { equipment = it },
                    label = { Text("Equipamiento (ej. barbell, dumbbell, body weight)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetMuscle,
                    onValueChange = { targetMuscle = it },
                    label = { Text("Músculo principal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Pasos de ejecución") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, initialBodyPart, equipment, targetMuscle, instructions)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TenyoJadePrimary,
                    contentColor = TenyoOnJadePrimary
                )
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TenyoTextSecondary)
            }
        }
    )
}
