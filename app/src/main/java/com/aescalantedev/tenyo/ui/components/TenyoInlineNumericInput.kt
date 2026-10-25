package com.aescalantedev.tenyo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary

/**
 * Campo de texto numérico directo integrado dentro de las filas de series (Hevy / Strong style).
 *
 * Características de interacción:
 * - Mantiene el estado del texto localmente mientras tiene foco para permitir borrar completamente
 *   el campo con Backspace sin que el padre lo sobrescriba automáticamente con '0' o '1'.
 * - Selecciona todo el texto al recibir foco para facilitar sobrescritura rápida con un solo toque.
 * - Sincroniza con el padre solo cuando el usuario ingresa un valor válido o al perder el foco.
 */
@Composable
fun TenyoInlineNumericInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "-",
    isDecimal: Boolean = false,
    imeAction: ImeAction = ImeAction.Next
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    // Solo sincroniza desde el valor externo cuando el campo NO está enfocado
    LaunchedEffect(value, isFocused) {
        if (!isFocused && textFieldValue.text != value) {
            textFieldValue = TextFieldValue(text = value, selection = TextRange(value.length))
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newTfv ->
            val raw = newTfv.text
            val sanitized = if (isDecimal) {
                raw.filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
            } else {
                raw.filter { it.isDigit() || it == '-' }
            }

            textFieldValue = newTfv.copy(text = sanitized)

            // Si el texto tiene contenido válido, notificar al padre
            if (sanitized.isNotBlank()) {
                onValueChange(sanitized)
            }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = TenyoTextPrimary,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (textFieldValue.text.isBlank()) {
                    textFieldValue = TextFieldValue(value.ifBlank { "0" })
                    onValueChange(textFieldValue.text)
                }
                focusManager.clearFocus()
            },
            onNext = {
                if (textFieldValue.text.isBlank()) {
                    textFieldValue = TextFieldValue(value.ifBlank { "0" })
                    onValueChange(textFieldValue.text)
                }
                focusManager.clearFocus()
            }
        ),
        cursorBrush = SolidColor(TenyoJadePrimary),
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(TenyoSurfaceContainerHigh)
            .border(
                width = 1.dp,
                color = if (isFocused) TenyoJadePrimary else TenyoOutlineSubtle,
                shape = RoundedCornerShape(8.dp)
            )
            .onFocusChanged { focusState ->
                val wasFocused = isFocused
                isFocused = focusState.isFocused
                if (focusState.isFocused && !wasFocused) {
                    // Seleccionar todo al enfocar para permitir reemplazo inmediato
                    textFieldValue = textFieldValue.copy(
                        selection = TextRange(0, textFieldValue.text.length)
                    )
                } else if (!focusState.isFocused && wasFocused) {
                    // Al perder foco, si quedó en blanco restaurar valor previo o '0'
                    if (textFieldValue.text.isBlank()) {
                        val fallback = if (value.isNotBlank()) value else "0"
                        textFieldValue = TextFieldValue(fallback)
                        onValueChange(fallback)
                    }
                }
            }
            .padding(horizontal = 6.dp),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (textFieldValue.text.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TenyoTextTertiary,
                            textAlign = TextAlign.Center
                        )
                    )
                }
                innerTextField()
            }
        }
    )
}
