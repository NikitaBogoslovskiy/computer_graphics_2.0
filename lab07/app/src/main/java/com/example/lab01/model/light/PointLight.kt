package com.example.lab01.model.light

import android.view.View
import android.widget.SeekBar
import android.widget.Switch
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableInt
import com.example.lab01.Dependencies
import com.example.lab01.R

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
    var diffuseLevel = ObservableInt(70)
    var specularLevel = ObservableInt(100)
    var k0Level = ObservableInt(50)
    var k1Level = ObservableInt(0)
    var k2Level = ObservableInt(0)
    var texture1Level = ObservableInt(0)
    var texture2Level = ObservableInt(100)
    var color = floatArrayOf(1f, 1f, 1f, 1f)
    var position = floatArrayOf(0f, 0f, 0f)

    private val lightPanel: ConstraintLayout = Dependencies.activity.findViewById(R.id.lightPanel)
    private val settingsPanel: ConstraintLayout = Dependencies.activity.findViewById(R.id.settingsPanel)
    private val lightSwitch: Switch = Dependencies.activity.findViewById(R.id.lightSwitch)

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

        val k0SeekBar = Dependencies.activity.findViewById<SeekBar>(R.id.k0SeekBar)
        k0SeekBar.progress = k0Level.get()
        k0SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                k0Level.set(progress)
            }
        })

        val k1SeekBar = Dependencies.activity.findViewById<SeekBar>(R.id.k1SeekBar)
        k1SeekBar.progress = k1Level.get()
        k1SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                k1Level.set(progress)
            }
        })

        val k2SeekBar = Dependencies.activity.findViewById<SeekBar>(R.id.k2SeekBar)
        k2SeekBar.progress = k2Level.get()
        k2SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                k2Level.set(progress)
            }
        })

        val texture1SeekBar = Dependencies.activity.findViewById<SeekBar>(R.id.texture1SeekBar)
        texture1SeekBar.progress = texture1Level.get()
        texture1SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                texture1Level.set(progress)
            }
        })

        val texture2SeekBar = Dependencies.activity.findViewById<SeekBar>(R.id.texture2SeekBar)
        texture2SeekBar.progress = texture2Level.get()
        texture2SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                texture2Level.set(progress)
            }
        })

        enableLight()
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

        val showMoreToggle = Dependencies.activity.findViewById<ToggleButton>(R.id.showMoreToggle)
        showMoreToggle.isChecked = true
        lightPanel.translationX = -lightPanel.width.toFloat()
        settingsPanel.translationX = -settingsPanel.width.toFloat()
        lightSwitch.translationX = -lightSwitch.width.toFloat()
        hideSettings()
        showMoreToggle.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> openSettings()
                false -> hideSettings()
            }
        }
    }

    private fun openSettings() {
        lightPanel.visibility = View.VISIBLE
        settingsPanel.visibility = View.VISIBLE
        lightSwitch.visibility = View.VISIBLE
        lightPanel.animate().translationX(0f)
        settingsPanel.animate().translationX(0f)
        lightSwitch.animate().translationX(0f)
    }

    private fun hideSettings() {
        lightPanel.visibility = View.GONE
        settingsPanel.visibility = View.GONE
        lightSwitch.visibility = View.GONE
        lightPanel.animate().translationX(-lightPanel.width.toFloat())
        settingsPanel.animate().translationX(-settingsPanel.width.toFloat())
        lightSwitch.animate().translationX(-lightSwitch.width.toFloat())
    }

    private fun enableLight() {
        active = true
    }

    private fun disableLight() {
        active = false
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

    fun getAmbientValue() = ambientLevel.get() / 100f

    fun getDiffuseValue() = diffuseLevel.get() / 100f

    fun getSpecularValue() = specularLevel.get() / 100f

    fun getK0Value() = k0Level.get() / 100f

    fun getK1Value() = k1Level.get() / 100f

    fun getK2Value() = k2Level.get() / 100f

    fun getTexture1Intensity() = texture1Level.get() / 100f
    fun getTexture2Intensity() = texture2Level.get() / 100f
}