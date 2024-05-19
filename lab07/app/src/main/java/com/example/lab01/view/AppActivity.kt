package com.example.lab01.view

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.databinding.AppActivityBinding
import com.example.lab01.model.light.PointLight
import android.view.View.GONE
import android.widget.Switch
import com.example.lab01.model.light.LightManager
import kotlinx.coroutines.withContext

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Dependencies.activity = this
        Dependencies.context = applicationContext
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity)
        Dependencies.lightManager = LightManager()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Dependencies.platformMode.next()
        }
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Dependencies.platformMode.prev()
        }
        return true
    }
}