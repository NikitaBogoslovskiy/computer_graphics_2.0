package com.example.lab01.model.utility

import android.opengl.GLES30

fun loadShader(type: Int, shaderCode: String): Int =
    GLES30.glCreateShader(type).also { shader ->
        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)
    }