package com.global_water_polo.play_abroad.core.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.global_water_polo.play_abroad.ui.theme.GWPPATheme

object CustomModifiers {
    val snackBarHost: @Composable SnackbarHostState.() -> Unit = {
        SnackbarHost(hostState = this) { snackBarData ->
            Snackbar(
                snackbarData = snackBarData,
                containerColor = GWPPATheme.colors.colorWhite,
                contentColor = GWPPATheme.colors.colorBlack,
                actionColor = GWPPATheme.colors.colorBlack,
                shape = RoundedCornerShape(4.dp)
            )
        }
    }
}
