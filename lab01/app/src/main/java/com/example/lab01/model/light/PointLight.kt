package com.example.lab01.model.light

import android.view.View
import android.widget.SeekBar
import android.widget.Switch
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableInt
import com.example.lab01.Dependencies
import com.example.lab01.R

enum class PointLightMode {
    LINEAR, QUADRATIC
}

enum class LightShading {
    GOURAUD, PHONG
}

enum class LightModel(private var value: Int) {
    LAMBERT(0), PHONG(1);

    fun toInt() = value
}

class PointLight {
    var active = false
    var shading = LightShading.PHONG
    var model = LightModel.PHONG
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

        val lightSwitch = Dependencies.activity.findViewById<Switch>(R.id.lightSwitch)
        disableLight()
        lightSwitch.isChecked = active
        lightSwitch.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> enableLight()
                false -> disableLight()
            }
        }

        val modelToggle = Dependencies.activity.findViewById<ToggleButton>(R.id.modelToggle)
        setPhongModel()
        modelToggle.isChecked = model == LightModel.PHONG
        modelToggle.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> setPhongModel()
                false -> setLambertModel()
            }
        }

        val shadingToggle = Dependencies.activity.findViewById<ToggleButton>(R.id.shadingToggle)
        setPhongShading()
        shadingToggle.isChecked = shading == LightShading.PHONG
        shadingToggle.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> setPhongShading()
                false -> setGouraudShading()
            }
        }
    }

    private fun enableLight() {
        active = true
        Dependencies.activity.findViewById<ConstraintLayout>(R.id.settingsPanel).visibility = View.VISIBLE
        Dependencies.activity.findViewById<ConstraintLayout>(R.id.lightPanel).visibility = View.VISIBLE
/*        Dependencies.activity.findViewById<ToggleButton>(R.id.modelToggle).visibility = View.VISIBLE
        Dependencies.activity.findViewById<ToggleButton>(R.id.shadingToggle).visibility = View.VISIBLE*/
    }

    private fun disableLight() {
        active = false
        Dependencies.activity.findViewById<ConstraintLayout>(R.id.settingsPanel).visibility = View.GONE
        Dependencies.activity.findViewById<ConstraintLayout>(R.id.lightPanel).visibility = View.GONE
/*        Dependencies.activity.findViewById<ToggleButton>(R.id.modelToggle).visibility = View.GONE
        Dependencies.activity.findViewById<ToggleButton>(R.id.shadingToggle).visibility = View.GONE*/
    }

    private fun setLambertModel() {
        model = LightModel.LAMBERT
        Dependencies.activity.findViewById<SeekBar>(R.id.ambientSeekBar).isEnabled = false
        Dependencies.activity.findViewById<SeekBar>(R.id.specularSeekBar).isEnabled = false
    }

    private fun setPhongModel() {
        model = LightModel.PHONG
        Dependencies.activity.findViewById<SeekBar>(R.id.ambientSeekBar).isEnabled = true
        Dependencies.activity.findViewById<SeekBar>(R.id.specularSeekBar).isEnabled = true
    }

    private fun setGouraudShading() {
        shading = LightShading.GOURAUD
    }

    private fun setPhongShading() {
        shading = LightShading.PHONG
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