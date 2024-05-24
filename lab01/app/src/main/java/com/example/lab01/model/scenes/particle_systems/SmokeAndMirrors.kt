package com.example.lab01.model.scenes.particle_systems

import com.example.lab01.model.particles.Smoke
import com.example.lab01.model.particles.Spark
import com.example.lab01.model.scenes.Scene
import com.example.lab01.utils.TextureData
import com.example.lab01.utils.Vector
import kotlin.random.Random

class SmokeAndMirrors : Scene {
    private var smokes: MutableList<Smoke> = emptyList<Smoke>().toMutableList()
    private val smokesNumber = 100
    private var minX: Float = -0.5f
    private var maxX: Float = 0.5f
    private var minY: Float = -2f
    private var maxY: Float = 2f

    init {
        addSmoke()
        val textureData = smokes.first().getTextureData()
        for(i in 1 until smokesNumber)
            addSmoke(textureData)
    }

    private fun addSmoke(textureData: TextureData? = null) {
        smokes.add(Smoke(
            startPosition = Vector(
                x = Random.nextFloat() * (maxX - minX) + minX,
                y = Random.nextFloat() * (maxY - minY) + minY,
            ),
            minX = minX,
            maxX = maxX,
            minY = minY,
            maxY = maxY,
            textureData = textureData
        ))
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        for(smoke in smokes)
            smoke.draw(view, projection)
    }
}