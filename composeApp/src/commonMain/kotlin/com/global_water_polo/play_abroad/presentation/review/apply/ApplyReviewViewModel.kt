package com.global_water_polo.play_abroad.presentation.review.apply

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/** Apply for GWP review: an identity clip and an in-water clip; submitting starts the free month. */
class ApplyReviewViewModel(
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<ApplyReviewState, ApplyReviewEvent>() {

    override val initialViewState = ApplyReviewState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val applyReviewState = state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                viewModelScope.launch {
                    profileRepository.profile.collect { p -> state.update { it.copy(uploads = p.uploads) } }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    fun onEvent(event: ApplyReviewEvent) {
        when (event) {
            ApplyReviewEvent.NavigateBack -> viewModelScope.launch { navigator.navigateUp() }
            //TODO: Real camera/gallery picking and upload; for now a tap marks the clip as added, like the prototype.
            is ApplyReviewEvent.OnClipClick -> profileRepository.update { it.copy(uploads = it.uploads + event.clip) }
            ApplyReviewEvent.OnSubmitClick -> if (state.value.canSubmit) {
                profileRepository.update { it.copy(reviewSubmitted = true) }
                viewModelScope.launch {
                    navigator.navigateTo(Screen.ReviewStatusScreen) {
                        popUpTo(Screen.ApplyReviewScreen) { inclusive = true }
                    }
                }
            }
        }
    }
}
