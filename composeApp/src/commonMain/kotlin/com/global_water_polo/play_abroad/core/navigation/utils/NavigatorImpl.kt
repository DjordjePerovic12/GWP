package com.global_water_polo.play_abroad.core.navigation.utils

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import com.global_water_polo.play_abroad.core.navigation.Screen

/**
  Implementation of the Navigator interface that handles navigation using Kotlin Channels.

  This class manages navigation events through a Channel, which provides a safe way to
  handle navigation in a coroutine-based environment. Provided as a Koin single in AbstractionModule.
 **/

class NavigatorImpl : Navigator {
    private val _navigationChannel = Channel<NavType>()
    override val navigationFlow = _navigationChannel.receiveAsFlow()

    override suspend fun navigateTo(
        destination: Screen,
        navOptions: NavOptionsBuilder.() -> Unit
    ) {
        _navigationChannel.send(
            NavType.NavigateToRoute(
                destination = destination,
                navOptions = navOptions
            )
        )
    }

    override suspend fun navigateUp() {
        _navigationChannel.send(NavType.NavigateUp)
    }
}
