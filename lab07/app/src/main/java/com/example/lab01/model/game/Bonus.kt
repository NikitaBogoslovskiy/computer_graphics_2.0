package com.example.lab01.model.game

import com.example.lab01.model.shapes.Cube
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addTranslation

class Bonus(model: Cube,
            position: Vector = Vector(0f, 0f, 0f),
            yaw: Float = 0f) : GameObject(model, position, yaw) {

    lateinit var activateCallback: () -> Unit
    var startPosition: Vector = Vector()

    override fun reset() {
        super.reset()
        startPosition = Vector()
    }

    override fun init() {
        boundingSphere = BoundingSphere(
            center = model.getMassCenter() + position,
            radius = model.getMassCenter().distanceTo(model.getFartherPoint())
        )
        model.pipeline.add(-startPosition.copy(), function = ::addTranslation)
        model.pipeline.add(position, function = ::addTranslation)
        startPosition = position.copy()
        updateDirection()
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        model.pipeline.add(-startPosition, function = ::addTranslation)
        model.pipeline.add(1f, Vector(0f, 1f, 0f), function = ::addRotation)
        model.pipeline.add(startPosition, function = ::addTranslation)
        model.draw(view, projection)
    }
}