package com.example.lab01.model.game

import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.Mesh
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addTranslation

class Obstacle(model: Mesh,
               position: Vector = Vector(0f, 0f, 0f),
               yaw: Float = 0f) : GameObject(model, position, yaw) {

    override fun init() {
        super.init()
        updateDirection()
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        model.draw(view, projection)
    }
}