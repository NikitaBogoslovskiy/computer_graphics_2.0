package com.example.lab01.model.light

import android.widget.Switch
import androidx.databinding.ObservableInt
import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.utils.radians
import kotlin.math.cos

data class TorchLight(var ambient: Float = 0.1f,
                      var diffuse: Float = 0.7f,
                      var specular: Float = 1f,
                      var k0: Float = 1f,
                      var k1: Float = 0.01f,
                      var k2: Float = 0f,
                      var color: FloatArray = floatArrayOf(1f, 1f, 1f, 1f),
                      var position: FloatArray = floatArrayOf(0f, 0f, 0f),
                      var direction: FloatArray = floatArrayOf(0f, -1f, 0f),
                      var innerCutOff: Float = cos(radians(12.5f)),
                      var outerCutOff: Float = cos(radians(17.5f))) : Light() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TorchLight

        if (ambient != other.ambient) return false
        if (diffuse != other.diffuse) return false
        if (specular != other.specular) return false
        if (k0 != other.k0) return false
        if (k1 != other.k1) return false
        if (k2 != other.k2) return false
        if (!color.contentEquals(other.color)) return false
        if (!position.contentEquals(other.position)) return false
        if (!direction.contentEquals(other.direction)) return false
        if (innerCutOff != other.innerCutOff) return false
        if (outerCutOff != other.outerCutOff) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ambient.hashCode()
        result = 31 * result + diffuse.hashCode()
        result = 31 * result + specular.hashCode()
        result = 31 * result + k0.hashCode()
        result = 31 * result + k1.hashCode()
        result = 31 * result + k2.hashCode()
        result = 31 * result + color.contentHashCode()
        result = 31 * result + position.contentHashCode()
        result = 31 * result + direction.contentHashCode()
        result = 31 * result + innerCutOff.hashCode()
        result = 31 * result + outerCutOff.hashCode()
        return result
    }
}