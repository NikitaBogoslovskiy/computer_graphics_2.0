package com.example.lab01.model.scenes

import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.Mesh
import com.example.lab01.model.utility.PlatformModeEnum
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addTranslation
import java.util.concurrent.atomic.AtomicBoolean


class Apelsinchik : Scene {
    private var mesh: Mesh

    init {
        //Dependencies.pointLight.position = floatArrayOf(-4f, 4f, 1f)
        mesh = Mesh(R.raw.cat, R.drawable.pupirishki)
        mesh.pipeline.addUnique(
            Vector(3f, 0f, 0f),
            function = ::addTranslation)
        mesh.pipeline.addRepeatable(Vector(-3f, 0f, 0f),
            function = ::addTranslation)
        mesh.pipeline.addRepeatable(1f, Vector(0f, 1f, 0f),
                function = ::addRotation)
        mesh.pipeline.addRepeatable(Vector(3f, 0f, 0f),
            function = ::addTranslation)
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        mesh.draw(view, projection)
    }
}