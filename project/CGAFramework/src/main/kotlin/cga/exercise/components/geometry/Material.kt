package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL13.*

class Material(var diff: Texture2D,
               var emit: Texture2D,
               var specular: Texture2D,
               var shininess: Float = 50.0f,
               var tcMultiplier : Vector2f = Vector2f(1.0f)){

    fun bind(shaderProgram: ShaderProgram){
        diff.bind(GL_TEXTURE0)
        emit.bind(GL_TEXTURE1)
        specular.bind(GL_TEXTURE2)
        shaderProgram.setUniform("material.diff", 0)
        shaderProgram.setUniform("material.emit", 1)
        shaderProgram.setUniform("material.specular", 2)
        shaderProgram.setUniform("tcMultiplier", tcMultiplier.x, tcMultiplier.y)
        shaderProgram.setUniform("material.shininess", shininess)
    }
}