package com.example.lab01.model.scenes

import android.opengl.Matrix

interface Scene {
    fun draw(vPMatrix: FloatArray)
}