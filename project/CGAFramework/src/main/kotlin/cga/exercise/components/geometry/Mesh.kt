package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

/**
 * Creates a Mesh object from vertexdata, intexdata and a given set of vertex attributes
 *
 * @param vertexdata plain float array of vertex data
 * @param indexdata  index data
 * @param attributes vertex attributes contained in vertex data
 * @throws Exception If the creation of the required OpenGL objects fails, an exception is thrown
 *
 * Created by Fabian on 16.09.2017.
 */
class Mesh(vertexdata: FloatArray, indexdata: IntArray, attributes: Array<VertexAttribute>,private var material: Material? = null) {
    //private data
    private var vao = 0
    private var vbo = 0
    private var ibo = 0
    private var indexcount = 0

    init {
        // todo: place your code here

        indexcount = indexdata.size

        // todo: generate IDs
        vao = GL30.glGenVertexArrays()
        vbo = GL15.glGenBuffers()
        ibo = GL15.glGenBuffers()
        // todo: bind your objects
        GL30.glBindVertexArray(vao)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo)
        // todo: upload your mesh data
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexdata, GL15.GL_STATIC_DRAW)
        for(i in 0 until attributes.size)
        {
            GL20.glEnableVertexAttribArray(i);
            GL20.glVertexAttribPointer(i, attributes[i].n, attributes[i].type, false, attributes[i].stride, attributes[i].offset.toLong())
        }
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexdata, GL15.GL_STATIC_DRAW)
        GL30.glBindVertexArray(0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    /**
     * renders the mesh
     */
    fun render() {
        GL30.glBindVertexArray(vao)
        GL11.glDrawElements(GL11.GL_TRIANGLES, indexcount, GL11.GL_UNSIGNED_INT, 0)
        GL30.glBindVertexArray(0)
    }

    fun render(shaderProgram: ShaderProgram) {
        material?.bind(shaderProgram)
        render()
    }

    fun changeTexture(textureNumber : Int, texture : Texture2D){
        when(textureNumber){
            0 -> material?.diff = texture
            1 -> material?.emit = texture
            2 -> material?.specular = texture
        }
    }

    /**
     * Deletes the previously allocated OpenGL objects for this mesh
     */
    fun cleanup() {
        if (ibo != 0) GL15.glDeleteBuffers(ibo)
        if (vbo != 0) GL15.glDeleteBuffers(vbo)
        if (vao != 0) GL30.glDeleteVertexArrays(vao)
    }
}