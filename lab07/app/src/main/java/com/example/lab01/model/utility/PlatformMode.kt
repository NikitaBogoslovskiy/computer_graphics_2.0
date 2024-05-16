package com.example.lab01.model.utility

import androidx.databinding.Observable
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

enum class PlatformModeEnum(val value: Int) {
    STATIC(0),
    CUBE_SELF_ROTATION(1),
    PLATFORM_SELF_ROTATION(2),
    PLATFORM_ROTATION(3);

    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
    }
}

class PlatformMode : ViewModel() {
    private var minModeInt = PlatformModeEnum.entries.minOfOrNull { it.value } ?: 0
    private var maxModeInt = PlatformModeEnum.entries.maxOfOrNull { it.value } ?: 0
    private var modeInt = ObservableInt(minModeInt)

    fun get() = PlatformModeEnum.fromInt(modeInt.get())

    fun next() {
        modeInt.set((modeInt.get() + 1).coerceIn(minModeInt, maxModeInt))
    }

    fun prev() {
        modeInt.set((modeInt.get() - 1).coerceIn(minModeInt, maxModeInt))
    }

    fun addCallback(callback: () -> Unit) {
        modeInt.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                callback.invoke()
            }
        })
    }
}