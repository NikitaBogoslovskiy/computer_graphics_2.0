package com.example.lab01.model.scenes

import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.RegularPolygon
import com.example.lab01.model.shapes.TexturedSquare
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addTranslation

class Scene2d3d : Scene {
    private var pentagon = RegularPolygon(5)
    private var cube = Cube()
    private var texturedSquare = TexturedSquare()

    init {
        cube.pipeline.addUnique(-45f, Vector(1f, 1f, 0f),
            function = ::addRotation)
        cube.pipeline.addUnique(Vector(0f, 0f, -3f),
            function = ::addTranslation)
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        pentagon.draw(view, projection)
        cube.draw(view, projection)
        texturedSquare.draw(view, projection)
    }
}