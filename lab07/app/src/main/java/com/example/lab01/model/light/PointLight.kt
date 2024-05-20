package com.example.lab01.model.light

import android.view.View
import android.widget.SeekBar
import android.widget.Switch
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableInt
import com.example.lab01.Dependencies
import com.example.lab01.R

data class PointLight(var ambient: Float = 0.1f,
                      var diffuse: Float = 0.7f,
                      var specular: Float = 0.000001f,
                      var k0: Float = 1f,
                      var k1: Float = 0.02f,
                      var k2: Float = 0f,
                      var color: FloatArray = floatArrayOf(1f, 1f, 1f, 1f),
                      var position: FloatArray = floatArrayOf(0f, 0f, 0f)) : Light()