package com.example.lab01.model.light

import android.widget.SeekBar
import androidx.databinding.ObservableInt
import com.example.lab01.Dependencies
import com.example.lab01.R

enum class PointLightMode {
    LINEAR, QUADRATIC
}

class PointLight {
    var ambientLevel = ObservableInt(30)
    var intensityLevel = ObservableInt(100)
    var color = floatArrayOf(1f, 1f, 1f, 1f)
    var position = floatArrayOf(0f, 0f, 0f)
    private var mode = PointLightMode.LINEAR

    init {
        val intensitySeekBar = Dependencies.activity.findViewById<SeekBar>(R.id.intensitySeekBar)
        intensitySeekBar.progress = intensityLevel.get()
        intensitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                intensityLevel.set(progress)
            }
        })

        val ambientSeekBar = Dependencies.activity.findViewById<SeekBar>(R.id.ambientSeekBar)
        ambientSeekBar.progress = ambientLevel.get()
        ambientSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ambientLevel.set(progress)
            }
        })
    }

    fun getAmbientValue() = ambientLevel.get() / 100f

    fun getIntensityValue() = when(mode) {
        PointLightMode.LINEAR -> intensityLevel.get() / 100f
        PointLightMode.QUADRATIC -> intensityLevel.get() * intensityLevel.get() / 10000f
    }
}