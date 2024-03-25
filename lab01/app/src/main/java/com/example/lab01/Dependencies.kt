package com.example.lab01

import android.database.Observable
import androidx.databinding.ObservableField
import com.example.lab01.model.utility.PlatformMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object Dependencies {
    var platformMode = PlatformMode()
}