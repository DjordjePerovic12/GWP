package com.global_water_polo.play_abroad.core.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.resources.Res
import com.global_water_polo.play_abroad.resources.error_field_empty
import com.global_water_polo.play_abroad.resources.error_invalid_email
import com.global_water_polo.play_abroad.resources.error_invalid_password
import com.global_water_polo.play_abroad.resources.error_invalid_phone
import com.global_water_polo.play_abroad.resources.error_invalid_url
import com.global_water_polo.play_abroad.resources.error_invalid_username
import com.global_water_polo.play_abroad.resources.error_max_length
import com.global_water_polo.play_abroad.resources.error_min_length
import com.global_water_polo.play_abroad.resources.error_only_spaces
import com.global_water_polo.play_abroad.resources.error_special_characters

/**
  Core utility functions, for usage examples check ExampleScreen and LoginViewModel
 **/

/**
  Composable function that observes a Flow with lifecycle awareness.
  Automatically handles collection and cancellation based on the lifecycle state.

  @param T The type of data flowing through the Flow
  @param lifecycleOwner The lifecycle owner to respect (defaults to LocalLifecycleOwner)
  @param minActiveState Minimum lifecycle state when collection should occur (defaults to STARTED)
  @param action The action to perform when a value is collected
 **/
@Composable
inline fun <reified T> Flow<T>.observeWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.lifecycleScope.launch {
            flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect(action)
        }
    }
}

/**
  Extension function for String to validate text input with various rules.

  @param minLength Minimum required length (default: 0)
  @param maxLength Maximum allowed length (default: Int.MAX_VALUE)
  @param isRequired Whether the field is required/can't be empty (default: true)
  @param format Optional format validation (email, phone, etc.)
  @param allowLeadingWhitespaces Whether to allow spaces at the start (default: false)
  @param allowTrailingWhitespaces Whether to allow spaces at the end (default: false)
  @param allowOnlyWhitespaces Whether to allow input of only spaces (default: false)
  @param allowSpecialCharacters Whether to allow special characters (default: true)
  @param stringProvider instance of StringProvider interface

  @return ValidationResult containing success status and error message if any

 For usage example check LoginViewModel
 **/

suspend fun String.validateInput(
    fieldName: String,
    minLength: Int = 0,
    maxLength: Int = Int.MAX_VALUE,
    isRequired: Boolean = true,
    format: InputFormat? = null,
    allowLeadingWhitespaces: Boolean = false,
    allowTrailingWhitespaces: Boolean = false,
    allowOnlyWhitespaces: Boolean = false,
    allowSpecialCharacters: Boolean = true,
    stringProvider: StringProvider
): ValidationResult {
    val errorMessages = ValidationErrorMessages(
        fieldName = fieldName,
        fieldEmptyError = stringProvider.getStringWithArgs(Res.string.error_field_empty, fieldName),
        onlySpacesError = stringProvider.getStringWithArgs(Res.string.error_only_spaces, fieldName),
        minLengthError = stringProvider.getStringWithArgs(
            Res.string.error_min_length,
            fieldName,
            minLength
        ),
        maxLengthError = stringProvider.getStringWithArgs(
            Res.string.error_max_length,
            fieldName,
            maxLength
        ),
        specialCharactersError = stringProvider.getStringWithArgs(
            Res.string.error_special_characters,
            fieldName
        ),
        formatError = when (format) {
            InputFormat.EMAIL -> stringProvider.getStringWithArgs(
                Res.string.error_invalid_email,
                fieldName
            )

            InputFormat.PASSWORD -> stringProvider.getStringWithArgs(
                Res.string.error_invalid_password,
                fieldName,
                minLength
            )

            InputFormat.PHONE -> stringProvider.getStringWithArgs(
                Res.string.error_invalid_phone,
                fieldName
            )

            InputFormat.USERNAME -> stringProvider.getStringWithArgs(
                Res.string.error_invalid_username,
                fieldName,
                minLength,
                maxLength
            )

            InputFormat.URL -> stringProvider.getStringWithArgs(
                Res.string.error_invalid_url,
                fieldName
            )

            null -> stringProvider.getStringWithArgs(Res.string.error_invalid_url, fieldName)
        }
    )

    val inputText = if (!allowLeadingWhitespaces || !allowTrailingWhitespaces) {
        when {
            !allowLeadingWhitespaces && !allowTrailingWhitespaces -> trim()
            !allowLeadingWhitespaces -> trimStart()
            else -> trimEnd()
        }
    } else this

    // Required field check
    if (isRequired && inputText.isEmpty()) {
        return ValidationResult.Error(errorMessages.fieldEmptyError)
    }

    // Only whitespaces check
    if (!allowOnlyWhitespaces && inputText.isNotEmpty() && inputText.all { it.isWhitespace() }) {
        return ValidationResult.Error(errorMessages.onlySpacesError)
    }

    // Length checks
    if (inputText.length < minLength) {
        return ValidationResult.Error(errorMessages.minLengthError)
    }
    if (inputText.length > maxLength) {
        return ValidationResult.Error(errorMessages.maxLengthError)
    }

    // Special characters check
    if (!allowSpecialCharacters && inputText.any { !it.isLetterOrDigit() && !it.isWhitespace() }) {
        return ValidationResult.Error(errorMessages.specialCharactersError)
    }

    // Format validation
    if (format != null && inputText.isNotEmpty()) {
        val isValid = when (format) {
            InputFormat.EMAIL -> inputText.isValidEmail()
            InputFormat.PASSWORD -> inputText.isValidPassword()
            InputFormat.PHONE -> inputText.isValidPhone()
            InputFormat.USERNAME -> inputText.isValidUsername()
            InputFormat.URL -> inputText.isValidUrl()
        }
        if (!isValid) {
            return ValidationResult.Error(errorMessages.formatError)
        }
    }

    return ValidationResult.Success
}

// Extension functions for specific format validations
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    return matches(emailRegex.toRegex())
}

//This isValidPassword() checks for a random password pattern
//TODO: Refactor so it matches the app password pattern once development starts
fun String.isValidPassword(): Boolean {
    val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
    return matches(passwordRegex.toRegex())
}

//This isValidPhone() checks for a general format of phone numbers
private fun String.isValidPhone(): Boolean {
    // Basic phone validation - can be adjusted based on requirements
    val phoneRegex = "^[+]?[0-9]{10,15}\$"
    return matches(phoneRegex.toRegex())
}

//This isValidUsername() checks for a random username pattern
//TODO: Refactor so it matches the app username pattern once development starts
private fun String.isValidUsername(): Boolean {
    val usernameRegex = "^[a-zA-Z0-9_]{3,20}\$"
    return matches(usernameRegex.toRegex())
}

private fun String.isValidUrl(): Boolean {
    val urlRegex = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$"
    return matches(urlRegex.toRegex())
}

fun Modifier.hideKeyboard(focusManager: FocusManager): Modifier = composed {
    pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })
    }
}

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}
