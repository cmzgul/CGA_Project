package cga.exercise.components.camera

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Math
import org.joml.Matrix4f

class TronCamera(var fieldOfView : Float = Math.toRadians(90f), var aspectRatio : Float = 16f/9f, var nearPlane : Float = 0.1f, var farPlane : Float = 100f) : ICamera, Transformable(){
    override fun getCalculateViewMatrix(): Matrix4f {
        return Matrix4f().lookAt(getWorldPosition(), getWorldPosition().sub(getWorldZAxis()), getWorldYAxis())
    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        return Matrix4f().perspective(fieldOfView, aspectRatio, nearPlane, farPlane)
    }

    override fun bind(shader: ShaderProgram) {
        shader.setUniform("view", getCalculateViewMatrix(), false)
        shader.setUniform("perspective", getCalculateProjectionMatrix(), false)
    }
}