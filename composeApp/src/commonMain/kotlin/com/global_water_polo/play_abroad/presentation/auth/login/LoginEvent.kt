package com.global_water_polo.play_abroad.presentation.auth.login

import com.global_water_polo.play_abroad.presentation.base.BaseEvent

sealed class LoginEvent : BaseEvent {
    data class OnEmailChanged(val email: String) : LoginEvent()
    data class OnPasswordChanged(val password: String) : LoginEvent()
    data object OnLoginClick : LoginEvent()
    data object NavigateBack : LoginEvent()
}
