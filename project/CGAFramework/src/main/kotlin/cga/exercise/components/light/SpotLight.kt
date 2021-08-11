package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import java.util.*

class SpotLight(pos : Vector3f, col : Vector3f, attenuation : Vector3f, private val innerAngle : Float, private val outerAngle : Float) : PointLight(pos, col,attenuation), ISpotLight {
    override fun bind(shaderProgram: ShaderProgram, name: String, viewMatrix: Matrix4f) {
        bind(shaderProgram, name)
        val direction = viewMatrix.transformDirection(getWorldZAxis().negate())
        shaderProgram.setUniform(name + "OuterAngle", Math.cos(Math.toRadians(outerAngle)))
        shaderProgram.setUniform(name + "InnerAngle", Math.cos(Math.toRadians(innerAngle)))
        shaderProgram.setUniform(name + "Direction", direction.x, direction.y, direction.z)
    }
}