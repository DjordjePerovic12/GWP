package com.global_water_polo.play_abroad.presentation.onboarding.who

import com.global_water_polo.play_abroad.core.components.LocalSound
import com.global_water_polo.play_abroad.core.components.Sfx
import com.global_water_polo.play_abroad.core.components.globe.LocalGlobeController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.presentation.base.BaseContentView
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingEvent
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel

@Composable
fun WhoScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.onboardingState.collectAsStateWithLifecycle()

    val globe = LocalGlobeController.current
    val sound = LocalSound.current

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        WhoScreenContent(
            state = state,
            onRoleClick = {
                // The ball drops into the water as the onboarding starts (the prototype's startRole splash).
                if (globe.hapticsOn) sound.play(Sfx.SPLASH)
                viewModel.onEvent(OnboardingEvent.OnRoleClick(it))
            },
            onBackToGlobeClick = { viewModel.onEvent(OnboardingEvent.OnBackToGlobeClick) }
        )
    }
}
