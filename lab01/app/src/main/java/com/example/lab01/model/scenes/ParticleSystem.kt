package com.example.lab01.model.scenes

import com.example.lab01.R
import com.example.lab01.model.particles.Spark
import com.example.lab01.model.shapes.Line
import com.example.lab01.model.shapes.Sprite
import com.example.lab01.utils.Vector

class ParticleSystem : Scene {
    private var sparks: MutableList<Spark> = emptyList<Spark>().toMutableList()
    private val sparksNumber = 125

    init {
        sparks.add(Spark())
        for(i in 0 until sparksNumber)
            sparks.add(Spark(textureData = sparks[0].getTextureData()))
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        for(spark in sparks)
            spark.drawTrack(view, projection)
        for(spark in sparks)
            spark.draw(view, projection)
    }
}