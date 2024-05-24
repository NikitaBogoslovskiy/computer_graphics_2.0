package com.example.lab01.model.particles

import com.example.lab01.R
import com.example.lab01.model.shapes.Sprite
import com.example.lab01.model.shapes.spriteColor
import com.example.lab01.utils.TextureData
import com.example.lab01.utils.Vector

abstract class Particle() {
    abstract fun init()

    abstract fun draw(view: FloatArray, projection: FloatArray)
}