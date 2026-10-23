package com.global_water_polo.play_abroad.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.global_water_polo.play_abroad.MainViewModel
import com.global_water_polo.play_abroad.presentation.home.athlete.AthleteHomeViewModel
import com.global_water_polo.play_abroad.presentation.home.parent.ParentHomeViewModel
import com.global_water_polo.play_abroad.presentation.intro.IntroViewModel
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel
import com.global_water_polo.play_abroad.presentation.review.apply.ApplyReviewViewModel
import com.global_water_polo.play_abroad.presentation.review.status.ReviewStatusViewModel

/**
 * Every ViewModel is registered here (the Koin equivalent of @HiltViewModel).
 * Add one viewModelOf(::<Feature>ViewModel) line per new feature.
 */

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::IntroViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::AthleteHomeViewModel)
    viewModelOf(::ParentHomeViewModel)
    viewModelOf(::ApplyReviewViewModel)
    viewModelOf(::ReviewStatusViewModel)
}
