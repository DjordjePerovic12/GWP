package com.global_water_polo.play_abroad.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

/** Needs UIViewControllerBasedStatusBarAppearance = NO in Info.plist, so the app sets the style itself. */
@Suppress("DEPRECATION")
@Composable
actual fun StatusBarStyle(lightIcons: Boolean) {
    SideEffect {
        UIApplication.sharedApplication.setStatusBarStyle(
            if (lightIcons) UIStatusBarStyleLightContent else UIStatusBarStyleDarkContent,
            animated = true
        )
    }
}
