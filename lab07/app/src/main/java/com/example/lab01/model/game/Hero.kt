package com.example.lab01.model.game

import com.example.lab01.Dependencies
import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.Mesh
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addTranslation

class Hero(model: Cube,
           position: Vector = Vector(0f, 0f, 0f),
           yaw: Float = 0f) : GameObject(model, position, yaw) {
    var isMoving = false
    private var moveFactor = 0.05f
    private var rotateFactor = 0.5f

    init {
        model.pipeline.add(position, function = ::addTranslation)
        updateDirection()
        relocateCamera()
    }

    fun rotateAroundY(rawAngle: Float) {
        super.rotateAroundY(rawAngle, rotateFactor)
    }

    private fun relocateCamera() {
        Dependencies.camera.cameraPos = position - direction * 6f
        Dependencies.camera.cameraPos.y = position.y + 2f
        Dependencies.camera.cameraTarget = Dependencies.camera.cameraPos + direction
        Dependencies.camera.cameraTarget.y -= 0.1f
        Dependencies.camera.updateViewMatrix()
    }

    fun doActions() {
        rotateActionCallback.invoke()
        rotateActionCallback = {}
        if (isMoving) {
            moveForward(moveFactor)
        }
        relocateCamera()
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        model.draw(Dependencies.camera.getViewMatrix(), projection)
    }
}