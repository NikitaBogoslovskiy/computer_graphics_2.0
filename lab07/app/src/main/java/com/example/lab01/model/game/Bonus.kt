package com.example.lab01.model.game

import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.Mesh
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addScale
import com.example.lab01.utils.addTranslation

class Bonus(model: Mesh,
            position: Vector = Vector(0f, 0f, 0f),
            yaw: Float = 0f) : GameObject(model, position, yaw) {

    lateinit var activateCallback: () -> Unit
    var startPosition: Vector = Vector()
    var alreadyScaled = false

    override fun reset() {
        super.reset()
        startPosition = Vector()
        alreadyScaled = false
    }

    override fun init() {
        position.y -= minY * scaleFactor
        boundingSphere = BoundingSphere(
            center = position,
            radius = maxX * scaleFactor * 1.5f
        )
        model.pipeline.add(-startPosition.copy(), function = ::addTranslation)
        if (scaleFactor != 1f && !alreadyScaled) {
            model.pipeline.add(Vector(scaleFactor, scaleFactor, scaleFactor), function = ::addScale)
            alreadyScaled = true
        }
        model.pipeline.add(position, function = ::addTranslation)
        startPosition = position.copy()
        updateDirection()
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        model.pipeline.add(-startPosition, function = ::addTranslation)
        model.pipeline.add(2f, Vector(0f, 1f, 0f), function = ::addRotation)
        model.pipeline.add(startPosition, function = ::addTranslation)
        model.draw(view, projection)
    }
}