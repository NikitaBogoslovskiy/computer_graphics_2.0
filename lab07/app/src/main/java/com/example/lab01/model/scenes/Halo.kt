package com.example.lab01.model.scenes

import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.model.shapes.Mesh
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addScale
import com.example.lab01.utils.addTranslation

class Halo : Scene {
    private var mesh: Mesh
    private var minX = -3.7f
    private var maxX = 3.65f

    init {
        Dependencies.pointLight.position = floatArrayOf(0f, 0f, 5f)
        mesh = Mesh(
            modelFileId = R.raw.spaceship,
            textureResourceId1 = R.drawable.spaceship_texture
        )
        Dependencies.swipeManager.setEventListener {
            val shift = it * 0.01f
            val currentX = mesh.getCurrentPosition().x
            if ((currentX + shift) >= minX && (currentX + shift) <= maxX) {
                mesh.pipeline.addUnique(
                    Vector(shift, 0f, 0f),
                    function = ::addTranslation
                )
                mesh.pipeline.hasExecutedUnique = false
            }
        }
/*        mesh.pipeline.addUnique(
            Vector(3f, 3f, 3f),
            function = ::addScale)
        mesh.pipeline.addUnique(
            Vector(0f, 0f, 0f),
            function = ::addTranslation)*/
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        mesh.draw(view, projection)
    }
}