package com.example.lab01.utils

import kotlin.math.PI
import kotlin.math.sqrt

data class Quaternion(val w: Float = 0f,
                      val x: Float = 0f,
                      val y: Float = 0f,
                      val z: Float = 0f) {

    companion object {
        fun fromFloatArray(a: FloatArray): Quaternion {
            return Quaternion(a[0], a[1], a[2], a[3])
        }
    }
}

data class Vector(var x: Float = 0f,
                  var y: Float = 0f,
                  var z: Float = 0f) {

    fun toFloatArray() = floatArrayOf(x, y, z)

    companion object {
        fun fromFloatArray(a: FloatArray): Vector {
            return Vector(a[0], a[1], a[2])
        }
    }

    operator fun plus(other: Vector): Vector {
        return Vector(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: Vector): Vector {
        return Vector(x - other.x, y - other.y, z - other.z)
    }

    operator fun times(scalar: Float): Vector {
        return Vector(x * scalar, y * scalar, z * scalar)
    }

    operator fun times(other: Vector): Vector {
        val newX = y * other.z - z * other.y;
        val newY = z * other.x - x * other.z;
        val newZ = x * other.y - y * other.x;
        return Vector(newX, newY, newZ)
    }

    fun normalize(): Vector {
        val length = sqrt(x * x + y * y + z * z)
        return Vector(x / length, y / length, z / length)
    }
}

private const val MULTIPLIER = (PI / 180).toFloat()
fun radians(degrees: Float) = degrees * MULTIPLIER