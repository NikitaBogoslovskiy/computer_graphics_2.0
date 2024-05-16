package com.example.lab01.utils

import android.view.MotionEvent
import com.example.lab01.Dependencies.camera
import com.example.lab01.view.SurfaceView
import kotlin.math.acos
import kotlin.math.max

class SwipeManager(private var view: SurfaceView) {
    private var previousX: Float = 0f
    private lateinit var eventListener: (Float) -> Unit

    fun setEventListener(listener: (Float) -> Unit) {
        eventListener = listener
    }

    fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                previousX = e.getX(0)
            }

            MotionEvent.ACTION_MOVE -> {
                if (e.pointerCount == 1) {
                    val x = e.getX(0)
                    eventListener.invoke(x - previousX)
                    view.requestRender()
                    previousX = x
                }
            }
        }
        return true
    }
}