package com.example.lab01.model.utility

import android.opengl.GLES20

fun loadShader(type: Int, shaderCode: String): Int =
    GLES20.glCreateShader(type).also { shader ->
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
    }