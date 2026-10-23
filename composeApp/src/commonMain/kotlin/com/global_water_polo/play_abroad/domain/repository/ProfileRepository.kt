package com.global_water_polo.play_abroad.domain.repository

import kotlinx.coroutines.flow.StateFlow
import com.global_water_polo.play_abroad.domain.model.Profile

interface ProfileRepository {
    val profile: StateFlow<Profile>
    fun update(transform: (Profile) -> Profile)
    fun reset()
}
