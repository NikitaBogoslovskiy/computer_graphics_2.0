package com.example.lab01.model.scenes

import com.example.lab01.Dependencies
import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.utility.PlatformModeEnum
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addRotation
import com.example.lab01.utils.addTranslation
import java.util.concurrent.atomic.AtomicBoolean

class Platform : Scene {
    private var redCube = Cube(1.5f, floatArrayOf(1f, 0f, 0f, 1f))
    private var greenCube = Cube(1.5f, floatArrayOf(0f, 1f, 0f, 1f))
    private var blueCube = Cube(1.5f, floatArrayOf(0f, 0f, 1f, 1f))
    private var yellowCube = Cube(1.5f, floatArrayOf(1f, 1f, 0f, 1f))
    private var needSwitchMode = AtomicBoolean()

    init {
        Dependencies.platformMode.addCallback { needSwitchMode.set(true) }
        needSwitchMode.set(true)
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
