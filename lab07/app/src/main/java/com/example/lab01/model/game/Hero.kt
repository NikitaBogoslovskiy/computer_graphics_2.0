package com.example.lab01.model.game

import com.example.lab01.Dependencies
import com.example.lab01.model.light.TorchLight
import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.Mesh
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addScale
import com.example.lab01.utils.addTranslation

class Hero(model: Mesh,
           position: Vector = Vector(0f, 0f, 0f),
           yaw: Float = 0f) : GameObject(model, position, yaw) {
    var isMoving = false
    var torch: TorchLight? = null
    lateinit var winningActionCallback: () -> Unit
    private var moveFactor = 0.25f
    private var rotateFactor = 0.5f
    private var otherObjects = emptyList<GameObject>().toMutableList()

    override fun init() {
        super.init()
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
            } else if (obj is Bonus && hasCollisionWith(obj)) {
                Dependencies.scene.currentBonusesNumber.set(
                    Dependencies.scene.currentBonusesNumber.get()?.plus(1) ?: 0)
                if (Dependencies.scene.currentBonusesNumber.get() == Dependencies.scene.maxBonusesNumber.get())
                    winningActionCallback.invoke()
                else
                    obj.activateCallback.invoke()

            }
        }

        val boundaries = Dependencies.scene.sceneBoundaries
        val border = boundingSphere.center + boundingSphere.radius
        if (border.x < boundaries.minX - boundaries.shiftX ||
            border.x > boundaries.maxX + boundaries.shiftX ||
            border.z < boundaries.minZ - boundaries.shiftZ ||
            border.z > boundaries.maxZ + boundaries.shiftZ)
            hasCollisions = true

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
        Dependencies.camera.cameraPos = position.copy() - direction * 6f
        Dependencies.camera.cameraPos.y = position.y + 4f
        Dependencies.camera.cameraTarget = position.copy()
        Dependencies.camera.cameraTarget.y += 2.0f
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