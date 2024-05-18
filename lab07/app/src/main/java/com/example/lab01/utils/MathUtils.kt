package com.example.lab01.utils

import kotlin.math.PI
import kotlin.math.sqrt

data class Point(var x: Float = 0f,
                 var y: Float = 0f,
                 var z: Float = 0f) {
    fun distanceTo(other: Point) = sqrt((other.x - x) * (other.x - x) +
                                            (other.y - y) * (other.y - y) +
                                            (other.z - z) * (other.z - z))
}

data class Vector(var x: Float = 0f,
                  var y: Float = 0f,
                  var z: Float = 0f) {

    operator fun plus(other: Vector): Vector {
        return Vector(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: Vector): Vector {
        return Vector(x - other.x, y - other.y, z - other.z)
    }

    operator fun times(scalar: Float): Vector {
        return Vector(x * scalar, y * scalar, z * scalar)
    }

    operator fun unaryMinus(): Vector {
        return Vector(-x, -y, -z)
    }

    operator fun times(other: Vector): Vector {
        val newX = y * other.z - z * other.y;
        val newY = z * other.x - x * other.z;
        val newZ = x * other.y - y * other.x;
        return Vector(newX, newY, newZ)
    }

    fun dot(other: Vector) = x * other.x + y * other.y

    fun length() = sqrt(x * x + y * y + z * z)

    fun normalize(): Vector {
        val l = length()
        return Vector(x / l, y / l, z / l)
    }

    fun toFloatArray() = floatArrayOf(x, y, z)
}

private const val MULTIPLIER = (PI / 180).toFloat()
fun radians(degrees: Float) = degrees * MULTIPLIER
fun degrees(radians: Float) = radians / MULTIPLIER
fun sign(value: Float): Float {
    return when {
        value < 0f -> -1f
        value > 0f -> 1f
        else -> 0f
    }
}