package com.example.lab01

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lab01.model.light.PointLight
import com.example.lab01.model.utility.PlatformMode
import com.example.lab01.utils.Camera
import com.example.lab01.utils.GameInputManager
import com.example.lab01.utils.TextureLoader

@SuppressLint("StaticFieldLeak")
object Dependencies {
    var platformMode = PlatformMode()
    lateinit var activity: ComponentActivity
    lateinit var pointLight: PointLight
    lateinit var context: Context
    lateinit var gameInputManager: GameInputManager
    val camera = Camera()
    val textureLoader = TextureLoader()
}