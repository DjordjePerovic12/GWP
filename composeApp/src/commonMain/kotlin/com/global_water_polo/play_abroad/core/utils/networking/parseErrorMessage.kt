package com.global_water_polo.play_abroad.core.utils.networking

import com.global_water_polo.play_abroad.data.remote.dto.ApiErrorDto

/**
  Parses error message from API error response body.
  Safely converts JSON error response to readable message with fallback handling if the response is not standard JSON response.

  @param errorBody The JSON string from API error response, can be null
  @return Parsed error message or null if parsing fails
 **/

fun parseErrorMessage(errorBody: String?): String? {
    return try {
        errorBody?.let {
            networkJson.decodeFromString<ApiErrorDto>(it).message.ifEmpty { "An error occurred" }
        } ?: "Error body is null"
    } catch (e: Exception) {
        null
    }
}
