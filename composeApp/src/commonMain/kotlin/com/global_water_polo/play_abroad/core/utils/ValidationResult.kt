package com.global_water_polo.play_abroad.core.utils

/**
  Sealed class representing the result of a validation operation.
  Provides a type-safe way to handle validation outcomes with either success or error states.

  This class follows the Result pattern, making validation results explicit and forcing
  proper error handling through exhaustive when expressions.
 **/
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
