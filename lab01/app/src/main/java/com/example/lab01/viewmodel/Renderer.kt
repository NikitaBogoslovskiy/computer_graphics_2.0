package com.example.lab01.viewmodel

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.lab01.Dependencies.camera
import com.example.lab01.model.scenes.particle_systems.BengalFire
import com.example.lab01.model.scenes.Scene
import com.example.lab01.model.scenes.particle_systems.Boom
import com.example.lab01.model.scenes.particle_systems.Fireworks
import com.example.lab01.model.scenes.particle_systems.SmokeAndMirrors
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class Renderer : GLSurfaceView.Renderer {
    private val projectionMatrix = FloatArray(16)
    private lateinit var scene: Scene

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        //GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        camera.disable()
        //scene = Scene2d()
        //scene = Scene2d3d()
        //scene = Platform()
        //scene = Apelsinchik()
        //scene = BengalFire()
        //scene = Fireworks()
        //scene = SmokeAndMirrors()
        scene = Boom()
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        scene.draw(camera.getViewMatrix(), projectionMatrix)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 100f)
    }
}