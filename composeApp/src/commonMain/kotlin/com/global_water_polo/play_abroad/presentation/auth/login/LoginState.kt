package com.global_water_polo.play_abroad.presentation.auth.login

import com.global_water_polo.play_abroad.presentation.base.BaseError
import com.global_water_polo.play_abroad.presentation.base.BaseViewState

data class LoginState(
    override val isLoading: Boolean = false,
    override val error: BaseError? = null,
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSuccess: Boolean = false
) : BaseViewState(isLoading, error)
