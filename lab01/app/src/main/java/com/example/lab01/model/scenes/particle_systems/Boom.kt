package com.example.lab01.model.scenes.particle_systems

import com.example.lab01.model.particles.Explosion
import com.example.lab01.model.particles.Firework
import com.example.lab01.model.scenes.Scene
import com.example.lab01.utils.Vector
import kotlin.random.Random

class Boom : Scene {
    private lateinit var explosion: Explosion

    init {
        initExplosion()
    }

    private fun initExplosion() {
        explosion = Explosion()
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        explosion.draw(view, projection)
        if (explosion.isOver)
            initExplosion()
    }
}