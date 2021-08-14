package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Skybox
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.Math
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private val skyboxShader: ShaderProgram

    private var meshes = arrayListOf<Mesh>()
    private val tronCamera = TronCamera(90f, 16f / 9f, 0.1f, 1000f)
    private var activeCamera = 0
    private var staticCamera = tronCamera
    private var staticCamera1 = tronCamera

    private val raumschiff = ModelLoader.loadModel(
        "assets/models/spaceship/Intergalactic_Spaceship-(Wavefront).obj",
        Math.toRadians(180f),
        Math.toRadians(0f),
        Math.toRadians(0f)
    )

    private val planet0 = ModelLoader.loadModel(
            "assets/models/planet0/Jupiter.obj",
            0f,
            0f,
            0f
    )

    private val rings = ArrayList<Renderable?>()
    private var ringCounter = -500f
    private var points = 0;


    private val pointLight: PointLight
    private val pointLight2: PointLight
    private val pointLight3: PointLight
    private val pointLight4: PointLight
    private val pointLight5: PointLight
    private val spotLight: SpotLight
    private val spotLight2: SpotLight
    private val mouseXPos = window.mousePos.xpos

    private var skybox = Skybox()
    private var skyBoxTextures = ArrayList<String>()


    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
        skyboxShader = ShaderProgram("assets/shaders/skybox_vert.glsl", "assets/shaders/skybox_frag.glsl")

        skyBoxTextures.add("assets/textures/skybox/right.png")
        skyBoxTextures.add("assets/textures/skybox/left.png")
        skyBoxTextures.add("assets/textures/skybox/bottom.png")
        skyBoxTextures.add("assets/textures/skybox/top.png")
        skyBoxTextures.add("assets/textures/skybox/front.png")
        skyBoxTextures.add("assets/textures/skybox/back.png")

        skybox.loadCubemap(skyBoxTextures)

        //initial opengl state
        glEnable(GL_CULL_FACE); GLError.checkThrow() //Cull-Facing wurde aktiviert
        glFrontFace(GL_CCW); GLError.checkThrow() // Alle Dreiecke, die zur Kamera gerichtet sind, sind entgegen des Uhrzeigersinns definiert.
        glCullFace(GL_BACK); GLError.checkThrow() // Es werden alle Dreiecke verworfen, die nach hinten zeigen
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LEQUAL); GLError.checkThrow()


        raumschiff?.scaleLocal(Vector3f(0.08f))


        planet0?.scaleLocal(Vector3f(1f))
        planet0?.translateLocal(Vector3f(0f, -40f ,-1000f))


        pointLight = PointLight(Vector3f(0.0f, 0.5f, 0.0f), Vector3f(2.0f, 0.0f, 1.0f), Vector3f(1.0f, 0.5f, 0.1f))
        pointLight2 = PointLight(Vector3f(20.0f, 5f, 20.0f), Vector3f(2.0f, 0.0f, 0.0f), Vector3f(1.0f, 0.5f, 0.1f))
        pointLight3 = PointLight(Vector3f(-20.0f, 5f, 20.0f), Vector3f(0.0f, 2.0f, 0.0f), Vector3f(1.0f, 0.5f, 0.1f))
        pointLight4 = PointLight(Vector3f(20.0f, 5f, -20.0f), Vector3f(0.0f, 0.0f, 2.0f), Vector3f(1.0f, 0.5f, 0.1f))
        pointLight5 = PointLight(Vector3f(-20.0f, 5f, -20.0f), Vector3f(2.0f, 0.0f, 2.0f), Vector3f(1.0f, 0.5f, 0.1f))
        pointLight.parent = raumschiff


        spotLight = SpotLight(Vector3f(0f, 1f, 0f), Vector3f(1.0f, 1.0f, 1.0f), Vector3f(0.5f, 0.05f, 0.01f), 12.5f, 17.5f)
        spotLight.rotateLocal(Math.toRadians(-5.0f), 0f, 0f)
        spotLight.parent = raumschiff

        spotLight2 =
            SpotLight(Vector3f(0f, 5f, 0f), Vector3f(1.0f, 0.0f, 1.0f), Vector3f(0.5f, 0.05f, 0.01f), 10.5f, 20.5f)
        spotLight2.rotateLocal(Math.toRadians(-90.0f), 0f, 0f)

        staticCamera.rotateLocal(Math.toRadians(-35.0f), 0f, 0f)
        staticCamera.translateLocal(Vector3f(0f, 0.0f, 20f))
        staticCamera.parent = raumschiff




        /*
        staticCamera1.rotateLocal(Math.toRadians(0f),0f,0f)
        staticCamera1.translateLocal(Vector3f(0f,0f,-12.5f))
        staticCamera1.parent = raumschiff
         */


        for (i in 0 until 50) {
            spawnRings()
        }
    }


    fun render(dt: Float, t: Float) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        staticCamera.bind(staticShader)
        skybox.render(
            skyboxShader,
            staticCamera.getCalculateViewMatrix(),
            staticCamera.getCalculateProjectionMatrix()
        )
        staticShader.use()
        pointLight.bind(staticShader, "PointLight")
        pointLight2.bind(staticShader, "PointLight2")
        pointLight3.bind(staticShader, "PointLight3")
        pointLight4.bind(staticShader, "PointLight4")
        pointLight5.bind(staticShader, "PointLight5")
        spotLight.bind(staticShader, "SpotLight", tronCamera.getCalculateViewMatrix())
        spotLight2.bind(staticShader, "SpotLight2", tronCamera.getCalculateViewMatrix())
        planet0?.render(staticShader)
        raumschiff?.render(staticShader)

        rings.forEach {
            it?.render(staticShader)
            it?.gotHit(raumschiff)
            if (it?.hit == 1) {
                points++
                println("You scored a point! Current Points : $points")
            }
        }

    }

    fun update(dt: Float, t: Float) {

        raumschiff?.translateLocal(Vector3f(0f, 0f, -20f))
        if (window.getKeyState(GLFW.GLFW_KEY_A)) {
            raumschiff?.rotateLocal(0f, Math.toRadians(dt * 100f), Math.toRadians(dt * 10f))
        }
        if (window.getKeyState(GLFW.GLFW_KEY_D)) {
            raumschiff?.rotateLocal(0f, Math.toRadians(dt * -100f), Math.toRadians(dt * -10f))
        }
        if (window.getKeyState(GLFW.GLFW_KEY_W)) {
            raumschiff?.rotateLocal(Math.toRadians(dt * 30f), 0f, 0f)
        }
        if (window.getKeyState(GLFW.GLFW_KEY_S)) {
            raumschiff?.rotateLocal(Math.toRadians(dt * -30f), 0f, 0f)
        }
        if(window.getKeyState(GLFW.GLFW_KEY_C)){

            print("Planet 0:"+planet0?.getWorldPosition())
        }
        if(window.getKeyState(GLFW.GLFW_KEY_X)){
            print("Camera 1")
        }
    }


    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {

    }

    fun onMouseMove(xpos: Double, ypos: Double){
    }

    fun cleanup() {}

    fun spawnRings() {
        var newRing = ModelLoader.loadModel("assets/models/ring/ring2.obj", 0f, 0f, 0f)
        rings.add(newRing)
        rings[rings.size - 1]?.translateLocal(Vector3f((Math.random() * 200f + 1f).toFloat(), (Math.random() * 200f + 1f).toFloat(), -ringCounter))
        rings[rings.size - 1]?.scaleLocal(Vector3f(0.5f))
        ringCounter += 400f
    }
}

