package com.global_water_polo.play_abroad.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
    This is a template AuthenticationResponseDto so that the token refresh in HttpClientFactory can
    be set up with no errors
    TODO: Extend this data class to match the apps respective authentication response
 **/

@Serializable
data class AuthenticationResponseDto(
    @SerialName("accessToken") val accessToken: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null
)
