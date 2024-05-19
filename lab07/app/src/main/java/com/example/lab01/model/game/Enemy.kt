package com.example.lab01.model.game

import com.example.lab01.model.shapes.Cube
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addTranslation
import com.example.lab01.utils.degrees
import kotlin.math.abs
import kotlin.math.atan2

class Enemy(model: Cube,
            position: Vector = Vector(0f, 0f, 0f),
            yaw: Float = 90f) : GameObject(model, position, yaw) {
    private val moveFactor = 0.1f
    private var rotateFactor = 0.1f
    lateinit var hero: Hero

    override fun init() {
        boundingSphere = BoundingSphere(
            center = model.getMassCenter() + position,
            radius = model.getMassCenter().distanceTo(model.getFartherPoint())
        )
        model.pipeline.add(position, function = ::addTranslation)
        updateDirection()
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        val newDirection = (hero.position - position).normalize()
        val angle = newDirection.angleWith(direction)
        if (!angle.isNaN() && abs(angle) > 0) {
            rotateAroundY(angle, rotateFactor)
            rotateActionCallback.invoke()
        }
        moveForward(moveFactor)
        model.draw(view, projection)
    }
}