package com.example.lab01.model.scenes.particle_systems

import com.example.lab01.model.particles.Firework
import com.example.lab01.model.particles.Spark
import com.example.lab01.model.scenes.Scene
import com.example.lab01.utils.Vector
import kotlin.concurrent.thread
import kotlin.random.Random

class Fireworks : Scene {
    private lateinit var firework: Firework

    private var minLinesNumber = 50
    private var maxLinesNumber = 200
    private var minStartPositionX = -1f
    private var maxStartPositionX = 1f
    private var minStartPositionY = -0.5f
    private var maxStartPositionY = 0.5f
    private var minMinDistance = 0.3f
    private var maxMinDistance = 0.5f
    private var minMaxDistance = 0.8f
    private var maxMaxDistance = 1.2f
    private var minLineWidth = 4f
    private var maxLineWidth = 7f
    private val minTime: Long = 100
    private val maxTime: Long = 145

    init {
        addRandomFirework()
    }

    private fun addRandomFirework() {
        firework = Firework(
            linesNumber = Random.nextInt(minLinesNumber, maxLinesNumber),
            startPosition = Vector(
                x = Random.nextFloat() * (maxStartPositionX - minStartPositionX) + minStartPositionX,
                y = Random.nextFloat() * (maxStartPositionY - minStartPositionY) + minStartPositionY,
            ),
            startColor = Vector(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()),
            endColor = Vector(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()),
            minDistance = Random.nextFloat() * (maxMinDistance - minMinDistance) + minMinDistance,
            maxDistance = Random.nextFloat() * (maxMaxDistance - minMaxDistance) + minMaxDistance,
            lineWidth = Random.nextFloat() * (maxLineWidth - minLineWidth) + minLineWidth,
            time = Random.nextLong(minTime, maxTime)
        )
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        firework.draw(view, projection)
        if (firework.isOver)
            addRandomFirework()
    }
}