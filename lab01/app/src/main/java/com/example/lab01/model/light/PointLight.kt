package com.example.lab01.model.light

import android.widget.SeekBar
import androidx.databinding.ObservableInt
import com.example.lab01.Dependencies
import com.example.lab01.R

enum class PointLightMode {
    LINEAR, QUADRATIC
}

class PointLight {
    var ambientLevel = ObservableInt(10)
    var diffuseLevel = ObservableInt(100)
    var specularLevel = ObservableInt(100)
    var color = floatArrayOf(1f, 1f, 1f, 1f)
    var position = floatArrayOf(0f, 0f, 0f)
    private var mode = PointLightMode.LINEAR

    init {
        val ambientSeekBar = Dependencies.activity.findViewById<SeekBar>(R.id.ambientSeekBar)
        ambientSeekBar.progress = ambientLevel.get()
        ambientSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ambientLevel.set(progress)
            }
        })

        val diffuseSeekBar = Dependencies.activity.findViewById<SeekBar>(R.id.diffuseSeekBar)
        diffuseSeekBar.progress = diffuseLevel.get()
        diffuseSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                diffuseLevel.set(progress)
            }
        })

        val specularSeekBar = Dependencies.activity.findViewById<SeekBar>(R.id.specularSeekBar)
        specularSeekBar.progress = specularLevel.get()
        specularSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                specularLevel.set(progress)
            }
        })
    }

    fun getAmbientValue() = when(mode) {
        PointLightMode.LINEAR -> ambientLevel.get() / 100f
        PointLightMode.QUADRATIC -> ambientLevel.get() * ambientLevel.get() / 10000f
    }

    fun getDiffuseValue() = when(mode) {
        PointLightMode.LINEAR -> diffuseLevel.get() / 100f
        PointLightMode.QUADRATIC -> diffuseLevel.get() * diffuseLevel.get() / 10000f
    }

    fun getSpecularValue() = when(mode) {
        PointLightMode.LINEAR -> specularLevel.get() / 100f
        PointLightMode.QUADRATIC -> specularLevel.get() * specularLevel.get() / 10000f
    }
}