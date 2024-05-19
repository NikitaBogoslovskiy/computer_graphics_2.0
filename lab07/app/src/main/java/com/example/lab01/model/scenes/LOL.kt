package com.example.lab01.model.scenes

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lab01.Dependencies
import com.example.lab01.Dependencies.context
import com.example.lab01.R
import com.example.lab01.model.game.Bonus
import com.example.lab01.model.game.Enemy
import com.example.lab01.model.game.GameObject
import com.example.lab01.model.game.Hero
import com.example.lab01.model.game.Obstacle
import com.example.lab01.model.light.AmbientLight
import com.example.lab01.model.light.PointLight
import com.example.lab01.model.light.TorchLight
import com.example.lab01.model.shapes.Cube
import com.example.lab01.model.shapes.Plane
import com.example.lab01.model.shapes.Skybox
import com.example.lab01.utils.Vector
import kotlin.random.Random


data class SceneBoundaries(var minX: Float, var minZ: Float, var maxX: Float, var maxZ: Float)

enum class GameState {
    NOT_STARTED, WON, LOST, IN_PROCESS
}

class LOL : Scene {
    private var gameIsActive = false
    private lateinit var skybox: Skybox
    private lateinit var grass: Plane
    private lateinit var plant: Hero
    private lateinit var cat: Enemy
    private lateinit var coin: Bonus
    private var obstacles = emptyList<Obstacle>().toMutableList()
    private var sceneBoundaries = SceneBoundaries(
        minX = -49f, minZ = -49f, maxX = 49f, maxZ = 49f
    )
    private lateinit var possibleX: MutableList<Int>
    private lateinit var possibleZ: MutableList<Int>

    private val lightPanel: ConstraintLayout = Dependencies.activity.findViewById(R.id.lightPanel)
    private val messageBox: TextView = Dependencies.activity.findViewById(R.id.messageBox)
    private val startButton: Button = Dependencies.activity.findViewById(R.id.gameStartButton)

    init {
        startButton.setOnClickListener {
            reset()
            setup()
            messageBox.visibility = View.GONE
            startButton.visibility = View.GONE
            lightPanel.visibility = View.VISIBLE
            gameIsActive = true
        }
    }

    private fun endGame() {
        lightPanel.visibility = View.GONE
        messageBox.visibility = View.VISIBLE
        startButton.visibility = View.VISIBLE
        gameIsActive = false
    }

    fun load() {
        loadObjects()
        loadLights()
    }

    private fun loadObjects() {
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

        grass = Plane(
            sideLength = 100f,
            textureResourceId = R.drawable.grass_texture
        )

        cat = Enemy(
            model = Cube(textureResourceId = R.drawable.wood_texture)
        )

        coin = Bonus(
            model = Cube(textureResourceId = R.drawable.sky_texture)
        )
        coin.activateCallback = {
            val startPosition = coin.startPosition.copy()
            assignRandomPosition(coin)
            possibleX.add(startPosition.x.toInt())
            possibleZ.add(startPosition.z.toInt())
        }

        obstacles.addAll(listOf(
            Obstacle(
                model = Cube(textureResourceId = R.drawable.ground_texture)
            ),
            Obstacle(
                model = Cube(textureResourceId = R.drawable.ground_texture)
            ),
            Obstacle(
                model = Cube(textureResourceId = R.drawable.ground_texture)
            ),
            Obstacle(
                model = Cube(textureResourceId = R.drawable.ground_texture)
            ),
            Obstacle(
                model = Cube(textureResourceId = R.drawable.ground_texture)
            )
        ))

        plant = Hero(
            model = Cube(textureResourceId = R.drawable.ice_texture)
        )
        Dependencies.gameInputManager.setLeftSideClickListener {
            plant.isMoving = it
        }
        Dependencies.gameInputManager.setRightSideClickListener {
            plant.rotateAroundY(it)
        }
        plant.addOtherObjects(cat, coin, *obstacles.toTypedArray())
        cat.hero = plant
        plant.winningActionCallback = {
            Handler(Looper.getMainLooper()).post {
                messageBox.text = context.getString(R.string.you_won)
                endGame()
            }
        }
        cat.failingActionCallback = {
            Handler(Looper.getMainLooper()).post {
                messageBox.text = context.getString(R.string.you_lost)
                endGame()
            }
        }
    }

    private fun loadLights() {
        val torchLight = TorchLight(
            position = floatArrayOf(0f, 0.75f, 0f),
            direction = floatArrayOf(1f, 0f, 1f)
        )
        Dependencies.lightManager.add(AmbientLight())
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(-35f, 15f, -35f)))
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(35f, 15f, -35f)))
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(-35f, 15f, 35f)))
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(35f, 15f, 35f)))
        Dependencies.lightManager.add(torchLight)
        plant.torch = torchLight
    }

    private fun assignRandomPosition(obj: GameObject) {
        val xIdx = Random.nextInt(possibleX.size)
        val zIdx = Random.nextInt(possibleZ.size)
        obj.position = Vector(possibleX[xIdx].toFloat(), 0.75f, possibleZ[zIdx].toFloat())
        obj.init()
        possibleX.removeAt(xIdx)
        possibleZ.removeAt(zIdx)
    }

    private fun reset() {
        possibleX = (sceneBoundaries.minX.toInt()..sceneBoundaries.maxX.toInt() step 4).toMutableList()
        possibleZ = (sceneBoundaries.minZ.toInt()..sceneBoundaries.maxZ.toInt() step 4).toMutableList()
        for(obstacle in obstacles)
            obstacle.reset()
        coin.reset()
        plant.reset()
        cat.reset()
    }

    private fun setup() {
        for(obstacle in obstacles)
            assignRandomPosition(obstacle)
        assignRandomPosition(coin)
        assignRandomPosition(plant)
        assignRandomPosition(cat)
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        if (!gameIsActive)
            return

        plant.doActions()
        val newView = Dependencies.camera.getViewMatrix()
        skybox.draw(newView, projection)
        grass.draw(newView, projection)
        for(obstacle in obstacles)
            obstacle.draw(newView, projection)
        coin.draw(newView, projection)
        cat.draw(newView, projection)
        plant.draw(newView, projection)
    }
}