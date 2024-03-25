package com.example.lab01.utils

import android.opengl.Matrix
import kotlin.math.cos
import kotlin.math.sin

class Camera {
    private var isEnabled = true
    private var cameraPos = Vector(0.0f, 0.0f, 3.0f);
    private var cameraTarget = Vector(0.0f, 0.0f, 0.0f);
    private var cameraUp = Vector(0.0f, 1.0f, 0.0f);
    private var viewMatrix = FloatArray(16)
    private val touchFactor: Float = 0.0065f
    private val positionFactor: Float = 1.2f
    private val directionFactor: Float = 10f
    private val zoomFactor: Float = 3f
    private var yaw = -90f
    private var pitch = 0f

    init {
        updateViewMatrix()
    }

    fun enable() {
        isEnabled = true
    }

    fun disable() {
        isEnabled = false
    }

    private fun updateViewMatrix() {
        Matrix.setLookAtM(viewMatrix, 0,
            cameraPos.x, cameraPos.y, cameraPos.z,
            cameraTarget.x, cameraTarget.y, cameraTarget.z,
            cameraUp.x, cameraUp.y, cameraUp.z)
    }

    fun getViewMatrix(): FloatArray {
        if (isEnabled) {
            updateViewMatrix()
        }
        return viewMatrix
    }

    fun updateCameraDirection(dx: Float, dy: Float) {
        yaw += dx * touchFactor * directionFactor
        pitch += -dy * touchFactor * directionFactor
        if(pitch > 89.0f)
            pitch = 89.0f
        if(pitch < -89.0f)
            pitch = -89.0f
        val direction = Vector()
        direction.x = cos(radians(yaw)) * cos(radians(pitch))
        direction.y = sin(radians(pitch))
        direction.z = sin(radians(yaw)) * cos(radians(pitch))
        cameraTarget = cameraPos + direction.normalize()
    }

    fun updateCameraPosition(dx: Float, dy: Float) {
        val upShift = cameraUp * dy * touchFactor * positionFactor
        val sideDirection = (cameraUp * (cameraTarget - cameraPos).normalize()).normalize()
        val sideShift = sideDirection * dx * touchFactor * positionFactor
        cameraPos += upShift
        cameraTarget += upShift
        cameraPos += sideShift
        cameraTarget += sideShift
    }

    fun updateCameraZoom(zoomStrength: Float) {
        val forwardShift = (cameraTarget - cameraPos).normalize() * zoomStrength * touchFactor * zoomFactor
        cameraPos += forwardShift
        cameraTarget += forwardShift
    }
}