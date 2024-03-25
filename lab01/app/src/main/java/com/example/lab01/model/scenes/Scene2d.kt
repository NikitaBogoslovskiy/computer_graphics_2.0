package com.example.lab01.model.scenes

import android.opengl.Matrix
import com.example.lab01.model.shapes.Square
import com.example.lab01.model.shapes.Triangle

class Scene2d : Scene {
    private var square: Square = Square()
    private var triangle: Triangle = Triangle()

    override fun draw(vPMatrix: FloatArray) {
        square.draw(vPMatrix)
        triangle.draw(vPMatrix)
    }
}