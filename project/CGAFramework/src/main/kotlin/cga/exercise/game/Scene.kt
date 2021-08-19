package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Skybox
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import org.joml.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12
import kotlin.collections.ArrayList


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private var staticShader: ShaderProgram
    private val skyboxShader: ShaderProgram
    private val tronShader : ShaderProgram
    private val toonShader : ShaderProgram
    private val normalShader : ShaderProgram

    private var mode = 0
    private var speed = -20f


    private val thirdPersonCamera = TronCamera(90f, 16f / 9f, 0.1f, 1200f)
    private var firstPersonCamera = TronCamera(90f, 16f / 9f, 0.1f, 1200f)
    private var activeCamera = thirdPersonCamera

    private val raumschiff = ModelLoader.loadModel(
        "assets/models/spaceship/Intergalactic_Spaceship-(Wavefront).obj",
        Math.toRadians(180f),
        Math.toRadians(0f),
        Math.toRadians(0f)
    )

    private val planet0 = ModelLoader.loadModel(
        "assets/models/planet0/Jupiter_2.obj",
        0f,
        0f,
        0f
    )

    private val homescreen = ModelLoader.loadModel(
        "assets/models/screens/homescreenobj.obj",
        Math.toRadians(0f),
        Math.toRadians(270f),
        Math.toRadians(0f)
    )

    private val replaysceen = ModelLoader.loadModel(
        "assets/models/screens/replayscreenobj.obj",
        Math.toRadians(0f),
        Math.toRadians(270f),
        Math.toRadians(0f)
    )

    private val planet1 = ModelLoader.loadModel(
        "assets/models/planet1/FictionalPlanet1.obj",
        0f,
        0f,
        0f
    )

    private val rings = ArrayList<Renderable?>()
    private var ringZPos = 0f
    private var points = 0
    private var pointsResetable = 0
    private var cameraPerspective = 0

    private var lost = false

    private val pointLight: PointLight
    private val spotLightFront : SpotLight

    private val colors = ArrayList<Vector3f>()
    private var currentColor = 0

    private var xposNow = window.mousePos.xpos

    private val ringhittexture : Texture2D

    private var skybox = Skybox()
    private var skyBoxTextures = ArrayList<String>()


    //scene setup
    init {
        tronShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
        skyboxShader = ShaderProgram("assets/shaders/skybox_vert.glsl", "assets/shaders/skybox_frag.glsl")
        toonShader = ShaderProgram("assets/shaders/toon_vert.glsl", "assets/shaders/toon_frag.glsl")
        normalShader = ShaderProgram("assets/shaders/normal_vert.glsl", "assets/shaders/normal_frag.glsl")

        staticShader = tronShader

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


        raumschiff?.translateLocal(Vector3f(0f, 0f, 500f)) //für einen einfacheren Start
        raumschiff?.scaleLocal(Vector3f(0.08f))

        homescreen?.translateLocal(Vector3f(0f, 0f, -20f))
        replaysceen?.translateLocal(Vector3f(0f, 0f, -20f))
        homescreen?.scaleLocal(Vector3f(100f))
        replaysceen?.scaleLocal(Vector3f(100f))

        planet0?.parent = planet1

        planet1?.translateGlobal(Vector3f(1100f, 350f, -1200f))
        planet1?.scaleLocal(Vector3f(20f))

        planet0?.rotateLocal(0f, 0f, -0.2f) //Schiefer Planet
        planet0?.translateGlobal(Vector3f(10f, -5f, 0f))
        planet0?.scaleLocal(Vector3f(0.5f))





        pointLight = PointLight(Vector3f(15f, 5f, 30f), Vector3f(0.0f, 0.0f, 5.0f), Vector3f(1.0f, 0.5f, 0.1f))
        pointLight.parent = raumschiff

        thirdPersonCamera.translateLocal(Vector3f(0f, 0.0f, 10f))
        thirdPersonCamera.parent = raumschiff

        firstPersonCamera.rotateLocal(Math.toRadians(0f), 0f, 0f)
        firstPersonCamera.translateLocal(Vector3f(0f, 0.0f, -5f))
        firstPersonCamera.parent = raumschiff

        spotLightFront = SpotLight(Vector3f(0f, 0f, 490f), Vector3f(10.0f, 10.0f, 0.0f), Vector3f(0.5f, 0.05f, 0.01f), 40.5f, 45.5f)
        spotLightFront.parent = raumschiff

        colors.add(Vector3f(5f, 0f, 5f))
        colors.add(Vector3f(0f, 5f, 5f))
        colors.add(Vector3f(5f, 5f, 0f))
        colors.add(Vector3f(5f, 0f, 5f))
        colors.add(Vector3f(0f, 0f, 5f))
        colors.add(Vector3f(5f, 0f, 0f))
        colors.add(Vector3f(0f, 5f, 0f))

        ringhittexture = Texture2D.invoke("assets/models/ring/ring_hit_emit.png", true)
        ringhittexture.setTexParams(GL12.GL_CLAMP_TO_EDGE, GL12.GL_CLAMP_TO_EDGE, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR_MIPMAP_LINEAR)

    }


    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        activeCamera.bind(staticShader)
        staticShader.use()

        pointLight.bind(staticShader, "PointLight")
        spotLightFront.bind(staticShader, "SpotLightFront", activeCamera.getCalculateViewMatrix())

        if(rings.size < 10)
        {
            spawnRings()
        }
        if(pointsResetable == 10)
        {
            pointsResetable = 0
            rings.clear()
        }

        if (mode == 0) {
            homescreen?.render(staticShader)
        } else if (mode == 1) {
            skybox.render(
                skyboxShader,
                activeCamera.getCalculateViewMatrix(),
                activeCamera.getCalculateProjectionMatrix()
            )
            if(cameraPerspective == 0)
            {
                cameraPerspective = 1
                thirdPersonCamera.translateLocal(Vector3f(0f, 6f, 2f))
            }
            staticShader.setUniform("colorization", 1f, 1f, 1f)
            staticShader.use()
            planet0?.render(staticShader)
            planet1?.render(staticShader)
            raumschiff?.render(staticShader)

            rings.forEach {
                it?.render(staticShader)
                it?.gotHit(raumschiff)
                if (it?.hit == 1) {
                    points++
                    pointsResetable++
                    println("You scored a point! Current Points : $points")
                    it.meshes[0].changeTexture(1, ringhittexture)
                    if(points % 10 == 0)
                    {
                        currentColor++
                        if(currentColor == 7)
                            currentColor = 0
                        pointLight.col = colors[currentColor]
                    }
                }
                else if (CollisionDetection.randtreffer(it, raumschiff)){
                    mode = 2
                }
            }
        } else if (mode == 2) {
            if(cameraPerspective == 1)
            {
                cameraPerspective = 0
                thirdPersonCamera.translateLocal(Vector3f(0f, -6f, -2f))
            }
             //für einen einfacheren Start
            replaysceen?.render(staticShader)
            raumschiff?.modelMatrix = Matrix4f()
            raumschiff?.translateLocal(Vector3f(0f, 0f, 500f))
            raumschiff?.scaleLocal(Vector3f(0.08f))
            points = 0
            pointsResetable = 0
            ringZPos = 0f
            speed = -20f
            rings.clear()
        }


    }

    fun update(dt: Float, t: Float) {
        if (mode == 1) {

            planet1?.rotateLocal(0f, 0.002f, 0f) //Planet rotation
            planet0?.rotateAroundPoint(0f, 0.00002f, 0f, planet1?.getPosition()!!) //Planet rotation
            raumschiff?.translateLocal(Vector3f(0f, 0f, speed))
            if (window.getKeyState(GLFW.GLFW_KEY_A)) {
                raumschiff?.rotateLocal(0f, Math.toRadians(dt * 100f), 0f)
            }
            if (window.getKeyState(GLFW.GLFW_KEY_D)) {
                raumschiff?.rotateLocal(0f, Math.toRadians(dt * -100f), 0f)
            }
            if (window.getKeyState(GLFW.GLFW_KEY_W)) {
                raumschiff?.rotateLocal(Math.toRadians(dt * 50f), 0f, 0f)
            }
            if (window.getKeyState(GLFW.GLFW_KEY_S)) {
                raumschiff?.rotateLocal(Math.toRadians(dt * -50f), 0f, 0f)
            }
            if (window.getKeyState(GLFW.GLFW_KEY_1)) {
                activeCamera = thirdPersonCamera
            }
            if (window.getKeyState(GLFW.GLFW_KEY_2)) {
                activeCamera = firstPersonCamera
            }
            if(window.getKeyState(GLFW.GLFW_KEY_4))
            {
                staticShader = toonShader
            }
            if(window.getKeyState(GLFW.GLFW_KEY_5))
            {
                staticShader = tronShader
            }
            if(window.getKeyState(GLFW.GLFW_KEY_6))
            {
                staticShader = normalShader
            }

            when(points){
                20 -> speed = -25f
                40 -> speed = -30f
                60 -> speed = -35f
            }
        }
        else{
            if (window.getKeyState(GLFW.GLFW_KEY_SPACE)) {
                mode = 1
                lost = false
            }
        }

    }


    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {

    }

    fun onMouseMove(xpos: Double, ypos: Double) {
    }

    fun cleanup() {}

    fun spawnRings() {
        var newRing = ModelLoader.loadModel("assets/models/ring/ring2.obj", 0f, 0f, 0f)
        rings.add(newRing)
        rings[rings.size - 1]?.translateLocal(
            Vector3f(
                (Math.random() * 200f + 1f).toFloat(),
                (Math.random() * 200f + 1f).toFloat(),
                ringZPos
            )
        )
        rings[rings.size - 1]?.scaleLocal(Vector3f(0.5f))
        ringZPos -= 400f
    }
}

