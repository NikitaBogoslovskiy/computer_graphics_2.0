package com.example.lab01.model.light

import android.widget.Switch
import androidx.databinding.ObservableInt
import com.example.lab01.Dependencies
import com.example.lab01.R

data class AmbientLight(var level: Float = 0.2f,
                        var color: FloatArray = floatArrayOf(1f, 1f, 1f, 1f)) : Light() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AmbientLight

        if (level != other.level) return false
        if (!color.contentEquals(other.color)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = level.hashCode()
        result = 31 * result + color.contentHashCode()
        return result
    }
}