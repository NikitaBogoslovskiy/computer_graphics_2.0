package com.example.lab01.model.light

import android.annotation.SuppressLint
import android.widget.Switch
import com.example.lab01.Dependencies
import com.example.lab01.R

data class LightData(
    val ambient: FloatArray,
    val diffuse: FloatArray,
    val specular: FloatArray,
    val k0: FloatArray,
    val k1: FloatArray,
    val k2: FloatArray,
    val lightColor: FloatArray,
    val lightPosition: FloatArray,
    val torchDirection: FloatArray,
    val torchInnerCutoff: FloatArray,
    val torchOuterCutoff: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LightData

        if (!ambient.contentEquals(other.ambient)) return false
        if (!diffuse.contentEquals(other.diffuse)) return false
        if (!specular.contentEquals(other.specular)) return false
        if (!k0.contentEquals(other.k0)) return false
        if (!k1.contentEquals(other.k1)) return false
        if (!k2.contentEquals(other.k2)) return false
        if (!lightColor.contentEquals(other.lightColor)) return false
        if (!lightPosition.contentEquals(other.lightPosition)) return false
        if (!torchDirection.contentEquals(other.torchDirection)) return false
        if (!torchInnerCutoff.contentEquals(other.torchInnerCutoff)) return false
        if (!torchOuterCutoff.contentEquals(other.torchOuterCutoff)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ambient.contentHashCode()
        result = 31 * result + diffuse.contentHashCode()
        result = 31 * result + specular.contentHashCode()
        result = 31 * result + k0.contentHashCode()
        result = 31 * result + k1.contentHashCode()
        result = 31 * result + k2.contentHashCode()
        result = 31 * result + lightColor.contentHashCode()
        result = 31 * result + lightPosition.contentHashCode()
        result = 31 * result + torchDirection.contentHashCode()
        result = 31 * result + torchInnerCutoff.contentHashCode()
        result = 31 * result + torchOuterCutoff.contentHashCode()
        return result
    }
}

@SuppressLint("UseSwitchCompatOrMaterialCode")
class LightManager {
    var pN = 0
    var tN = 0
    var aN = 0
    private var lights = emptyList<Light>().toMutableList()

    private var ambientEnabled = true
    private var pointEnabled = true
    private var torchEnabled = true


    private val ambientLightSwitch: Switch = Dependencies.activity.findViewById(R.id.ambientLightSwitch)
    private val pointLightSwitch: Switch = Dependencies.activity.findViewById(R.id.pointLightSwitch)
    private val torchLightSwitch: Switch = Dependencies.activity.findViewById(R.id.torchLightSwitch)

    init {
        ambientLightSwitch.isChecked = ambientEnabled
        ambientLightSwitch.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> enableAmbientLight()
                false -> disableAmbientLight()
            }
        }

        pointLightSwitch.isChecked = pointEnabled
        pointLightSwitch.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> enablePointLight()
                false -> disablePointLight()
            }
        }

        torchLightSwitch.isChecked = torchEnabled
        torchLightSwitch.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> enableTorchLight()
                false -> disableTorchLight()
            }
        }
    }

    private fun enableAmbientLight() {
        ambientEnabled = true
    }

    private fun disableAmbientLight() {
        ambientEnabled = false
    }

    private fun enablePointLight() {
        pointEnabled = true
    }

    private fun disablePointLight() {
        pointEnabled = false
    }

    private fun enableTorchLight() {
        torchEnabled = true
    }

    private fun disableTorchLight() {
        torchEnabled = false
    }

    fun add(light: Light) {
        when(light) {
            is AmbientLight -> aN++
            is PointLight -> pN++
            is TorchLight -> tN++
        }
        lights.add(light)
    }

    fun getLightsData(): LightData {
        val pointLights = lights.filterIsInstance<PointLight>()
        val torchLights = lights.filterIsInstance<TorchLight>()
        val ambientLights = lights.filterIsInstance<AmbientLight>()
        val pN = pointLights.size
        val tN = torchLights.size
        val aN = ambientLights.size
        val data = LightData(
            ambient = FloatArray(pN + tN + aN),
            diffuse = FloatArray(pN + tN),
            specular = FloatArray(pN + tN),
            k0 = FloatArray(pN + tN),
            k1 = FloatArray(pN + tN),
            k2 = FloatArray(pN + tN),
            lightColor = FloatArray((pN + tN + aN) * 4),
            lightPosition = FloatArray((pN + tN) * 3),
            torchDirection = FloatArray(tN * 3),
            torchInnerCutoff = FloatArray(tN),
            torchOuterCutoff = FloatArray(tN)
        )
        data.k0.fill(1f)

        var pIdx = 0
        var tIdx = 0
        var aIdx = 0
        if (pointEnabled) {
            for(light in pointLights) {
                data.ambient[pIdx] = light.ambient
                data.diffuse[pIdx] = light.diffuse
                data.specular[pIdx] = light.specular
                data.k0[pIdx] = light.k0
                data.k1[pIdx] = light.k1
                data.k2[pIdx] = light.k2
                for(localIdx in (pIdx * 4) until (pIdx * 4 + 4))
                    data.lightColor[localIdx] = light.color[localIdx % 4]
                for(localIdx in (pIdx * 3) until (pIdx * 3 + 3))
                    data.lightPosition[localIdx] = light.position[localIdx % 3]
                pIdx++
            }
        } else {
            pIdx += pN
        }

        if (torchEnabled) {
            for(light in torchLights) {
                data.ambient[pIdx] = light.ambient
                data.diffuse[pIdx] = light.diffuse
                data.specular[pIdx] = light.specular
                data.k0[pIdx] = light.k0
                data.k1[pIdx] = light.k1
                data.k2[pIdx] = light.k2
                for(localIdx in (pIdx * 4) until (pIdx * 4 + 4))
                    data.lightColor[localIdx] = light.color[localIdx % 4]
                for(localIdx in (pIdx * 3) until (pIdx * 3 + 3))
                    data.lightPosition[localIdx] = light.position[localIdx % 3]
                for(localIdx in (tIdx * 3) until (tIdx * 3 + 3))
                    data.torchDirection[localIdx] = light.direction[localIdx % 3]
                data.torchInnerCutoff[tIdx] = light.innerCutOff
                data.torchOuterCutoff[tIdx] = light.outerCutOff
                pIdx++
                tIdx++
            }
        } else {
            pIdx += tN
            tIdx += tN
        }

        if (ambientEnabled) {
            for(light in ambientLights) {
                data.ambient[pIdx] = light.level
                for(localIdx in (pIdx * 4) until (pIdx * 4 + 4))
                    data.lightColor[localIdx] = light.color[localIdx % 4]
                pIdx++
                tIdx++
                aIdx++
            }
        } else {
            pIdx += aN
            tIdx += aN
            aIdx += aN
        }

        return data
    }
}