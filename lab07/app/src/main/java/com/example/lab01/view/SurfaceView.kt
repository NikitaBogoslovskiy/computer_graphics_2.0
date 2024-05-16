package com.example.lab01.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.lab01.Dependencies
import com.example.lab01.utils.MultitouchManager
import com.example.lab01.utils.SwipeManager
import com.example.lab01.viewmodel.Renderer


class SurfaceView : GLSurfaceView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    val renderer: com.example.lab01.viewmodel.Renderer
    private var multitouchManager = MultitouchManager(this)
    private var swipeManager = SwipeManager(this)

    init {
        Dependencies.swipeManager = swipeManager
        setEGLContextClientVersion(2)
        renderer = Renderer()
        setRenderer(renderer)
        //renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return swipeManager.onTouchEvent(e)
    }
}