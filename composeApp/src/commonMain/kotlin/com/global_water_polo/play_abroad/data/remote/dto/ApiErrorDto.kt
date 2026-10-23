package com.global_water_polo.play_abroad.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
    This is the way our backend usually sends us error messages
 **/

@Serializable
data class ApiErrorDto(
    @SerialName("message")
    val message: String = ""
)
