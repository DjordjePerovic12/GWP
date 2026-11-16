package com.global_water_polo.play_abroad.core.components.model3d

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sin
import kotlin.math.tan

/**
 * Where the 3D ball sits in its view for one frame. The ball is a unit sphere; the platform view moves the
 * camera so it looks [radius] (a fraction of the view's height) big, centred at [centerY] (0 = top, 1 =
 * bottom), turned [spin] radians about its poles and leaned [tilt] radians towards the viewer.
 */
data class BallFrame(
    val radius: Float,
    val centerY: Float = 0.5f,
    val spin: Float = 0f,
    val tilt: Float = 0f,
    /** False hides the view (a full-screen loader is over it). */
    val visible: Boolean = true
)

/** Vertical field of view the platform views use, in degrees (the reference opener's VFOV). */
const val BALL_FOV_DEGREES = 26f

/**
 * Spin that turns the GWP logo panel to face the viewer. The 16 Nov ball export (gwppa-blender INTRO_KIT.md)
 * puts the logo at 18 N 32 W in the intro's axes: +32 degrees brings 32 W to a camera on +Z.
 */
const val BALL_FRONT_SPIN = 0.56f

/** Lean (top toward the viewer) that brings the logo's 18 N to the centre, as the web intro's hero pose does. */
const val BALL_FRONT_TILT = 0.31f

/** Camera distance (in ball radii) that makes the ball look [radius] of the view's height. */
fun ballCameraDistance(radius: Float): Float {
    val k = (radius * 2f * tan(BALL_FOV_DEGREES / 2f * PI.toFloat() / 180f)).coerceAtLeast(1e-4f)
    return 1f / sin(atan(k))
}

/** World-space height of the ball's centre that puts it at [BallFrame.centerY] for a camera [distance] away. */
fun ballCenterHeight(centerY: Float, distance: Float): Float =
    (0.5f - centerY) * 2f * distance * tan(BALL_FOV_DEGREES / 2f * PI.toFloat() / 180f)

/**
 * The GWP ball model from Blender (composeResources/files/gwp_ball.glb on Android, gwp_ball.usdz on iOS),
 * rendered with real lighting on a transparent background: Filament on Android, SceneKit on iOS. [frame] is
 * read once per display frame, so animations drive it without recomposing. Not touchable: taps go to what
 * lies under it.
 */
@Composable
expect fun BallModelView(frame: () -> BallFrame, modifier: Modifier = Modifier)
