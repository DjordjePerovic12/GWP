package com.global_water_polo.play_abroad.core.utils

/**
  Data class representing validation error messages for form fields.
  Centralizes all possible validation error messages for a given field,
  supporting localization and custom error formatting.

  @property fieldName The name of the field being validated (e.g., "Email", "Password")
  Used for contextual error messages
 **/

data class ValidationErrorMessages(
    val fieldName: String,
    val fieldEmptyError: String,
    val onlySpacesError: String,
    val minLengthError: String,
    val maxLengthError: String,
    val specialCharactersError: String,
    val formatError: String
)
