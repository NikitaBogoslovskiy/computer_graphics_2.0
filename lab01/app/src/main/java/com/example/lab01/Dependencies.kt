package com.example.lab01

import android.view.View
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lab01.model.light.PointLight
import com.example.lab01.model.utility.PlatformMode
import com.example.lab01.utils.Camera

object Dependencies {
    var platformMode = PlatformMode()
    lateinit var activity: ComponentActivity
    var pointLight: PointLight? = null
    val camera = Camera()

    fun initPointLight(newLight: PointLight) {
        pointLight = newLight
        activity.findViewById<ConstraintLayout>(R.id.lightPanel).visibility = View.VISIBLE
    }
}