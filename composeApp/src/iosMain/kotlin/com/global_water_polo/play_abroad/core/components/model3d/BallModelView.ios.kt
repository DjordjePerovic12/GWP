package com.global_water_polo.play_abroad.core.components.model3d

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.readValue
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.writeToURL
import platform.SceneKit.SCNAntialiasingMode
import platform.SceneKit.SCNCamera
import platform.SceneKit.SCNLight
import platform.SceneKit.SCNLightTypeAmbient
import platform.SceneKit.SCNLightTypeDirectional
import platform.SceneKit.SCNLightingModelPhysicallyBased
import platform.SceneKit.SCNMaterial
import platform.SceneKit.SCNNode
import platform.SceneKit.SCNScene
import platform.SceneKit.SCNVector3Make
import platform.SceneKit.SCNView
import platform.SceneKit.lookAt
import platform.UIKit.UIColor
import com.global_water_polo.play_abroad.resources.Res

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun BallModelView(frame: () -> BallFrame, modifier: Modifier) {
    val currentFrame by rememberUpdatedState(frame)
    val ball = remember { BallScene() }
    LaunchedEffect(ball) {
        runCatching { Res.readBytes("files/gwp_ball.usdz") }.getOrNull()?.let(ball::load)
    }
    LaunchedEffect(ball) {
        while (true) {
            withFrameNanos { }
            ball.apply(currentFrame())
        }
    }
    DisposableEffect(ball) { onDispose { ball.release() } }
    // Over the Compose content (it is transparent), so the space and texts drawn underneath show through.
    UIKitView(
        factory = { ball.view },
        modifier = modifier,
        properties = UIKitInteropProperties(interactionMode = null, placedAsOverlay = true)
    )
}

/**
 * SceneKit on a transparent SCNView: the USDZ ball under a warm key light from the upper left, a cool rim
 * from behind on the right and a soft ambient fill, matching the Android lighting.
 */
@OptIn(ExperimentalForeignApi::class)
private class BallScene {
    val view = SCNView(frame = CGRectZero.readValue(), options = null)
    private val scene = SCNScene()
    private val ballNode = SCNNode()
    private val cameraNode = SCNNode()

    init {
        view.scene = scene
        view.backgroundColor = UIColor.clearColor
        view.opaque = false
        view.userInteractionEnabled = false
        view.antialiasingMode = SCNAntialiasingMode.SCNAntialiasingModeMultisampling4X
        view.rendersContinuously = true
        view.preferredFramesPerSecond = 60

        cameraNode.camera = SCNCamera().apply {
            fieldOfView = BALL_FOV_DEGREES.toDouble()
            zNear = 0.05
            zFar = 200.0
        }
        scene.rootNode.addChildNode(cameraNode)
        view.pointOfView = cameraNode
        scene.rootNode.addChildNode(ballNode)

        scene.rootNode.addChildNode(light(SCNLightTypeDirectional, 1100.0, UIColor(red = 1.0, green = 0.96, blue = 0.9, alpha = 1.0)).apply {
            position = SCNVector3Make(-3f, 3f, 4f)
            lookAt(SCNVector3Make(0f, 0f, 0f))
        })
        scene.rootNode.addChildNode(light(SCNLightTypeDirectional, 450.0, UIColor(red = 0.62, green = 0.75, blue = 1.0, alpha = 1.0)).apply {
            position = SCNVector3Make(4f, -0.5f, -4f)
            lookAt(SCNVector3Make(0f, 0f, 0f))
        })
        scene.rootNode.addChildNode(light(SCNLightTypeAmbient, 260.0, UIColor.whiteColor))
        scene.lightingEnvironment.contents = UIColor.grayColor
        scene.lightingEnvironment.intensity = 1.0
    }

    private fun light(type: String?, intensity: Double, color: UIColor) = SCNNode().apply {
        light = SCNLight().apply {
            this.type = type ?: SCNLightTypeDirectional!!
            this.intensity = intensity
            this.color = color
        }
    }

    fun load(usdz: ByteArray) {
        if (ballNode.childNodes.isNotEmpty()) return
        // SceneKit reads USDZ from a file URL.
        val url = NSURL.fileURLWithPath(NSTemporaryDirectory() + "gwp_ball.usdz")
        val data = usdz.usePinned { NSData.create(bytes = it.addressOf(0), length = usdz.size.toULong()) }
        if (!data.writeToURL(url, atomically = true)) return
        val loaded = SCNScene.sceneWithURL(url, options = null, error = null) ?: return
        loaded.rootNode.childNodes.toList().forEach { child ->
            child as SCNNode
            child.removeFromParentNode()
            ballNode.addChildNode(child)
        }
        usePhysicallyBased(ballNode)
    }

    private fun usePhysicallyBased(node: SCNNode) {
        node.geometry?.materials?.forEach { (it as SCNMaterial).lightingModelName = SCNLightingModelPhysicallyBased }
        node.childNodes.forEach { usePhysicallyBased(it as SCNNode) }
    }

    fun apply(f: BallFrame) {
        view.hidden = !f.visible
        val distance = ballCameraDistance(f.radius)
        cameraNode.position = SCNVector3Make(0f, 0f, distance)
        cameraNode.camera?.zNear = maxOf(0.05, distance - 1.5.toFloat().toDouble())
        cameraNode.camera?.zFar = distance + 2.0
        ballNode.position = SCNVector3Make(0f, ballCenterHeight(f.centerY, distance), 0f)
        // SceneKit applies yaw (the spin) before pitch (the lean), like the Android transform.
        ballNode.eulerAngles = SCNVector3Make(f.tilt, f.spin, 0f)
    }

    fun release() {
        view.rendersContinuously = false
        view.scene = null
    }
}
