package com.example.lab01

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.view.View
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lab01.model.light.LightManager
import com.example.lab01.model.light.PointLight
import com.example.lab01.model.scenes.LOL
import com.example.lab01.model.utility.PlatformMode
import com.example.lab01.utils.Camera
import com.example.lab01.utils.GameInputManager
import com.example.lab01.utils.TextureLoader

@SuppressLint("StaticFieldLeak")
object Dependencies {
    var platformMode = PlatformMode()
    lateinit var activity: ComponentActivity
    lateinit var lightManager: LightManager
    lateinit var context: Context
    lateinit var gameInputManager: GameInputManager
    lateinit var scene: LOL
    val camera = Camera()
    val textureLoader = TextureLoader()
    var musicPlayer = MediaPlayer()
    var soundPlayer = MediaPlayer()
}