package com.global_water_polo.play_abroad.di

import org.koin.core.module.dsl.viewModelOf
import com.global_water_polo.play_abroad.presentation.app.profile.ProfileViewModel
import com.global_water_polo.play_abroad.presentation.app.feed.FeedViewModel
import com.global_water_polo.play_abroad.presentation.app.globe.GlobeViewModel
import com.global_water_polo.play_abroad.presentation.app.travel.FlightRadarViewModel
import com.global_water_polo.play_abroad.presentation.app.settings.SettingsViewModel
import org.koin.dsl.module
import com.global_water_polo.play_abroad.MainViewModel
import com.global_water_polo.play_abroad.presentation.home.parent.ParentHomeViewModel
import com.global_water_polo.play_abroad.presentation.home.coach.CoachHomeViewModel
import com.global_water_polo.play_abroad.presentation.intro.IntroViewModel
import com.global_water_polo.play_abroad.presentation.opener.OpenerViewModel
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel

/**
 * Every ViewModel is registered here (the Koin equivalent of @HiltViewModel).
 * Add one viewModelOf(::<Feature>ViewModel) line per new feature.
 */

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::IntroViewModel)
    viewModelOf(::OpenerViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::ParentHomeViewModel)
    viewModelOf(::CoachHomeViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::FeedViewModel)
    viewModelOf(::GlobeViewModel)
    viewModelOf(::FlightRadarViewModel)
    viewModelOf(::SettingsViewModel)
}
