package com.global_water_polo.play_abroad.presentation.auth.login

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.utils.InputFormat
import com.global_water_polo.play_abroad.core.utils.StringProvider
import com.global_water_polo.play_abroad.core.utils.ValidationResult
import com.global_water_polo.play_abroad.core.utils.isValidEmail
import com.global_water_polo.play_abroad.core.utils.isValidPassword
import com.global_water_polo.play_abroad.core.utils.validateInput
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/**
  Starting implementation of login logic for development/testing purposes.
  This can be used as a starting point of any LoginViewModel, because the real one
  won't differentiate much. Extend with more necessary logic when in development.

  IMPORTANT: Always run LoginViewModelTest (./gradlew :composeApp:allTests) before sending Login out to QA,
  if all pass, we are good to go, if any fail more work needs to be done.

  Currently validates:
  - Email format
  - Password requirements
  - No validation errors present

  TODO: Replace with actual API login implementation
  Notes for implementation:
  1. Add API client/repository dependency
  2. Make network call to login endpoint
  3. Handle API response and errors
  4. Store auth token if successful
 **/

class LoginViewModel(
    private val stringProvider: StringProvider,
    private val navigator: Navigator
) : BaseViewModel<LoginState, LoginEvent>() {

    override val initialViewState = LoginState()

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val loginState = state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> {
                // Text is updated synchronously so the TextField never drops characters,
                // the (suspending) validation follows and is ignored if the text changed meanwhile.
                state.update { it.copy(email = event.email) }
                viewModelScope.launch {
                    val validationResult = event.email.validateInput(
                        fieldName = "Email",
                        format = InputFormat.EMAIL,
                        stringProvider = stringProvider
                    )

                    state.update {
                        if (it.email != event.email) it
                        else it.copy(emailError = (validationResult as? ValidationResult.Error)?.message)
                    }
                }
            }

            is LoginEvent.OnPasswordChanged -> {
                state.update { it.copy(password = event.password) }
                viewModelScope.launch {
                    val validationResult = event.password.validateInput(
                        fieldName = "Password",
                        format = InputFormat.PASSWORD,
                        minLength = 8,
                        stringProvider = stringProvider
                    )

                    state.update {
                        if (it.password != event.password) it
                        else it.copy(passwordError = (validationResult as? ValidationResult.Error)?.message)
                    }
                }
            }

            LoginEvent.OnLoginClick -> viewModelScope.launch {
                if (validateFields()) {
                    login()
                }
            }

            LoginEvent.NavigateBack -> viewModelScope.launch {
                navigator.navigateUp()
            }
        }
    }

    private suspend fun validateFields(): Boolean {
        val emailValidation = state.value.email.validateInput(
            "Email",
            format = InputFormat.EMAIL,
            stringProvider = stringProvider
        )

        val passwordValidation = state.value.password.validateInput(
            "Password",
            format = InputFormat.PASSWORD,
            minLength = 8,
            stringProvider = stringProvider
        )

        state.update {
            it.copy(
                emailError = (emailValidation as? ValidationResult.Error)?.message,
                passwordError = (passwordValidation as? ValidationResult.Error)?.message
            )
        }

        return emailValidation is ValidationResult.Success &&
                passwordValidation is ValidationResult.Success
    }

    /**
      Mock implementation of login logic for development/testing purposes.
      TODO: Replace with actual API login implementation
     **/
    private fun login() {
        state.update { it.copy(isLoading = true) }

        val current = state.value
        val isValid = current.email.isValidEmail() && current.emailError == null &&
                current.password.isValidPassword() && current.passwordError == null
        state.update { it.copy(isLoading = false, isSuccess = isValid) }
    }
}
