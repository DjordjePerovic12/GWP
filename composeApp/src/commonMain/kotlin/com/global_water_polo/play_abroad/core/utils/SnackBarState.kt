package com.global_water_polo.play_abroad.core.utils

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
  Manages Snackbar display state and lifecycle in a Compose application.
  Provides controlled way to show snackbars and handle their dismissal.

  @property scaffoldState The SnackbarHostState to manage snackbar display
  @property scope CoroutineScope for launching snackbar operations
  @property navController NavHostController for navigation-aware snackbar handling
 **/
class SnackBarState(
    val scaffoldState: SnackbarHostState,
    val scope: CoroutineScope,
    val navController: NavHostController
) {

    private var snackBarJob: Job? = null

    fun showSnackBar(
        message: String
    ) {
        snackBarJob?.cancel()
        snackBarJob = scope.launch {
            scaffoldState.currentSnackbarData?.dismiss()
            scaffoldState.showSnackbar(
                message = message, duration = SnackbarDuration.Long, actionLabel = "OK"
            )
        }
    }
}

@Composable
fun rememberAppState(
    scaffoldState: SnackbarHostState = remember {
        SnackbarHostState()
    },
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope()
) = remember(scaffoldState, navController, scope) {
    SnackBarState(
        scaffoldState = scaffoldState, scope = scope, navController = navController
    )
}
