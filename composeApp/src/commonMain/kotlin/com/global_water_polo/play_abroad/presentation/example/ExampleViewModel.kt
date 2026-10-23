package com.global_water_polo.play_abroad.presentation.example

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.utils.networking.NetworkError
import com.global_water_polo.play_abroad.core.utils.networking.Resource
import com.global_water_polo.play_abroad.core.utils.networking.onError
import com.global_water_polo.play_abroad.core.utils.networking.onSuccess
import com.global_water_polo.play_abroad.domain.repository.BaseRepository
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/**
IMPORTANT!
Example feature showing the full screen set (Screen, ScreenContent, ViewModel, State, Event),
networking through the repository and navigation through Navigator.
All of the error functions call mock endpoints that only return errors,
check ApiService to see what each returns.
 **/

//TODO: Delete or rename once development starts

class ExampleViewModel(
    private val repository: BaseRepository,
    private val navigator: Navigator
) : BaseViewModel<ExampleState, ExampleEvent>() {

    override val initialViewState = ExampleState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val exampleState = state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                getDataResponseTest()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    fun onEvent(event: ExampleEvent) {
        when (event) {
            ExampleEvent.OnDataResponseClick -> getDataResponseTest()
            ExampleEvent.OnEmptyResponseClick -> showErrorOf { repository.getEmptyErrorTest() }
            ExampleEvent.OnHTMLResponseClick -> showErrorOf { repository.getHTMLErrorTest() }
            ExampleEvent.OnCustomMessageResponseClick -> showErrorOf { repository.getCustomMessageErrorTest() }
            ExampleEvent.OnLoginDemoClick -> viewModelScope.launch {
                navigator.navigateTo(Screen.LoginScreen)
            }
        }
    }

    private fun getDataResponseTest() {
        viewModelScope.launch {
            state.update { it.copy(isLoading = true) }
            repository.getDataResponseTest()
                .onSuccess { result ->
                    state.update { it.copy(isLoading = false, testData = result) }
                }
                .onError { networkError, errorMessage ->
                    Logger.e { "NE $networkError, EM $errorMessage" }
                    state.update { it.copy(isLoading = false) }
                    _snackBarChannel.send(errorMessage ?: networkError.name)
                }
        }
    }

    private fun showErrorOf(call: suspend () -> Resource<Unit, NetworkError, String?>) {
        viewModelScope.launch {
            state.update { it.copy(isLoading = true) }
            call()
                .onSuccess {
                    state.update { it.copy(isLoading = false) }
                }
                .onError { networkError, errorMessage ->
                    Logger.e { "NE $networkError, EM $errorMessage" }
                    state.update { it.copy(isLoading = false) }
                    _snackBarChannel.send(errorMessage ?: networkError.name)
                }
        }
    }
}
