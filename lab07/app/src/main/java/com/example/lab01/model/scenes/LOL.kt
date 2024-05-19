package com.example.lab01.model.scenes

import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.model.game.GameObject
import com.example.lab01.model.game.Hero
import com.example.lab01.model.light.AmbientLight
import com.example.lab01.model.light.PointLight
import com.example.lab01.model.light.TorchLight
import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.InstancedMesh
import com.example.lab01.model.shapes.Mesh
import com.example.lab01.model.shapes.Plane
import com.example.lab01.model.shapes.Skybox
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addScale
import com.example.lab01.utils.addTranslation
import com.example.lab01.utils.radians
import kotlin.math.cos

class LOL : Scene {
    private var skybox: Skybox
    private lateinit var cube: Hero

    private lateinit var plant: Mesh
    private lateinit var cat: Mesh
    private lateinit var grass: Plane
    private var minX = -3.7f
    private var maxX = 3.65f

    init {
        Dependencies.lightManager.add(AmbientLight())
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(-35f, 15f, -35f)))
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(35f, 15f, -35f)))
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(-35f, 15f, 35f)))
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(35f, 15f, 35f)))
        Dependencies.lightManager.add(TorchLight(
            position = floatArrayOf(0f, 10f, 0f),
            direction = floatArrayOf(0f, -1f, 0f)
        ))

        skybox = Skybox(
            sideLength = 1000f,
            textureResourceIds = listOf(
                R.drawable.right,
                R.drawable.left,
                R.drawable.top,
                R.drawable.bottom,
                R.drawable.front,
                R.drawable.back
            )
        )
        cube = Hero(
            model = Cube(textureResourceId = R.drawable.ice_texture),
            position = Vector(0f, 0.75f, 0f)
        )
        Dependencies.gameInputManager.setLeftSideClickListener {
            cube.isMoving = it
        }
        Dependencies.gameInputManager.setRightSideClickListener {
            cube.rotateAroundY(it)
        }
        grass = Plane(
            sideLength = 100f,
            textureResourceId = R.drawable.grass_texture
        )
        /*
        plant = Mesh(
            modelFileId = R.raw.plant,
            textureResourceId = R.drawable.plant_texture
        )*/
/*
        cat = Mesh(
            modelFileId = R.raw.cat,
            textureResourceId = R.drawable.cat_texture
        )
        cat.pipeline.addUnique(
            Vector(0.1f, 0.1f, 0.1f),
            function = ::addScale
        )*/
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        cube.doActions()
        skybox.draw(Dependencies.camera.getViewMatrix(), projection)
        /*        grass.draw(view, projection)
                plant.draw(view, projection)*/
        grass.draw(Dependencies.camera.getViewMatrix(), projection)
        cube.draw(view, projection)
    }
}