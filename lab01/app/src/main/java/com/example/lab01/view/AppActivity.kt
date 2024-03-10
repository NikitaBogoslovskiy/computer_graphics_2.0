package com.example.lab01.view

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity

class AppActivity : ComponentActivity() {
    private lateinit var gLView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gLView = SurfaceView(this)
        setContentView(gLView)
    }
}