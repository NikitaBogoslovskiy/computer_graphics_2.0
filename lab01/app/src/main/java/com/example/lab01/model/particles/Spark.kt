package com.example.lab01.model.particles

import android.opengl.GLES20
import com.example.lab01.R
import com.example.lab01.model.shapes.Line
import com.example.lab01.model.shapes.Sprite
import com.example.lab01.model.shapes.spriteColor
import com.example.lab01.utils.TextureData
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addTranslation
import com.example.lab01.utils.radians
import java.time.LocalDateTime
import java.util.Date
import java.util.Timer
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Spark(size: Float = 0.15f, textureData: TextureData? = null) : Particle() {

    private var sprite = Sprite(sideLength = size, textureId = R.drawable.spark, textureData = textureData)

    private lateinit var startPoint: Vector
    private lateinit var endPoint: Vector
    private lateinit var direction: Vector
    private var speed: Float = 0f
    private var currentPosition: Float = 0f

    private val maxStartShift = 0.05f
    private val minDistance = 0.2f
    private val maxDistance = 0.9f
    private val minSpeed = 0.0015f
    private val maxSpeed = 0.0025f
    private val startColor = Vector(1f, 1f, 1f)
    private val endColor = Vector(0.97f, 0.71f, 0f)
    private var track: Line = Line()
    private val colorChange = endColor - startColor
    private var previousTime = Date()
    private var currentTime = Date()

    init {
        init()
    }

    override fun init() {
        val angle = Random.nextFloat() * 360
        val distance = Random.nextFloat() * (maxDistance - minDistance) + minDistance
        val shift = Vector(
            x = Random.nextFloat() * maxStartShift,
            y = Random.nextFloat() * maxStartShift
        )
        startPoint = Vector() + shift
        endPoint = Vector(
            x = startPoint.x + distance * cos(radians(angle)),
            y = startPoint.y + distance * sin(radians(angle))
        )
        speed = Random.nextFloat() * (maxSpeed - minSpeed) + minSpeed
        direction = endPoint - startPoint
        currentPosition = 0f
        sprite.color = spriteColor
        sprite.pipeline.add(Random.nextFloat() * 360, Vector(0f, 0f, 1f), function = ::addRotation)
        sprite.pipeline.add(startPoint, function = ::addTranslation)
        track.startPoint = startPoint
        track.endPoint = startPoint
        track.startColor = startColor
        track.endColor = startColor
        track.processData()
    }

    fun drawTrack(view: FloatArray, projection: FloatArray) {
        track.draw(view, projection)
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        if (currentPosition >= 1) {
            sprite.pipeline.add(-(startPoint + direction * currentPosition), function = ::addTranslation)
            init()
        }

        sprite.draw(view, projection)

        if (currentPosition < 1) {
            currentTime = Date()
            val timeDelta = (currentTime.time - previousTime.time)
            previousTime = currentTime
            currentPosition += speed * timeDelta
            track.endPoint = startPoint + direction * currentPosition
            track.endColor = startColor + colorChange * currentPosition
            track.processData()
            sprite.pipeline.add(direction * speed * timeDelta, function = ::addTranslation)
            sprite.color = floatArrayOf(track.endColor.x, track.endColor.y, track.endColor.z, 1f)
        }
    }

    fun getTextureData() = sprite.getTextureData()
}