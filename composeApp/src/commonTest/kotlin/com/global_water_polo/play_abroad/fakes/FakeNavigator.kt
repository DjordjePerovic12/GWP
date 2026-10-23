package com.global_water_polo.play_abroad.fakes

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import com.global_water_polo.play_abroad.core.navigation.Screen
import com.global_water_polo.play_abroad.core.navigation.utils.NavType
import com.global_water_polo.play_abroad.core.navigation.utils.Navigator

class FakeNavigator : Navigator {
    val destinations = mutableListOf<Screen>()
    var navigateUpCount = 0

    override val navigationFlow: Flow<NavType> = emptyFlow()

    override suspend fun navigateTo(destination: Screen, navOptions: NavOptionsBuilder.() -> Unit) {
        destinations += destination
    }

    override suspend fun navigateUp() {
        navigateUpCount++
    }
}
