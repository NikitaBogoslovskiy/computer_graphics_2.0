package com.example.lab01.model.shapes

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.model.shaders.BASE_FRAGMENT_SHADER_SPRITE
import com.example.lab01.model.shaders.BASE_VERTEX_SHADER_SPRITE
import com.example.lab01.model.utility.loadShader
import com.example.lab01.utils.Pipeline
import com.example.lab01.utils.TextureData
import com.example.lab01.utils.Vector
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

val spriteColor = floatArrayOf(1f, 1f, 1f, 1f)

class Sprite(sideLength: Float = 0.2f,
             var color: FloatArray = spriteColor,
             private var textureId: Int = R.drawable.default_texture,
             private var textureData: TextureData? = null) : Shape {

    //Model pipeline
    var pipeline = Pipeline()
    private var modelMatrix = FloatArray(16)

    //Raw data
    private var halfSide = sideLength / 2
    private val data = floatArrayOf(
        -halfSide, -halfSide, 0f, 1f,
        halfSide, -halfSide, 0f, 0f,
        halfSide, halfSide, 1f, 0f,
        halfSide, halfSide, 1f, 0f,
        -halfSide, halfSide, 1f, 1f,
        -halfSide, -halfSide, 0f, 1f
    )

    //Processed data
    private val coordinatesPerVertex = 2
    private val coordinatesPerTexture = 2
    private val coordinatesPerPoint = coordinatesPerVertex + coordinatesPerTexture
    private val pointsCount = data.size / coordinatesPerPoint
    private val vertexStride: Int = coordinatesPerVertex * Float.SIZE_BYTES
    private val textureStride: Int = coordinatesPerTexture * Float.SIZE_BYTES
    private lateinit var vertices: FloatArray
    private lateinit var textures: FloatArray
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer

    fun getTextureData() = textureData

    private fun processData() {
        val vertexList = emptyList<Float>().toMutableList()
        val textureList = emptyList<Float>().toMutableList()
        for (pointIdx in 0 until pointsCount) {
            vertexList.add(data[pointIdx * coordinatesPerPoint + 0])
            vertexList.add(data[pointIdx * coordinatesPerPoint + 1])
            textureList.add(data[pointIdx * coordinatesPerPoint + 2])
            textureList.add(data[pointIdx * coordinatesPerPoint + 3])
        }
        vertices = vertexList.toFloatArray()
        textures = textureList.toFloatArray()
        vertexBuffer =
            ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(vertices)
                    position(0)
                }
            }
        textureBuffer =
            ByteBuffer.allocateDirect(textures.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(textures)
                    position(0)
                }
            }
    }

    //Shaders
    private var program: Int

    init {
        if (textureData == null)
            textureData = Dependencies.textureLoader.loadTexture(textureId)
        processData()
        Matrix.setIdentityM(modelMatrix, 0)
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, loadShader(GLES20.GL_VERTEX_SHADER, BASE_VERTEX_SHADER_SPRITE))
            GLES20.glAttachShader(
                it,
                loadShader(GLES20.GL_FRAGMENT_SHADER, BASE_FRAGMENT_SHADER_SPRITE)
            )
            GLES20.glLinkProgram(it)
        }
    }

    private fun setBaseParams(program: Int, view: FloatArray, projection: FloatArray) {
        val posLoc = GLES20.glGetAttribLocation(program, "position")
        val colLoc = GLES20.glGetUniformLocation(program, "color")
        val modelLoc = GLES20.glGetUniformLocation(program, "model")
        val viewLoc = GLES20.glGetUniformLocation(program, "view")
        val projectionLoc = GLES20.glGetUniformLocation(program, "projection")
        GLES20.glUniformMatrix4fv(modelLoc, 1, false, modelMatrix, 0)
        GLES20.glUniformMatrix4fv(viewLoc, 1, false, view, 0)
        GLES20.glUniformMatrix4fv(projectionLoc, 1, false, projection, 0)
        GLES20.glVertexAttribPointer(
            posLoc,
            coordinatesPerVertex,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        GLES20.glUniform4fv(colLoc, 1, color, 0)
        GLES20.glEnableVertexAttribArray(posLoc)
    }

    private fun setTexturesParams(program: Int) {
        val texLoc = GLES20.glGetAttribLocation(program, "a_texture")
        val textureUnitLoc = GLES20.glGetUniformLocation(program, "texture_unit")
        GLES20.glUniform1i(textureUnitLoc, textureData?.textureNumber ?: 0)
        GLES20.glVertexAttribPointer(
            texLoc,
            coordinatesPerTexture,
            GLES20.GL_FLOAT,
            false,
            textureStride,
            textureBuffer
        )
        GLES20.glEnableVertexAttribArray(texLoc)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (textureData?.textureNumber ?: 0))
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, (textureData?.textureId ?: 0))
    }

    private fun setLightParams(program: Int) {
    }

    private fun getCurrentProgram() = program

    override fun draw(view: FloatArray, projection: FloatArray) {
        val program = getCurrentProgram()
        GLES20.glUseProgram(program)
        pipeline.execute(modelMatrix)
        setBaseParams(program, view, projection)
        setTexturesParams(program)
        setLightParams(program)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, pointsCount)
    }
}