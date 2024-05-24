package com.example.lab01.model.particles

import com.example.lab01.model.shapes.Line
import com.example.lab01.model.shapes.spriteColor
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addTranslation
import com.example.lab01.utils.radians
import java.util.Date
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class FireworkLine(
    private var startPoint: Vector = Vector(),
    private var endPoint: Vector = Vector(),
    private var startColor: Vector = Vector(1f, 1f, 1f),
    private var endColor: Vector = Vector(1f, 1f, 1f),
    private var startAlpha: Float = 1f,
    private var endAlpha: Float = 1f,
    private var time: Long = 1000,
    private val lineWidth: Float = 3f
) {
    lateinit var line: Line
    private var t: Float = 0f
    private val direction = endPoint - startPoint
    private val colorChange = endColor - startColor
    private val startAlphaChange = 0.075f
    private val endAlphaChange = 0.0115f
    private val verticalChange = 0.002f
    private var previousTime = Date()
    private var timeDelta: Long = 0
        get() {
            val currentTime = Date()
            val value = currentTime.time - previousTime.time
            previousTime = currentTime
            return value
        }
    private var multiplier = 0.8f
    var isAlmostOver = false
    var isOver = false

    fun init() {
        line = Line(
            startPoint = startPoint,
            endPoint = startPoint,
            startColor = startColor,
            endColor = startColor,
            startAlpha = startAlpha,
            endAlpha = endAlpha,
            lineWidth = lineWidth
        )
    }

    fun start() {
        previousTime = Date()
    }

    fun recalculate() {
        when {
            t < 1 -> {
                t += (timeDelta.toDouble() / time).toFloat() * multiplier
                if (multiplier > 0.2f)
                    multiplier -= 0.1f
                line.endPoint = startPoint + direction * t
                line.endColor = startColor + colorChange * t
                if (t >= 0.1) {
                    startAlpha -= startAlphaChange
                    line.startAlpha = startAlpha
                }
                line.processData()
            }
            startAlpha > 0f -> {
                startAlpha -= startAlphaChange
                line.startAlpha = startAlpha
                line.processData()
            }
            endAlpha > 0f -> {
                if (!isAlmostOver)
                    isAlmostOver = true
                endAlpha -= endAlphaChange
                line.endAlpha = endAlpha
                line.pipeline.add(Vector(0f, -verticalChange, 0f), function = ::addTranslation)
                line.processData()
            }
            else -> isOver = true
        }
    }

    fun draw(view: FloatArray, projection: FloatArray) {
        line.draw(view, projection)
    }
}

class Firework(
    private var linesNumber: Int = 100,
    private var startPosition: Vector = Vector(),
    private val startColor: Vector = Vector(1f, 1f, 1f),
    private val endColor: Vector = Vector(1f, 0f, 0f),
    private val minDistance: Float = 0.6f,
    private val maxDistance: Float = 0.8f,
    private val lineWidth: Float = 3f,
    private val time: Long) : Particle() {

    private var lines = emptyList<FireworkLine>().toMutableList()

    var isAlmostOver = false
    var isOver = false

    init {
        init()
    }

    override fun init() {
        for(i in 0 until linesNumber) {
            val angle = Random.nextFloat() * 360
            val distance = Random.nextFloat() * (maxDistance - minDistance) + minDistance
            val newLine = FireworkLine(
                startPoint = startPosition,
                endPoint = Vector(
                    x = startPosition.x + distance * cos(radians(angle)),
                    y = startPosition.y + distance * sin(radians(angle))
                ),
                startColor = startColor,
                endColor = endColor,
                startAlpha = 1f,
                endAlpha = 1f,
                time = time,
                lineWidth = lineWidth
            )
            newLine.init()
            lines.add(newLine)
        }
        for(line in lines)
            line.start()
    }


    override fun draw(view: FloatArray, projection: FloatArray) {
        val notOver = lines.filterNot { it.isOver }
        for(line in notOver) {
            line.draw(view, projection)
            line.recalculate()
        }
        isOver = notOver.isEmpty()
        isAlmostOver = lines.all { it.isAlmostOver }
    }
}