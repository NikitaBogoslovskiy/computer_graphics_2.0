package com.example.lab01.model.scenes

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableField
import com.example.lab01.Dependencies
import com.example.lab01.Dependencies.context
import com.example.lab01.Dependencies.musicPlayer
import com.example.lab01.Dependencies.soundPlayer
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
import com.example.lab01.model.shapes.Mesh
import com.example.lab01.model.shapes.Plane
import com.example.lab01.model.shapes.Skybox
import com.example.lab01.utils.Vector
import com.example.lab01.utils.addScale
import com.example.lab01.utils.radians
import kotlin.math.cos
import kotlin.random.Random


data class SceneBoundaries(var minX: Float, var minZ: Float, var maxX: Float, var maxZ: Float, var shiftX: Float, var shiftZ: Float)

class LOL : Scene {
    var currentBonusesNumber = ObservableField(0)
    var maxBonusesNumber = ObservableField(6)
    private var gameIsActive = false
    private lateinit var skybox: Skybox
    private lateinit var grass: Plane
    private lateinit var hero: Hero
    private lateinit var enemy: Enemy
    private lateinit var bonus: Bonus
    private var obstacles = emptyList<Obstacle>().toMutableList()
    var sceneBoundaries = SceneBoundaries(
        minX = -25f, minZ = -25f, maxX = 25f, maxZ = 25f, shiftX = 5f, shiftZ = 5f
    )
    private lateinit var torchAssignmentCallback: () -> Unit
    private lateinit var possibleX: MutableList<Int>
    private lateinit var possibleZ: MutableList<Int>

    private lateinit var lightPanel: ConstraintLayout
    private lateinit var bonusPanel: ConstraintLayout
    private lateinit var messageBox: TextView
    private lateinit var startButton: Button
    private lateinit var enemySwitch: Switch
    private var backgroundMusic = listOf(R.raw.background1, R.raw.background2, R.raw.background3)
    //private var backgroundMusic = listOf(R.raw.lost_sound)

    fun bindUI() {
        lightPanel = Dependencies.activity.findViewById(R.id.lightPanel)
        bonusPanel = Dependencies.activity.findViewById(R.id.bonusPanel)
        messageBox = Dependencies.activity.findViewById(R.id.messageBox)
        startButton = Dependencies.activity.findViewById(R.id.gameStartButton)
        enemySwitch = Dependencies.activity.findViewById(R.id.enemySwitch)
        startButton.setOnClickListener {
            reset()
            setup()
            musicPlayer.stop()
            musicPlayer = MediaPlayer.create(context, R.raw.background1)
            musicPlayer.isLooping = true
            musicPlayer.setVolume(0.8f, 0.8f)
            if (enemySwitch.isChecked) {
                musicPlayer.start()
                enemy.isActive = true
            } else {
                enemy.isActive = false
            }
            messageBox.visibility = View.GONE
            startButton.visibility = View.GONE
            lightPanel.visibility = View.VISIBLE
            bonusPanel.visibility = View.VISIBLE
            enemySwitch.visibility = View.VISIBLE
            gameIsActive = true
        }
        enemySwitch.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> {
                    enemy.isActive = true
                    musicPlayer.start()
                }
                false -> {
                    enemy.isActive = false
                    musicPlayer.pause()
                }
            }
        }
    }

    private fun endGame() {
        lightPanel.visibility = View.GONE
        bonusPanel.visibility = View.GONE
        enemySwitch.visibility = View.GONE
        messageBox.visibility = View.VISIBLE
        startButton.visibility = View.VISIBLE
        gameIsActive = false
    }

    fun load() {
        loadLights()
        loadObjects()
        torchAssignmentCallback.invoke()
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
            sideLength = 60f,
            textureResourceId = R.drawable.grass_texture
        )

        enemy = Enemy(
            model = Mesh(
                modelFileId = R.raw.bad_krosh,
                textureResourceId = R.drawable.bad_krosh
            )
        )

        bonus = Bonus(
            model = Mesh(
                modelFileId = R.raw.candy,
                textureResourceId = R.drawable.default_texture
            )
        )
        bonus.activateCallback = {
            soundPlayer = MediaPlayer.create(context, R.raw.eating_sound)
            soundPlayer.setVolume(1f, 1f)
            soundPlayer.start()
            val startPosition = bonus.startPosition.copy()
            assignRandomPosition(bonus)
            possibleX.add(startPosition.x.toInt())
            possibleZ.add(startPosition.z.toInt())
        }

        obstacles.addAll(listOf(
            Obstacle(
                model = Mesh(
                    modelFileId = R.raw.stone1,
                    textureResourceId = R.drawable.stone
                )
            ),
            Obstacle(
                model = Mesh(
                    modelFileId = R.raw.stone1,
                    textureResourceId = R.drawable.stone
                )
            ),
            Obstacle(
                model = Mesh(
                    modelFileId = R.raw.stone2,
                    textureResourceId = R.drawable.stone
                )
            ),
            Obstacle(
                model = Mesh(
                    modelFileId = R.raw.barrel,
                    textureResourceId = R.drawable.barrel
                )
            ),
            Obstacle(
                model = Mesh(
                    modelFileId = R.raw.barrel,
                    textureResourceId = R.drawable.barrel
                )
            )
        ))

        hero = Hero(
            model = Mesh(
                modelFileId = R.raw.good_krosh,
                textureResourceId = R.drawable.good_krosh
            )
        )
        Dependencies.gameInputManager.setLeftSideClickListener {
            hero.isMoving = it
        }
        Dependencies.gameInputManager.setRightSideClickListener {
            hero.rotateAroundY(it)
        }

        hero.addOtherObjects(enemy, bonus, *obstacles.toTypedArray())
        enemy.hero = hero
        hero.winningActionCallback = {
            musicPlayer.stop()
            musicPlayer = MediaPlayer.create(context, R.raw.win_sound)
            musicPlayer.setVolume(0.8f, 0.8f)
            musicPlayer.start()
            Handler(Looper.getMainLooper()).post {
                messageBox.text = context.getString(R.string.you_won)
                endGame()
            }
        }
        enemy.failingActionCallback = {
            musicPlayer.stop()
            musicPlayer = MediaPlayer.create(context, R.raw.lost_sound)
            musicPlayer.setVolume(0.8f, 0.8f)
            musicPlayer.start()
            Handler(Looper.getMainLooper()).post {
                messageBox.text = context.getString(R.string.you_lost)
                endGame()
            }
        }
    }

    private fun loadLights() {
        val heroTorchLight = TorchLight(
            position = floatArrayOf(0f, 0.75f, 0f),
            direction = floatArrayOf(1f, 0f, 1f)
        )
        val bonusTorchLight = TorchLight(
            direction = floatArrayOf(0f, -1f, 0f),
            k0 = 1f,
            ambient = 0f,
            diffuse = 0.5f,
            specular = 0f,
            innerCutOff = cos(radians(15f)),
            outerCutOff = cos(radians(25f)),
        )
        Dependencies.lightManager.add(AmbientLight())
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(-35f, 100f, -35f)))
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(35f, 100f, -35f)))
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(-35f, 100f, 35f)))
        Dependencies.lightManager.add(PointLight(position = floatArrayOf(35f, 100f, 35f)))
        Dependencies.lightManager.add(heroTorchLight)
        //Dependencies.lightManager.add(bonusTorchLight)
        torchAssignmentCallback = {
            hero.torch = heroTorchLight
            //bonus.torch = bonusTorchLight
        }
    }

    private fun assignRandomPosition(obj: GameObject, shift: Float = 0f) {
        val xIdx = Random.nextInt(possibleX.size)
        val zIdx = Random.nextInt(possibleZ.size)
        obj.position = Vector(possibleX[xIdx].toFloat(), shift, possibleZ[zIdx].toFloat())
        obj.init()
        possibleX.removeAt(xIdx)
        possibleZ.removeAt(zIdx)
    }

    private fun reset() {
        currentBonusesNumber.set(0)
        possibleX = (sceneBoundaries.minX.toInt()..sceneBoundaries.maxX.toInt() step 4).toMutableList()
        possibleZ = (sceneBoundaries.minZ.toInt()..sceneBoundaries.maxZ.toInt() step 4).toMutableList()
        for(obstacle in obstacles)
            obstacle.reset()
        bonus.reset()
        hero.reset()
        enemy.reset()
    }

    private fun setup() {
        for(obstacle in obstacles) {
            obstacle.applyScale(2f)
            assignRandomPosition(obstacle)
        }

        bonus.applyScale(0.08f)
        assignRandomPosition(bonus, 1f)

        hero.yaw = -90f
        hero.applyScale(0.003f)
        assignRandomPosition(hero)

        enemy.yaw = -90f
        enemy.applyScale(0.12f)
        assignRandomPosition(enemy)
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        if (!gameIsActive)
            return

        hero.doActions()
        val newView = Dependencies.camera.getViewMatrix()
        skybox.draw(newView, projection)
        grass.draw(newView, projection)
        for(obstacle in obstacles)
            obstacle.draw(newView, projection)
        bonus.draw(newView, projection)
        enemy.draw(newView, projection)
        hero.draw(newView, projection)
    }
}