package com.example.lab01.view

import android.content.pm.ActivityInfo
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity

class AppActivity : ComponentActivity() {
    private lateinit var gLView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        gLView = SurfaceView(this)
        setContentView(gLView)
    }

    override fun onPause() {
        super.onPause()
        gLView.stopSensors()
        gLView.onPause()
    }

    override fun onResume() {
        super.onResume()
        gLView.onResume()
        gLView.startSensors()
    }
}