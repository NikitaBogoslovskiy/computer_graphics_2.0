package com.example.lab01.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.KeyEvent
import android.view.MotionEvent
import com.example.lab01.utils.MultitouchManager
import com.example.lab01.viewmodel.Renderer
class SurfaceView(context: Context) : GLSurfaceView(context) {

    val renderer: com.example.lab01.viewmodel.Renderer
    private var multitouchManager = MultitouchManager(this)

    init {
        setEGLContextClientVersion(2)
        renderer = Renderer()
        setRenderer(renderer)
        //renderMode = RENDERMODE_WHEN_DIRTY;
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return multitouchManager.onTouchEvent(e)
    }
}