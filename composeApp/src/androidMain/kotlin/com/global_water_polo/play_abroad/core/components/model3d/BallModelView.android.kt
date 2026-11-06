package com.global_water_polo.play_abroad.core.components.model3d

import android.opengl.Matrix
import android.view.Choreographer
import android.view.Surface
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.filament.Camera
import com.google.android.filament.Engine
import com.google.android.filament.EntityManager
import com.google.android.filament.Filament
import com.google.android.filament.IndirectLight
import com.google.android.filament.LightManager
import com.google.android.filament.SwapChain
import com.google.android.filament.View
import com.google.android.filament.Viewport
import com.google.android.filament.android.UiHelper
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.FilamentAsset
import com.google.android.filament.gltfio.Gltfio
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.gltfio.UbershaderProvider
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.max
import com.global_water_polo.play_abroad.resources.Res

@Composable
actual fun BallModelView(frame: () -> BallFrame, modifier: Modifier) {
    val glb by produceState<ByteArray?>(null) { value = runCatching { Res.readBytes("files/gwp_ball.glb") }.getOrNull() }
    val currentFrame by rememberUpdatedState(frame)
    AndroidView(
        factory = { context -> TextureView(context).also { it.tag = BallRenderer(it) } },
        update = { view ->
            val renderer = view.tag as BallRenderer
            renderer.frame = { currentFrame() }
            glb?.let(renderer::load)
        },
        onRelease = { (it.tag as BallRenderer).destroy() },
        modifier = modifier
    )
}

/**
 * Filament on a transparent TextureView: the glTF ball, a warm key light from the upper left, a cool rim
 * from behind on the right and a soft even fill. The camera and the ball's turn follow [frame] every
 * display frame; textures decode in the background over the first frames.
 */
private class BallRenderer(private val textureView: TextureView) : Choreographer.FrameCallback {
    var frame: () -> BallFrame = { BallFrame(radius = 0.3f) }

    private val engine = Engine.create()
    private val renderer = engine.createRenderer()
    private val scene = engine.createScene()
    private val view = engine.createView()
    private val cameraEntity = EntityManager.get().create()
    private val camera = engine.createCamera(cameraEntity)
    private val uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
    private val materials = UbershaderProvider(engine)
    private val assetLoader = AssetLoader(engine, materials, EntityManager.get())
    private val resourceLoader = ResourceLoader(engine)
    private val key = EntityManager.get().create()
    private val rim = EntityManager.get().create()
    private val indirect = IndirectLight.Builder().irradiance(1, floatArrayOf(1f, 1f, 1f)).intensity(22_000f).build(engine)
    private val choreographer = Choreographer.getInstance()
    private val transform = FloatArray(16)
    private var swapChain: SwapChain? = null
    private var asset: FilamentAsset? = null
    private var loading = false
    private var width = 1
    private var height = 1
    private var running = true

    init {
        view.scene = scene
        view.camera = camera
        view.blendMode = View.BlendMode.TRANSLUCENT
        view.antiAliasing = View.AntiAliasing.FXAA
        view.multiSampleAntiAliasingOptions = view.multiSampleAntiAliasingOptions.apply {
            enabled = true
            sampleCount = 4
        }
        renderer.clearOptions = renderer.clearOptions.apply {
            clear = true
            clearColor = doubleArrayOf(0.0, 0.0, 0.0, 0.0)
        }
        camera.setExposure(16f, 1f / 125f, 100f)
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1f, 0.96f, 0.9f)
            .intensity(110_000f)
            .direction(0.55f, -0.45f, -0.7f)
            .build(engine, key)
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(0.62f, 0.75f, 1f)
            .intensity(40_000f)
            .direction(-0.7f, 0.1f, 0.7f)
            .build(engine, rim)
        scene.addEntity(key)
        scene.addEntity(rim)
        scene.indirectLight = indirect

        uiHelper.isOpaque = false
        uiHelper.renderCallback = object : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: Surface) {
                swapChain?.let(engine::destroySwapChain)
                swapChain = engine.createSwapChain(surface, uiHelper.swapChainFlags)
            }

            override fun onDetachedFromSurface() {
                swapChain?.let {
                    engine.destroySwapChain(it)
                    engine.flushAndWait()
                }
                swapChain = null
            }

            override fun onResized(width: Int, height: Int) {
                this@BallRenderer.width = width
                this@BallRenderer.height = height
                view.viewport = Viewport(0, 0, width, height)
            }
        }
        uiHelper.attachTo(textureView)
        choreographer.postFrameCallback(this)
    }

    fun load(glb: ByteArray) {
        if (asset != null) return
        val buffer = ByteBuffer.allocateDirect(glb.size).order(ByteOrder.nativeOrder()).put(glb).flip()
        val loaded = assetLoader.createAsset(buffer) ?: return
        resourceLoader.asyncBeginLoad(loaded)
        loading = true
        scene.addEntities(loaded.entities)
        asset = loaded
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!running) return
        choreographer.postFrameCallback(this)
        val ball = asset
        if (ball != null) {
            if (loading) {
                resourceLoader.asyncUpdateLoad()
                if (resourceLoader.asyncGetLoadProgress() >= 1f) {
                    ball.releaseSourceData()
                    loading = false
                }
            }
            val f = frame()
            textureView.alpha = if (f.visible) 1f else 0f
            val distance = ballCameraDistance(f.radius)
            camera.setProjection(
                BALL_FOV_DEGREES.toDouble(), width.toDouble() / max(1, height),
                max(0.05, distance - 1.5).toDouble(), (distance + 2.0), Camera.Fov.VERTICAL
            )
            camera.lookAt(0.0, 0.0, distance.toDouble(), 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
            Matrix.setIdentityM(transform, 0)
            Matrix.translateM(transform, 0, 0f, ballCenterHeight(f.centerY, distance), 0f)
            Matrix.rotateM(transform, 0, degrees(f.tilt), 1f, 0f, 0f)
            Matrix.rotateM(transform, 0, degrees(f.spin), 0f, 1f, 0f)
            val tm = engine.transformManager
            tm.setTransform(tm.getInstance(ball.root), transform)
        }
        val chain = swapChain ?: return
        if (uiHelper.isReadyToRender && renderer.beginFrame(chain, frameTimeNanos)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }

    fun destroy() {
        running = false
        choreographer.removeFrameCallback(this)
        uiHelper.detach()
        asset?.let {
            scene.removeEntities(it.entities)
            assetLoader.destroyAsset(it)
        }
        materials.destroyMaterials()
        materials.destroy()
        assetLoader.destroy()
        resourceLoader.destroy()
        engine.destroyIndirectLight(indirect)
        engine.destroyEntity(key)
        engine.destroyEntity(rim)
        swapChain?.let(engine::destroySwapChain)
        engine.destroyView(view)
        engine.destroyScene(scene)
        engine.destroyCameraComponent(cameraEntity)
        EntityManager.get().destroy(cameraEntity)
        engine.destroyRenderer(renderer)
        engine.destroy()
    }

    private fun degrees(radians: Float) = (radians * 180f / PI.toFloat())

    companion object {
        init {
            Gltfio.init()
            Filament.init()
        }
    }
}
