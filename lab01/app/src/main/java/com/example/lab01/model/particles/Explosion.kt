package com.example.lab01.model.particles

import com.example.lab01.R
import com.example.lab01.model.shapes.Line
import com.example.lab01.model.shapes.Sprite
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

class Flame (
    private val textureData: TextureData? = null,
    private var startPosition: Vector = Vector(),
    private var endPosition: Vector = Vector(),
    private var startColor: Vector = Vector(1f, 1f, 1f),
    private var endColor: Vector = Vector(1f, 1f, 1f),
    private var startScale: Float = 0.1f,
    private var endScale: Float = 2f,
    private var time: Long = 1000,
) {
    val sprite = Sprite(
        sideLength = 1f,
        color = floatArrayOf(startColor.x, startColor.y, startColor.z, 1f),
        textureId = R.drawable.explosion,
        textureData = textureData
    )

    private var t: Float = 0f
    private var speed = Random.nextFloat() * 0.007f + 0.075f
    private val direction = endPosition - startPosition
    private val colorChange = endColor - startColor
    private val scaleChange = endScale - startScale
    private var previousTime = Date()
    private var multiplier = 1.2f
    private val timeDelta: Long
        get() {
            val currentTime = Date()
            val value = currentTime.time - previousTime.time
            previousTime = currentTime
            return value
        }
    var isOver = false

    fun init() {
        sprite.color[3] = 0f
        sprite.pipeline.add(Vector(
            x = startScale,
            y = startScale,
            z = 1f
        ), function = ::addScale)
        sprite.pipeline.add(Random.nextFloat() * 360, Vector(0f, 0f, 1f),
            function = ::addRotation)
    }

    fun start() {
        previousTime = Date()
    }

    fun recalculate() {
        val oldPosition = startPosition + direction * t
        val oldScale = startScale + scaleChange * t
        //t += (timeDelta.toDouble() / time).toFloat()
        t += speed
        speed *= multiplier
        multiplier *= 1.2f
        if (t > 1) {
            if (sprite.color[3] <= -0.1f) {
                isOver = true
            } else {
                sprite.color[3] -= 0.015f
            }
            return
        }
        val newPosition = startPosition + direction * t
        val newColor = startColor + colorChange * t
        val newScale = startScale + scaleChange * t

        sprite.pipeline.add(-oldPosition, function = ::addTranslation)
        sprite.pipeline.add(Vector(
            x = 1 / oldScale,
            y = 1 / oldScale,
            z = 1f
        ), function = ::addScale)
        sprite.pipeline.add(Vector(
            x = newScale,
            y = newScale,
            z = 1f
        ), function = ::addScale)
        sprite.pipeline.add(newPosition, function = ::addTranslation)
        sprite.color = floatArrayOf(newColor.x, newColor.y, newColor.z, t * 0.7f)
    }

    fun draw(view: FloatArray, projection: FloatArray) {
        sprite.draw(view, projection)
        if (!isOver)
            recalculate()
    }
}

class Explosion (
    private var flamesNumber: Int = 100,
    private var startPosition: Vector = Vector(),
    private val startColor: Vector = Vector(1f, 1f, 1f),
    private val endColor: Vector = Vector(1f, 1f, 1f),
    private val minDistance: Float = 0.6f,
    private val maxDistance: Float = 0.7f,
    private val minStartScale: Float = 0.001f,
    private val maxStartScale: Float = 0.001f,
    private val minEndScale: Float = 3f,
    private val maxEndScale: Float = 8f,
    private val time: Long = 5000) : Particle() {

    private var flames = emptyList<Flame>().toMutableList()

    var isOver = false

    init {
        init()
    }

    private fun addFlame(textureData: TextureData? = null) {
        val angle = Random.nextFloat() * 360
        val distance = Random.nextFloat() * (maxDistance - minDistance) + minDistance
        val flame = Flame(
            textureData = textureData,
            startPosition = startPosition,
            endPosition = Vector(
                x = startPosition.x + distance * cos(radians(angle)),
                y = startPosition.y + distance * sin(radians(angle))
            ),
            startColor = startColor,
            endColor = endColor,
            startScale = Random.nextFloat() * (maxStartScale - minStartScale) + minStartScale,
            endScale = Random.nextFloat() * (maxEndScale - minEndScale) + minEndScale,
            time = time
        )
        flame.init()
        flames.add(flame)
    }

    override fun init() {
        addFlame()
        val textureData = flames.first().sprite.getTextureData()
        for(i in 1 until flamesNumber)
            addFlame(textureData)
        for(flame in flames)
            flame.start()
    }


    override fun draw(view: FloatArray, projection: FloatArray) {
        for(flame in flames) {
            flame.draw(view, projection)
            flame.recalculate()
        }
        isOver = flames.all { it.isOver }
    }
}