package com.example.lab01.model.scenes

import android.app.Activity
import android.app.Application
import android.widget.SeekBar
import android.widget.Toast
import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.model.light.PointLight
import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.utility.PlatformModeEnum
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addTranslation
import java.util.concurrent.atomic.AtomicBoolean


class Platform : Scene {
    private var redCube: Cube
    private var greenCube: Cube
    private var blueCube: Cube
    private var yellowCube: Cube
    private var needSwitchMode = AtomicBoolean()

    init {
        Dependencies.platformMode.addCallback { needSwitchMode.set(true) }
        needSwitchMode.set(true)
        Dependencies.initPointLight(PointLight())

        redCube = Cube(1.5f, floatArrayOf(1f, 0f, 0f, 1f))
        greenCube = Cube(1.5f, floatArrayOf(0f, 1f, 0f, 1f))
        blueCube = Cube(1.5f, floatArrayOf(0f, 0f, 1f, 1f))
        yellowCube = Cube(1.5f, floatArrayOf(1f, 1f, 0f, 1f))
/*        val bar1 = Dependencies.activity.findViewById<SeekBar>(R.id.seekBar1)
        bar1.progress = (redCube.color[0] * 100).toInt()
        bar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                redCube.color[0] = progress / 100f
            }
        })
        val bar2 = Dependencies.activity.findViewById<SeekBar>(R.id.seekBar2)
        bar2.progress = (redCube.color[1] * 100).toInt()
        bar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                redCube.color[1] = progress / 100f
            }
        })
        val bar3 = Dependencies.activity.findViewById<SeekBar>(R.id.seekBar3)
        bar3.progress = (redCube.color[2] * 100).toInt()
        bar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                redCube.color[2] = progress / 100f
            }
        })*/
    }

    private fun switchMode() {
        reset()
        when (Dependencies.platformMode.get()) {
            PlatformModeEnum.STATIC -> {
                setStaticPipeline()
            }
            PlatformModeEnum.CUBE_SELF_ROTATION -> {
                setCubeSelfRotationPipeline()
            }
            PlatformModeEnum.PLATFORM_SELF_ROTATION -> {
                setPlatformSelfRotationPipeline()
            }
            PlatformModeEnum.PLATFORM_ROTATION -> {
                setPlatformRotationPipeline()
            }
        }
    }

    private fun reset() {
        redCube.pipeline.reset()
        greenCube.pipeline.reset()
        blueCube.pipeline.reset()
        yellowCube.pipeline.reset()
    }

    private fun setStaticPipeline() {
        redCube.pipeline.addUnique(Vector(3f, 0f, 0f),
            function = ::addTranslation)
        greenCube.pipeline.addUnique(Vector(3f, 0f, -1.5f),
            function = ::addTranslation)
        blueCube.pipeline.addUnique(Vector(3f, 0f, 1.5f),
            function = ::addTranslation)
        yellowCube.pipeline.addUnique(Vector(3f, 1.5f, 0f),
            function = ::addTranslation)
    }

    private fun setCubeSelfRotationPipeline() {
        setStaticPipeline()

        redCube.pipeline.addRepeatable(Vector(-3f, 0f, 0f),
            function = ::addTranslation)
        greenCube.pipeline.addRepeatable(Vector(-3f, 0f, 1.5f),
            function = ::addTranslation)
        blueCube.pipeline.addRepeatable(Vector(-3f, 0f, -1.5f),
            function = ::addTranslation)
        yellowCube.pipeline.addRepeatable(Vector(-3f, -1.5f, 0f),
            function = ::addTranslation)

        redCube.pipeline.addRepeatable(0.5f, Vector(0f, 1f, 0f),
            function = ::addRotation)
        greenCube.pipeline.addRepeatable(1f, Vector(0f, 1f, 0f),
            function = ::addRotation)
        blueCube.pipeline.addRepeatable(1.5f, Vector(0f, 1f, 0f),
            function = ::addRotation)
        yellowCube.pipeline.addRepeatable(2f, Vector(0f, 1f, 0f),
            function = ::addRotation)

        redCube.pipeline.addRepeatable(Vector(3f, 0f, 0f),
            function = ::addTranslation)
        greenCube.pipeline.addRepeatable(Vector(3f, 0f, -1.5f),
            function = ::addTranslation)
        blueCube.pipeline.addRepeatable(Vector(3f, 0f, 1.5f),
            function = ::addTranslation)
        yellowCube.pipeline.addRepeatable(Vector(3f, 1.5f, 0f),
            function = ::addTranslation)
    }

    private fun setPlatformSelfRotationPipeline() {
        setStaticPipeline()

        redCube.pipeline.addRepeatable(Vector(-3f, 0f, 0f),
            function = ::addTranslation)
        greenCube.pipeline.addRepeatable(Vector(-3f, 0f, 0f),
            function = ::addTranslation)
        blueCube.pipeline.addRepeatable(Vector(-3f, 0f, 0f),
            function = ::addTranslation)
        yellowCube.pipeline.addRepeatable(Vector(-3f, 0f, 0f),
            function = ::addTranslation)

        redCube.pipeline.addRepeatable(1f, Vector(0f, 1f, 0f),
            function = ::addRotation)
        greenCube.pipeline.addRepeatable(1f, Vector(0f, 1f, 0f),
            function = ::addRotation)
        blueCube.pipeline.addRepeatable(1f, Vector(0f, 1f, 0f),
            function = ::addRotation)
        yellowCube.pipeline.addRepeatable(1f, Vector(0f, 1f, 0f),
            function = ::addRotation)

        redCube.pipeline.addRepeatable(Vector(3f, 0f, 0f),
            function = ::addTranslation)
        greenCube.pipeline.addRepeatable(Vector(3f, 0f, 0f),
            function = ::addTranslation)
        blueCube.pipeline.addRepeatable(Vector(3f, 0f, 0f),
            function = ::addTranslation)
        yellowCube.pipeline.addRepeatable(Vector(3f, 0f, 0f),
            function = ::addTranslation)
    }

    private fun setPlatformRotationPipeline() {
        setStaticPipeline()

        redCube.pipeline.addRepeatable(1f, Vector(0f, 1f, 0f),
            function = ::addRotation)
        greenCube.pipeline.addRepeatable(1f, Vector(0f, 1f, 0f),
            function = ::addRotation)
        blueCube.pipeline.addRepeatable(1f, Vector(0f, 1f, 0f),
            function = ::addRotation)
        yellowCube.pipeline.addRepeatable(1f, Vector(0f, 1f, 0f),
            function = ::addRotation)
    }

    override fun draw(vPMatrix: FloatArray) {
        if (needSwitchMode.get()) {
            switchMode()
            needSwitchMode.set(false)
        }

        redCube.draw(vPMatrix)
        greenCube.draw(vPMatrix)
        blueCube.draw(vPMatrix)
        yellowCube.draw(vPMatrix)
    }
}
