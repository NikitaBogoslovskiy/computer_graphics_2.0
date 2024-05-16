package com.example.lab01.utils

import android.opengl.Matrix

fun addTranslation(mat: FloatArray, args: List<Any>) {
    val shift = args[0] as Vector
    val translation = FloatArray(16)
    Matrix.setIdentityM(translation, 0)
    Matrix.translateM(translation, 0, shift.x, shift.y, shift.z)
    Matrix.multiplyMM(mat, 0, translation, 0, mat, 0)
}

fun addRotation(mat: FloatArray, args: List<Any>) {
    val angle: Float = args[0] as Float
    val axis: Vector = args[1] as Vector
    val rotation = FloatArray(16)
    Matrix.setRotateM(rotation, 0, angle, axis.x, axis.y, axis.z)
    Matrix.multiplyMM(mat, 0, rotation, 0, mat, 0)
}

fun addScale(mat: FloatArray, args: List<Any>) {
    val scaleFactor: Vector = args[0] as Vector
    val scale = FloatArray(16)
    Matrix.setIdentityM(scale, 0)
    Matrix.scaleM(scale, 0, scaleFactor.x, scaleFactor.y, scaleFactor.z)
    Matrix.multiplyMM(mat, 0, scale, 0, mat, 0)
}