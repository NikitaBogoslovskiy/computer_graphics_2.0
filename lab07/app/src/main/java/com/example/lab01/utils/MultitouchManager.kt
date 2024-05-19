package com.example.lab01.utils

import android.view.MotionEvent
import com.example.lab01.Dependencies.camera
import com.example.lab01.view.SurfaceView
import kotlin.math.acos
import kotlin.math.max

class MultitouchManager(private var view: SurfaceView) {
    private var previousX0: Float = 0f
    private var previousY0: Float = 0f
    private var previousX1: Float = 0f
    private var previousY1: Float = 0f

    private var isSingleMode = true
    private var isEnabled = true

    fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isSingleMode = true
                previousX0 = e.getX(0)
                previousY0 = e.getY(0)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                isSingleMode = false
                isEnabled = true
                previousX1 = e.getX(1)
                previousY1 = e.getY(1)
            }

            MotionEvent.ACTION_MOVE -> {
                if (isEnabled && e.pointerCount <= 2) {
                    if (isSingleMode) {
                        val x0 = e.getX(0)
                        val y0 = e.getY(0)
                        camera.updateCameraPosition(x0 - previousX0, y0 - previousY0)
                        view.requestRender()
                        previousX0 = x0
                        previousY0 = y0
                    } else {
                        val x0 = e.getX(0)
                        val y0 = e.getY(0)
                        val x1 = e.getX(1)
                        val y1 = e.getY(1)
                        val dir1 = Vector(x0 - previousX0, y0 - previousY0)
                        val dir2 = Vector(x1 - previousX1, y1 - previousY1)
                        val oldDistance =
                            Point(previousX0, previousY0).distanceTo(Point(previousX1, previousY1))
                        val newDistance = Point(x0, y0).distanceTo(Point(x1, y1))
                        val angle = degrees(acos(dir1.dot2d(dir2) / (dir1.length() * dir2.length())))
                        if (angle < 60) {
                            camera.updateCameraDirection(dir1.x, dir1.y)
                        } else {
                            camera.updateCameraZoom(
                                sign(newDistance - oldDistance) * max(dir1.length(), dir2.length())
                            )
                        }
                        view.requestRender()
                        previousX0 = x0
                        previousY0 = y0
                        previousX1 = x1
                        previousY1 = y1
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                isSingleMode = true
                isEnabled = false
            }

            MotionEvent.ACTION_UP -> {
                isEnabled = true
            }
        }
        return true
    }
}