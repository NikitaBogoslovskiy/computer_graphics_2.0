package com.example.lab01.view

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.example.lab01.utils.Quaternion
import com.example.lab01.viewmodel.Renderer


class SurfaceView(context: Context) : GLSurfaceView(context), SensorEventListener {

    private val renderer: com.example.lab01.viewmodel.Renderer
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var previousX: Float = 0f
    private var previousY: Float = 0f

    init {
        setEGLContextClientVersion(2)
        renderer = Renderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY;
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val quaternion = FloatArray(4)
            SensorManager.getQuaternionFromVector(quaternion, event.values)
            renderer.camera.updateCameraDirection(Quaternion.fromFloatArray(quaternion))
            requestRender()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun startSensors() {
        val listSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (itemSensor in listSensors) {
            when (itemSensor.type) {
                Sensor.TYPE_ROTATION_VECTOR ->
                    sensorManager.registerListener(
                        this, itemSensor,
                        SensorManager.SENSOR_DELAY_GAME
                    )
                else -> {}
            }
        }
    }

    fun stopSensors() {
        sensorManager.unregisterListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.getX(0)
        val y: Float = e.getY(0)
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                if (e.pointerCount == 1) {
                    renderer.camera.updateCameraPosition(x - previousX, y - previousY)
                    requestRender()
                } else {
                    renderer.camera.updateCameraZoom(y - previousY)
                    requestRender()
                }
            }
        }
        previousX = x
        previousY = y
        return true
    }
}