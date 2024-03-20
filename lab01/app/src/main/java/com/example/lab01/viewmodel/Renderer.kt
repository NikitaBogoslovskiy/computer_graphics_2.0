package com.example.lab01.viewmodel

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.RegularPolygon
import com.example.lab01.model.shapes.Shape
import com.example.lab01.model.shapes.Square
import com.example.lab01.model.shapes.TexturedSquare
import com.example.lab01.model.shapes.Triangle

class Renderer : GLSurfaceView.Renderer {
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    private lateinit var square: Shape
    private lateinit var triangle: Shape
    private lateinit var pentagon: Shape
    private lateinit var cube: Shape
    private lateinit var texturedSquare: Shape

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        square = Square()
        triangle = Triangle()
        pentagon = RegularPolygon(5)
        cube = Cube()
        texturedSquare = TexturedSquare()
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
/*        square.draw(vPMatrix)
        triangle.draw(vPMatrix)*/
        pentagon.draw(vPMatrix)
        cube.draw(vPMatrix)
        texturedSquare.draw(vPMatrix)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 12f)
    }
}