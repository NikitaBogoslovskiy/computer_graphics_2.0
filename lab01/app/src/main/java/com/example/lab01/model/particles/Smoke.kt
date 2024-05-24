package com.example.lab01.model.particles

import com.example.lab01.R
import com.example.lab01.model.shapes.Line
import com.example.lab01.model.shapes.Sprite
import com.example.lab01.model.shapes.spriteColor
import com.example.lab01.utils.TextureData
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addScale
import com.example.lab01.utils.addTranslation
import com.example.lab01.utils.radians
import java.util.Date
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Smoke(
    size: Float = 0.75f,
    startPosition: Vector = Vector(),
    textureData: TextureData? = null,
    private var minX: Float = -0.5f,
    private var maxX: Float = 0.5f,
    private var minY: Float = -1.5f,
    private var maxY: Float = 1.5f) : Particle() {

    private var sprite = Sprite(sideLength = size, textureId = R.drawable.smoke, textureData = textureData)

    private var currentPosition: Vector = startPosition.copy()
    private var minXShift = -0.001f
    private var maxXShift = 0.001f
    private var minYShift = 0.005f
    private var maxYShift = 0.01f
    private var minScale = 0.5f
    private var maxScale = 1.5f
    private var minAlpha = 0.1f
    private var maxAlpha = 1f
    private var minAlphaChange = -0.05f
    private var maxAlphaChange = 0.05f

    init {
        init()
    }

    override fun init() {
        sprite.color = spriteColor.clone()
        sprite.color[3] = Random.nextFloat() * (maxAlpha - minAlpha) + minAlpha
        sprite.pipeline.add(Vector(
            x = Random.nextFloat() * (maxScale - minScale) + minScale,
            y = Random.nextFloat() * (maxScale - minScale) + minScale,
            z = 1f), function = ::addScale)
        sprite.pipeline.add(Random.nextFloat() * 360,
            Vector(0f, 0f, 1f), function = ::addRotation)
        sprite.pipeline.add(currentPosition, function = ::addTranslation)
    }

    private fun recalculate() {
        val shift = Vector(
            x = Random.nextFloat() * (maxXShift - minXShift) + minXShift,
            y = Random.nextFloat() * (maxYShift - minYShift) + minYShift,
        )
        val newPosition = currentPosition + shift
        newPosition.x = newPosition.x.coerceIn(minX, maxX)
        sprite.pipeline.add(newPosition - currentPosition, function = ::addTranslation)
        currentPosition = newPosition

        val alphaChange = Random.nextFloat() * (maxAlphaChange - minAlphaChange) + minAlphaChange
        sprite.color[3] = (sprite.color[3] + alphaChange).coerceIn(minAlpha, maxAlpha)
    }

    private fun tryReset() {
        if (currentPosition.y < maxY)
            return
        sprite.pipeline.reset()
        sprite.resetModelMatrix()
        currentPosition = Vector(
            x = Random.nextFloat() * (maxX - minX) + minX,
            y = minY
        )
        init()
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        tryReset()
        sprite.draw(view, projection)
        recalculate()
    }

    fun getTextureData() = sprite.getTextureData()
}