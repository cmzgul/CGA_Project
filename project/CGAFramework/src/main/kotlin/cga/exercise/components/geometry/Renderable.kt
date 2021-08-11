package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

class Renderable (var meshes : MutableList<Mesh> = mutableListOf(), modelMatrix: Matrix4f = Matrix4f(), parent: Transformable? = null) : IRenderable,
    Transformable(modelMatrix, parent) {
    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", getWorldModelMatrix(), false)
        meshes.forEach{
            it.render(shaderProgram)
        }
    }
}