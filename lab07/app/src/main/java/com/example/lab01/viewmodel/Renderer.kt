package com.example.lab01.viewmodel

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.widget.SeekBar
import com.example.lab01.Dependencies
import com.example.lab01.Dependencies.camera
import com.example.lab01.model.scenes.Apelsinchik
import com.example.lab01.model.scenes.LOL
import com.example.lab01.model.scenes.Platform
//import com.example.lab01.model.scenes.Platform
import com.example.lab01.model.scenes.Scene
import com.example.lab01.model.scenes.Scene2d
import com.example.lab01.model.scenes.Scene2d3d
import com.example.lab01.utils.Camera
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class Renderer : GLSurfaceView.Renderer {
    private val projectionMatrix = FloatArray(16)
    private lateinit var scene: Scene

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        //camera.disable()
        //scene = Scene2d()
        //scene = Scene2d3d()
        //scene = Platform()
        //scene = Apelsinchik()
        scene = LOL()
    }

    override fun onDrawFrame(unused: GL10) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)
        scene.draw(camera.getViewMatrix(), projectionMatrix)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 1000f)
        Dependencies.gameInputManager.setScreenWidth(width.toFloat())
    }
}