package com.global_water_polo.play_abroad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.domain.repository.DataStoreRepository

//TODO: Refactor regarding needs of your app (splash/auth gate, app wide snack bars, language)

class MainViewModel(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val state = MutableStateFlow(MainState())
    val viewStateFlow: StateFlow<MainState> = state

    private val _snackBarChannel = Channel<String>()
    val snackBarChannel = _snackBarChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            dataStoreRepository.getPreferredLanguage().collect { languageTag ->
                state.update { it.copy(languageTag = languageTag.ifBlank { null }) }
            }
        }
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnLanguageChange -> viewModelScope.launch {
                dataStoreRepository.savePreferredLanguage(event.languageTag)
            }
        }
    }
}

data class MainState(
    val languageTag: String? = null
)

sealed class MainEvent {
    data class OnLanguageChange(val languageTag: String) : MainEvent()
}
