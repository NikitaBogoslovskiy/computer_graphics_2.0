package com.example.lab01.utils

import android.opengl.Matrix
import kotlin.math.acos

class Camera {
    var isEnabled = true

    private var cameraPos = Vector(0.0f, 0.0f, 3.0f);
    private var cameraTarget = Vector(0.0f, 0.0f, 0.0f);
    private var cameraUp = Vector(0.0f, 1.0f, 0.0f);
    private var rotationMatrix = FloatArray(16)
    private var translationVector = Vector()
    private var startViewMatrix = FloatArray(16)
    private var viewMatrix = FloatArray(16)
    private val touchFactor: Float = 0.01f
    private var dUp = 0f
    private var dRight = 0f
    private var dForward = 0f

    init {
        Matrix.setIdentityM(rotationMatrix, 0)
        initViewMatrix()
    }

    private fun initViewMatrix() {
        Matrix.setLookAtM(startViewMatrix, 0,
            cameraPos.x, cameraPos.y, cameraPos.z,
            cameraTarget.x, cameraTarget.y, cameraTarget.z,
            cameraUp.x, cameraUp.y, cameraUp.z)
    }

    fun getViewMatrix(): FloatArray {
        if (!isEnabled) {
            return startViewMatrix
        }
        val tempMatrix = startViewMatrix.copyOf()
        Matrix.translateM(tempMatrix, 0, -cameraPos.x, -cameraPos.y, -cameraPos.z)
        Matrix.multiplyMM(viewMatrix, 0, rotationMatrix, 0, tempMatrix, 0)
        updateTranslationVector()
        Matrix.translateM(viewMatrix, 0, cameraPos.x, cameraPos.y, cameraPos.z)
        Matrix.translateM(
            viewMatrix,
            0,
            translationVector.x,
            translationVector.y,
            translationVector.z
        )
        return viewMatrix
    }

    fun updateCameraDirection(q: Quaternion) {
        Matrix.setRotateM(rotationMatrix, 0,
            (2.0f * acos(q.w) * 180.0f / Math.PI).toFloat(), q.y, -q.x, -q.z)
    }

    fun updateCameraPosition(dx: Float, dy: Float) {
        dRight = dx
        dUp = dy
    }

    fun updateCameraZoom(dy: Float) {
        dForward = dy
    }

    private fun updateTranslationVector() {
        val localUp = Vector(viewMatrix[1], viewMatrix[5], viewMatrix[9])
        val localRight = Vector(viewMatrix[0], viewMatrix[4], viewMatrix[8])
        val localForward = Vector(viewMatrix[2], viewMatrix[6], viewMatrix[10])
        translationVector += localUp * dUp * touchFactor
        translationVector += localRight * -dRight * touchFactor
        translationVector += localForward * -dForward * touchFactor
        dRight = 0f
        dUp = 0f
        dForward = 0f
    }
}