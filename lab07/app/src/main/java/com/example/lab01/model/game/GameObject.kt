package com.example.lab01.model.game

import com.example.lab01.Dependencies
import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.Mesh
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addTranslation
import com.example.lab01.utils.radians
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

abstract class GameObject(protected var model: Cube,
                          protected var position: Vector = Vector(0f, 0f, 0f),
                          protected var yaw: Float = 0f) {

    protected var pitch = 0f
    protected var direction = Vector()
    protected var rotateActionCallback: () -> Unit = {}

    protected fun updateDirection() {
        direction.x = cos(radians(yaw)) * cos(radians(pitch))
        direction.y = sin(radians(pitch))
        direction.z = sin(radians(yaw)) * cos(radians(pitch))
        direction.normalize()
    }

    fun moveForward(moveFactor: Float) {
        val shift = direction * moveFactor
        position += shift
        model.pipeline.add(shift, function = ::addTranslation)
    }

    fun rotateAroundY(rawAngle: Float, rotateFactor: Float) {
        rotateActionCallback = {
            val angle = -rotateFactor * rawAngle
            yaw += rotateFactor * rawAngle
            updateDirection()
            model.pipeline.add(-position, function = ::addTranslation)
            model.pipeline.add(angle, Vector(0f, 1f, 0f), function = ::addRotation)
            model.pipeline.add(position, function = ::addTranslation)
        }
    }

    abstract fun draw(view: FloatArray, projection: FloatArray)
}