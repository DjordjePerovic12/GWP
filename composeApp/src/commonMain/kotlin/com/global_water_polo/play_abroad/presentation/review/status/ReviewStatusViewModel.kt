package com.global_water_polo.play_abroad.presentation.review.status

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/** What the athlete sees while GWP coaches review their clips. */
class ReviewStatusViewModel(
    private val navigator: Navigator
) : BaseViewModel<ReviewStatusState, ReviewStatusEvent>() {

    override val initialViewState = ReviewStatusState()

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val reviewStatusState = state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    fun onEvent(event: ReviewStatusEvent) {
        when (event) {
            ReviewStatusEvent.OnBackToProfileClick -> viewModelScope.launch { navigator.navigateUp() }
        }
    }
}
