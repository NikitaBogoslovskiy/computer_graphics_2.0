package com.example.lab01.model.game

import com.example.lab01.Dependencies
import com.example.lab01.model.shapes.Cube
import com.example.lab01.utils.Vector

class Hero(model: Cube,
           position: Vector = Vector(0f, 0f, 0f),
           yaw: Float = 0f) : GameObject(model, position, yaw) {
    var isMoving = false
    private var moveFactor = 0.05f
    private var rotateFactor = 0.5f

    fun rotateAroundY(rawAngle: Float) {
        super.rotateAroundY(rawAngle, rotateFactor)
    }

    private fun relocateCamera() {
        Dependencies.camera.cameraPos = position - direction * 5f
        Dependencies.camera.cameraPos.y = position.y + 1f
        Dependencies.camera.cameraTarget = Dependencies.camera.cameraPos + direction
        Dependencies.camera.updateViewMatrix()
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        rotateActionCallback.invoke()
        rotateActionCallback = {}
        if (isMoving) {
            moveForward(moveFactor)
        }
        relocateCamera()
        model.draw(Dependencies.camera.getViewMatrix(), projection)
    }
}