package com.example.lab01.utils

import android.view.MotionEvent
import com.example.lab01.view.SurfaceView
import kotlin.math.max
import kotlin.math.min

class GameInputManager(private var view: SurfaceView) {
    private var previousX: Float = 0f
    private var screenWidth: Float = 0f
    private var halfScreenWidth: Float = 0f
    private lateinit var leftSideClickListener: (Boolean) -> Unit
    private lateinit var rightSideClickListener: (Float) -> Unit

    fun setScreenWidth(value: Float) {
        screenWidth = value
        halfScreenWidth = value / 2
    }

    fun setLeftSideClickListener(listener: (Boolean) -> Unit) {
        leftSideClickListener = listener
    }

    fun setRightSideClickListener(listener: (Float) -> Unit) {
        rightSideClickListener = listener
    }

    fun onTouchEvent(e: MotionEvent): Boolean {
        val pointersX = emptyList<Float>().toMutableList()
        for(i in 0 until e.pointerCount)
            pointersX.add(e.getX(i))
        val x = e.getX(e.actionIndex)
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (x < halfScreenWidth)
                    leftSideClickListener.invoke(true)
                else
                    previousX = x
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (x < halfScreenWidth)
                    leftSideClickListener.invoke(true)
                else
                    previousX = x
            }

            MotionEvent.ACTION_MOVE -> {
                for(currX in pointersX) {
                    if (currX >= halfScreenWidth) {
                        rightSideClickListener.invoke(currX - previousX)
                        view.requestRender()
                        previousX = currX
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                if (x < halfScreenWidth)
                    leftSideClickListener.invoke(false)
            }

            MotionEvent.ACTION_UP -> {
                if (x < halfScreenWidth)
                    leftSideClickListener.invoke(false)
            }
        }
        return true
    }
}