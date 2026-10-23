package com.global_water_polo.play_abroad.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
Example DTO created for testing networking
 **/

@Serializable
data class TestResponseDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null
)
