package com.global_water_polo.play_abroad.presentation.onboarding.offers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.global_water_polo.play_abroad.core.utils.observeWithLifecycle
import com.global_water_polo.play_abroad.domain.model.Program
import com.global_water_polo.play_abroad.presentation.base.BaseContentView
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingEvent
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingState
import com.global_water_polo.play_abroad.presentation.onboarding.OnboardingViewModel

@Composable
fun OffersScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel,
    showSnackBar: (String) -> Unit
) {
    val state by viewModel.onboardingState.collectAsStateWithLifecycle()
    var eliteSheet by remember { mutableStateOf(false) }

    viewModel.snackBarChannel.observeWithLifecycle { message ->
        showSnackBar(message)
    }

    BaseContentView(
        modifier = modifier,
        viewState = state,
        applySystemBarsPadding = false
    ) {
        OffersScreenContent(
            state = state,
            showEliteSheet = eliteSheet,
            onProgramClick = { program ->
                // Locked programs open the Elite offer, like the prototype's rattling chains.
                if (program.needsElite && !state.isElite) eliteSheet = true
                else viewModel.onEvent(OnboardingEvent.OnOfferPick(program.key))
            },
            onPrimaryClick = {
                if (state.plan == OnboardingState.PLAN_NEXT && state.offerPick == null) eliteSheet = true
                else viewModel.onEvent(OnboardingEvent.OnOffersPrimaryClick)
            },
            onLaterClick = { viewModel.onEvent(OnboardingEvent.OnOffersLaterClick) },
            onEliteStartClick = {
                viewModel.onEvent(OnboardingEvent.OnEliteStartClick)
                eliteSheet = false
            },
            onEliteDismiss = { eliteSheet = false },
            onBackClick = { viewModel.onEvent(OnboardingEvent.NavigateBack) }
        )
    }
}

/** Programs in the order All offers lists them. */
val OFFER_PROGRAMS: List<Program> = Program.entries
