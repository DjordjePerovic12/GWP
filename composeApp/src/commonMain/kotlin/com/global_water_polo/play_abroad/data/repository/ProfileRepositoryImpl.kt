package com.global_water_polo.play_abroad.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.global_water_polo.play_abroad.domain.model.Profile
import com.global_water_polo.play_abroad.domain.repository.ProfileRepository

//TODO: In memory only, like the prototype ("nothing is saved or sent"). Back it with the API once it exists.

class ProfileRepositoryImpl : ProfileRepository {
    private val state = MutableStateFlow(Profile())
    override val profile: StateFlow<Profile> = state.asStateFlow()

    override fun update(transform: (Profile) -> Profile) {
        state.update(transform)
    }

    override fun reset() {
        state.value = Profile()
    }
}
