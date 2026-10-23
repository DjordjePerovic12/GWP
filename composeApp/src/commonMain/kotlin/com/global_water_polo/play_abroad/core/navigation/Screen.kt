package com.global_water_polo.play_abroad.core.navigation

import kotlinx.serialization.Serializable

/**
This interface represents all navigation destinations in the app.
The sealed interface ensures type-safe navigation.
 **/

/**
Type safe navigation in Compose Multiplatform enables us to define destinations
without the need to have routes like earlier
 **/

sealed interface Screen {

    //Example
    //Screens with no needed arguments sent through navigation are defined as data object like this
    @Serializable
    data object ExampleScreen : Screen

    //Example
    //Screen where arguments are sent through navigation are defined as data classes like this
    //It can have any amount of properties as any other data class
    @Serializable
    data class ExampleScreenWithArguments(val name: String, val email: String) : Screen

    @Serializable
    data object LoginScreen : Screen
}
