package com.global_water_polo.play_abroad.domain.model

import kotlinx.serialization.Serializable

/** Who is onboarding. Coach and club share the account and travel steps; fans go straight to the feed. */
@Serializable
enum class Lane {
    ATHLETE,
    PARENT,
    COACH,
    CLUB,
    FAN
}
