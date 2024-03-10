package com.example.lab01.viewmodel

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.example.lab01.model.shapes.Square
import com.example.lab01.model.shapes.Triangle

class Renderer : GLSurfaceView.Renderer {
    private lateinit var square: Square
    private lateinit var triangle: Triangle

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        square = Square()
        triangle = Triangle()
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        square.draw()
        //triangle.draw()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }
}