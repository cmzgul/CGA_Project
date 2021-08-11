package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f


open class Transformable(var modelMatrix: Matrix4f = Matrix4f(), var parent: Transformable? = null) {

    /**
     * Rotates object around its own origin.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     */
    fun rotateLocal(pitch: Float, yaw: Float, roll: Float) {
        modelMatrix = modelMatrix.rotateXYZ(pitch, yaw, roll)
    }

    /**
     * Rotates object around given rotation center.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     * @param altMidpoint rotation center
     */
    fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        var matrix = Matrix4f()
        matrix.translate(altMidpoint)
        matrix.rotateXYZ(pitch, yaw, roll)
        matrix.translate(Vector3f(altMidpoint).negate())
        modelMatrix = matrix.mul(modelMatrix)
    }

    /**
     * Translates object based on its own coordinate system.
     * @param deltaPos delta positions
     */
    fun translateLocal(deltaPos: Vector3f) {
        modelMatrix = modelMatrix.translate(deltaPos)
    }

    /**
     * Translates object based on its parent coordinate system.
     * Hint: global operations will be left-multiplied
     * @param deltaPos delta positions (x, y, z)
     */
    fun translateGlobal(deltaPos: Vector3f) {
        val translation_matrix = Matrix4f().translate(deltaPos)
        modelMatrix = translation_matrix.mul(modelMatrix)
    }

    /**
     * Scales object related to its own origin
     * @param scale scale factor (x, y, z)
     */
    fun scaleLocal(scale: Vector3f) {
        modelMatrix = modelMatrix.scale(scale)
    }

    /**
     * Returns position based on aggregated translations.
     * Hint: last column of model matrix
     * @return position
     */
    fun getPosition(): Vector3f {
        return Vector3f(modelMatrix.m30(), modelMatrix.m31(), modelMatrix.m32())
    }

    /**
     * Returns position based on aggregated translations incl. parents.
     * Hint: last column of world model matrix
     * @return position
     */
    fun getWorldPosition(): Vector3f {
        val world_modelmatrix = getWorldModelMatrix()
        return Vector3f(world_modelmatrix.m30(), world_modelmatrix.m31(), world_modelmatrix.m32())
    }

    /**
     * Returns x-axis of object coordinate system
     * Hint: first normalized column of model matrix
     * @return x-axis
     */
    fun getXAxis(): Vector3f {
        return Vector3f(modelMatrix.m00(), modelMatrix.m01(), modelMatrix.m02()).normalize()
    }

    /**
     * Returns y-axis of object coordinate system
     * Hint: second normalized column of model matrix
     * @return y-axis
     */
    fun getYAxis(): Vector3f {
        return Vector3f(modelMatrix.m10(), modelMatrix.m11(), modelMatrix.m12()).normalize()
    }

    /**
     * Returns z-axis of object coordinate system
     * Hint: third normalized column of model matrix
     * @return z-axis
     */
    fun getZAxis(): Vector3f {
        return Vector3f(modelMatrix.m20(), modelMatrix.m21(), modelMatrix.m22()).normalize()
    }

    /**
     * Returns x-axis of world coordinate system
     * Hint: first normalized column of world model matrix
     * @return x-axis
     */
    fun getWorldXAxis(): Vector3f {
        var world_modelmatrix = getWorldModelMatrix()
        return Vector3f(world_modelmatrix.m00(), world_modelmatrix.m01(), world_modelmatrix.m02()).normalize()
    }

    /**
     * Returns y-axis of world coordinate system
     * Hint: second normalized column of world model matrix
     * @return y-axis
     */
    fun getWorldYAxis(): Vector3f {
        var world_modelmatrix = getWorldModelMatrix()
        return Vector3f(world_modelmatrix.m10(), world_modelmatrix.m11(), world_modelmatrix.m12()).normalize()
    }

    /**
     * Returns z-axis of world coordinate system
     * Hint: third normalized column of world model matrix
     * @return z-axis
     */
    fun getWorldZAxis(): Vector3f {
        var world_modelmatrix = getWorldModelMatrix()
        return Vector3f(world_modelmatrix.m20(), world_modelmatrix.m21(), world_modelmatrix.m22()).normalize()
    }

    /**
     * Returns multiplication of world and object model matrices.
     * Multiplication has to be recursive for all parents.
     * Hint: scene graph
     * @return world modelMatrix
     */
    fun getWorldModelMatrix(): Matrix4f {
        var platzhalter = getLocalModelMatrix()
        parent?.getWorldModelMatrix()?.mul(modelMatrix, platzhalter)
        return platzhalter
    }

    /**
     * Returns object model matrix
     * @return modelMatrix
     */
    fun getLocalModelMatrix(): Matrix4f {
        return Matrix4f(modelMatrix)
    }
}