package com.example.lab01.model.game

import com.example.lab01.Dependencies
import com.example.lab01.model.light.TorchLight
import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.Mesh
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addTranslation

class Hero(model: Cube,
           position: Vector = Vector(0f, 0f, 0f),
           yaw: Float = 0f) : GameObject(model, position, yaw) {
    var isMoving = false
    var torch: TorchLight? = null
    private var moveFactor = 0.05f
    private var rotateFactor = 0.5f
    private var otherObjects = emptyList<GameObject>().toMutableList()

    init {
        model.pipeline.add(position, function = ::addTranslation)
        updateDirection()
        relocateTorch()
        relocateCamera()
    }

    fun addOtherObjects(vararg args: GameObject) = otherObjects.addAll(args)

    fun rotateAroundY(rawAngle: Float) {
        super.rotateAroundY(rawAngle, rotateFactor)
    }

    private fun moveForward() {
        val shift = direction * moveFactor
        boundingSphere.center += shift

        var hasCollisions = false
        for(obj in otherObjects) {
            if (obj is Obstacle && hasCollisionWith(obj)) {
                hasCollisions = true
                break
            }
        }

        if (hasCollisions) {
            boundingSphere.center -= shift
        } else {
            position += shift
            model.pipeline.add(shift, function = ::addTranslation)
        }
    }

    private fun relocateTorch() {
        val light = torch ?: return
        light.position = position.toFloatArray()
        light.direction = direction.toFloatArray()
    }

    private fun relocateCamera() {
        Dependencies.camera.cameraPos = position.copy() - direction * 5f
        Dependencies.camera.cameraPos.y = position.y + 2f
        Dependencies.camera.cameraTarget = position.copy()
        Dependencies.camera.cameraTarget.y += 0.9f
        Dependencies.camera.updateViewMatrix()
    }

    fun doActions() {
        rotateActionCallback.invoke()
        rotateActionCallback = {}
        if (isMoving) {
            moveForward()
        }
        relocateTorch()
        relocateCamera()
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        model.draw(view, projection)
    }
}