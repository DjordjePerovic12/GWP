package com.global_water_polo.play_abroad.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.global_water_polo.play_abroad.MainViewModel
import com.global_water_polo.play_abroad.presentation.auth.login.LoginViewModel
import com.global_water_polo.play_abroad.presentation.example.ExampleViewModel

/**
 * Every ViewModel is registered here (the Koin equivalent of @HiltViewModel).
 * Add one viewModelOf(::<Feature>ViewModel) line per new feature.
 */

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::ExampleViewModel)
    viewModelOf(::LoginViewModel)
}
