package com.example.lab01.model.game

import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.Mesh
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addTranslation
import com.example.lab01.utils.degrees
import com.example.lab01.utils.sign
import kotlin.math.abs
import kotlin.math.atan2

class Enemy(model: Mesh,
            position: Vector = Vector(0f, 0f, 0f),
            yaw: Float = 90f) : GameObject(model, position, yaw) {
    private val moveFactor = 0.08f
    private var rotateFactor = 1f
    lateinit var hero: Hero
    lateinit var failingActionCallback: () -> Unit
    var isActive = true

    override fun init() {
        super.init()
        updateDirection()
    }

    private fun moveForward() {
        val shift = direction * moveFactor
        boundingSphere.center += shift
        if (hasCollisionWith(hero))
            failingActionCallback.invoke()
        position += shift
        model.pipeline.add(shift, function = ::addTranslation)
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        if (isActive) {
            val newDirection = Vector(
                hero.position.x - position.x, 0f,
                hero.position.z - position.z
            ).normalize()
            val angle = newDirection.angleWith(direction)
            if (!angle.isNaN() && angle > 0.5f) {
                val cross = direction * newDirection
                val sign = sign(cross.dot3d(Vector(0f, 1f, 0f)))
                rotateAroundY(-angle * sign, rotateFactor)
                rotateActionCallback.invoke()
            }
            moveForward()
        }
        model.draw(view, projection)
    }
}