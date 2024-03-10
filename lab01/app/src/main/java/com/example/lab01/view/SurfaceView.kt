package com.example.lab01.view

import android.content.Context
import android.opengl.GLSurfaceView
import com.example.lab01.viewmodel.Renderer

class SurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: Renderer

    init {
        setEGLContextClientVersion(2)
        renderer = Renderer()
        setRenderer(renderer)
    }
}