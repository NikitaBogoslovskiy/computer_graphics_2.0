package com.example.lab01.viewmodel

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.lab01.model.scenes.Platform
//import com.example.lab01.model.scenes.Platform
import com.example.lab01.model.scenes.Scene
import com.example.lab01.model.scenes.Scene2d
import com.example.lab01.model.scenes.Scene2d3d
import com.example.lab01.utils.Camera
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class Renderer : GLSurfaceView.Renderer {
    val camera = Camera()
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private lateinit var scene: Scene

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        //camera.isEnabled = false
        //scene = Scene2d()
        //scene = Scene2d3d()
        scene = Platform()
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, camera.getViewMatrix(), 0)
        scene.draw(vPMatrix)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 100f)
    }
}