package com.global_water_polo.play_abroad.presentation.app.feed

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.global_water_polo.play_abroad.core.components.membership.MembershipSheet
import com.global_water_polo.play_abroad.core.components.membership.membershipOf
import com.global_water_polo.play_abroad.core.utils.AccountGate
import com.global_water_polo.play_abroad.core.utils.LockAction
import com.global_water_polo.play_abroad.core.utils.StringProvider
import com.global_water_polo.play_abroad.domain.repository.lockStep
import com.global_water_polo.play_abroad.resources.*
import org.jetbrains.compose.resources.StringResource
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
    private val navigator: Navigator,
    private val stringProvider: StringProvider
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
                    profileRepository.profile.collect { p ->
                        state.update {
                            // Elite just started: the library's chains fall away once.
                            val unlocked = !it.membership.elite && membershipOf(p).elite && it.profile.lane != null
                            it.copy(profile = p, freed = it.freed || unlocked)
                        }
                    }
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
            // v29's lock: liking and saving wait until the account is confirmed.
            is FeedEvent.OnLikeClick -> if (!lockedAccount()) toggle("r${event.story}", likes = true)
            is FeedEvent.OnSaveStoryClick -> if (!lockedAccount()) state.update { it.copy(saveSheet = event.story) }
            FeedEvent.OnSaveDismiss -> state.update { it.copy(saveSheet = null) }
            is FeedEvent.OnFolderPick -> if (!lockedAccount()) saveTo(event.story, event.folder)
            is FeedEvent.OnFolderCreate -> if (!lockedAccount()) {
                val name = event.name.trim().take(FeedState.FOLDER_NAME_MAX)
                if (name.isNotEmpty()) {
                    profileRepository.update { if (name in it.folders || name in FeedState.BASE_FOLDERS) it else it.copy(folders = it.folders + name) }
                    saveTo(event.story, name)
                }
            }
            is FeedEvent.OnSaveRemove -> {
                val key = "r${event.story}"
                profileRepository.update { it.copy(clipFolders = it.clipFolders - key, saved = it.saved - key) }
                state.update { it.copy(saveSheet = null) }
            }
            // Next members get the Elite offer; free users the paywall.
            FeedEvent.OnSeeEliteClick -> {
                state.update { it.copy(saveSheet = null) }
                askForPlan()
            }
            is FeedEvent.OnSavedClipClick -> state.update { it.copy(tab = FeedTab.FOR_YOU, reel = event.story) }
            FeedEvent.OnOpenAllWithEliteClick -> state.update { it.copy(course = null, membershipSheet = MembershipSheet.ELITE) }
            FeedEvent.OnFreedShown -> state.update { it.copy(freed = false) }
            is FeedEvent.OnLock -> {
                state.update { it.copy(lockView = profileRepository.lockStep(event.action, it.lockView)) }
                if (event.action == LockAction.DONE) open(Screen.HomeScreen)
            }
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
            is FeedEvent.OnCourseCtaClick -> if (!lockedAccount()) {
                val s = state.value
                val course = FeedState.COURSES[event.course]
                when {
                    // Next members buy single folders; GWP sets that up later (the sheet says so).
                    s.courseLocked(course) && s.membership.plan != null -> Unit
                    s.courseLocked(course) -> {
                        state.update { it.copy(course = null) }
                        askForPlan()
                    }
                    else -> {
                        val key = "c${event.course}"
                        val saving = key !in s.profile.saved
                        toggle(key, likes = false)
                        if (saving) say(Res.string.feed_saved_toast)
                    }
                }
            }
            FeedEvent.OnCourseDismiss -> state.update { it.copy(course = null) }
            is FeedEvent.OnMembershipSheet -> state.update { it.copy(membershipSheet = event.sheet) }
            is FeedEvent.OnStartPlan -> if (!lockedAccount()) profileRepository.startPlan(event.plan, event.yearly)
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

    /** Saves clip [story] into [folder] (moving it if it was elsewhere) and says so. */
    private fun saveTo(story: Int, folder: String) {
        val key = "r$story"
        profileRepository.update { it.copy(clipFolders = it.clipFolders + (key to folder), saved = it.saved - key) }
        state.update { it.copy(saveSheet = null) }
        viewModelScope.launch { _snackBarChannel.send(stringProvider.getStringWithArgs(Res.string.save_toast, folder)) }
    }

    private fun say(res: StringResource) = viewModelScope.launch { _snackBarChannel.send(stringProvider.getString(res)) }

    /** While the account is locked, an action opens the lock sheet instead (v29's click guard). */
    private fun lockedAccount(): Boolean {
        if (!AccountGate.locked(state.value.profile)) return false
        state.update { it.copy(saveSheet = null, course = null, membershipSheet = null, lockView = it.lockView.copy(sheet = true)) }
        return true
    }

    private fun open(route: Screen) = viewModelScope.launch { navigator.openTab(route) }
}
