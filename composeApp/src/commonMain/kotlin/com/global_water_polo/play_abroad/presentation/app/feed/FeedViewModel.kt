package com.global_water_polo.play_abroad.presentation.app.feed

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.navigation.utils.openTab
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository
import com.global_water_polo.play_abroad.domain.repository.cancelPlan
import com.global_water_polo.play_abroad.domain.repository.startPlan
import com.global_water_polo.play_abroad.domain.repository.switchToNext
import com.global_water_polo.play_abroad.presentation.base.BaseViewModel

/** Feed tab: video-first stories, the course library (Elite opens all of it) and what was saved. */
class FeedViewModel(
    private val profileRepository: ProfileRepository,
    private val navigator: Navigator
) : BaseViewModel<FeedState, FeedEvent>() {

    override val initialViewState = FeedState()
    private var hasLoadedInitialData = false

    override fun dismissError() {
        state.update { it.copy(error = null) }
    }

    val feedState = state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                viewModelScope.launch {
                    profileRepository.profile.collect { p -> state.update { it.copy(profile = p) } }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = state.value
        )

    fun onEvent(event: FeedEvent) {
        when (event) {
            is FeedEvent.OnTabClick -> open(event.tab.route)
            is FeedEvent.OnFeedTabClick -> state.update { it.copy(tab = event.tab) }
            is FeedEvent.OnLikeClick -> toggle("r${event.story}", likes = true)
            is FeedEvent.OnSaveStoryClick -> toggle("r${event.story}", likes = false)
            is FeedEvent.OnStoryActionClick -> {
                val story = FeedState.STORIES[event.story]
                when {
                    state.value.storyLocked(story) -> askForPlan()
                    story.action == "lab" -> open(Screen.ProfileScreen(segment = "lab"))
                    story.action == "globe" -> open(Screen.GlobeScreen())
                    story.action?.startsWith("city:") == true -> open(Screen.GlobeScreen(city = story.action.removePrefix("city:")))
                }
            }
            is FeedEvent.OnCourseClick -> state.update { it.copy(course = event.course) }
            is FeedEvent.OnCourseCtaClick -> {
                val course = FeedState.COURSES[event.course]
                if (state.value.courseLocked(course)) {
                    state.update { it.copy(course = null) }
                    askForPlan()
                } else {
                    toggle("c${event.course}", likes = false)
                }
            }
            FeedEvent.OnCourseDismiss -> state.update { it.copy(course = null) }
            is FeedEvent.OnMembershipSheet -> state.update { it.copy(membershipSheet = event.sheet) }
            is FeedEvent.OnStartPlan -> profileRepository.startPlan(event.plan, event.yearly)
            FeedEvent.OnSwitchToNext -> profileRepository.switchToNext()
            FeedEvent.OnCancelPlan -> profileRepository.cancelPlan()
        }
    }

    /** Elite content asks for a plan: the paywall with none yet, the Elite offer on Next. */
    private fun askForPlan() = state.update {
        it.copy(membershipSheet = if (it.membership.plan == null) MembershipSheet.PAYWALL else MembershipSheet.ELITE)
    }

    private fun toggle(key: String, likes: Boolean) = profileRepository.update {
        if (likes) it.copy(likes = if (key in it.likes) it.likes - key else it.likes + key)
        else it.copy(saved = if (key in it.saved) it.saved - key else it.saved + key)
    }

    private fun open(route: Screen) = viewModelScope.launch { navigator.openTab(route) }
}
