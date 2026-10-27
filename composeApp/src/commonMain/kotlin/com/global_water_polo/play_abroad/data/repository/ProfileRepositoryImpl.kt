package com.global_water_polo.play_abroad.data.repository

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import com.global_water_polo.play_abroad.data.local.MediaFiles
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.repository.DataStoreRepository
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository

//TODO: Kept on the device only, like the prototype ("nothing is sent"). Back it with the API once it exists.

/**
 * The profile, saved on the device as JSON (DataStore) so it survives closing the app. It is read once at
 * launch; every change after that is written a moment later. A profile saved by an older build that no longer
 * reads is dropped rather than crashing.
 */
@OptIn(FlowPreview::class)
class ProfileRepositoryImpl(private val dataStore: DataStoreRepository) : ProfileRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }
    private val state = MutableStateFlow(Profile())
    private val loadedState = MutableStateFlow(false)
    private var changedBeforeLoad = false

    override val profile: StateFlow<Profile> = state.asStateFlow()
    override val loaded: StateFlow<Boolean> = loadedState.asStateFlow()

    init {
        scope.launch {
            val saved = dataStore.getProfile().first()
            val decoded = saved.takeIf { it.isNotBlank() }?.let {
                runCatching { json.decodeFromString(Profile.serializer(), it) }
                    .onFailure { e -> Logger.w(e) { "Saved profile could not be read; starting fresh" } }
                    .getOrNull()
            }
            if (decoded != null && !changedBeforeLoad) state.value = decoded
            loadedState.value = true
            state.drop(1).debounce(300).collect { p ->
                runCatching { dataStore.saveProfile(json.encodeToString(Profile.serializer(), p)) }
                    .onFailure { e -> Logger.w(e) { "Profile not saved" } }
            }
        }
    }

    override fun update(transform: (Profile) -> Profile) {
        if (!loadedState.value) changedBeforeLoad = true
        state.update(transform)
    }

    override fun reset() {
        state.value = Profile()
        scope.launch { MediaFiles.clear() }
    }
}
