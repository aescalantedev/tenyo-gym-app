package com.aescalantedev.tenyo.core.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Objeto de transferencia de datos (DTO) para deserializar cada registro del archivo
 * `assets/exercises.json` proveniente del repositorio `hasaneyldrm/exercises-dataset`.
 *
 * Sigue estrictamente la definicion de `exercises.schema.json`.
 */
@Serializable
data class ExerciseSeedDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("category")
    val category: String = "",
    @SerialName("body_part")
    val bodyPart: String,
    @SerialName("equipment")
    val equipment: String = "body weight",
    @SerialName("target")
    val target: String = "",
    @SerialName("muscle_group")
    val muscleGroup: String = "",
    @SerialName("secondary_muscles")
    val secondaryMuscles: List<String> = emptyList(),
    @SerialName("instructions")
    val instructions: Map<String, String> = emptyMap(),
    @SerialName("instruction_steps")
    val instructionSteps: Map<String, List<String>> = emptyMap(),
    @SerialName("media_id")
    val mediaId: String = "",
    @SerialName("image")
    val image: String = "",
    @SerialName("gif_url")
    val gifUrl: String = ""
)
