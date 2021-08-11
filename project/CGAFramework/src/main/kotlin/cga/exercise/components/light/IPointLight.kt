package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

interface IPointLight {
    fun bind(shaderProgram: ShaderProgram, name: String)
}