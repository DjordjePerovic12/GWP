package com.global_water_polo.play_abroad.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator
import com.global_water_polo.play_abroad.core.navigation.utils.NavigatorImpl

//Binds navigation
val abstractionModule = module {
    singleOf(::NavigatorImpl) bind Navigator::class
}
