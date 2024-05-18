package com.example.lab01.model.shapes

import android.opengl.GLES30
import android.opengl.Matrix
import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.model.light.LightShading
import com.example.lab01.model.shaders.BASE_FRAGMENT_SHADER
import com.example.lab01.model.shaders.BASE_VERTEX_SHADER
import com.example.lab01.model.shaders.GOURAUD_FRAGMENT_SHADER
import com.example.lab01.model.shaders.GOURAUD_VERTEX_SHADER
import com.example.lab01.model.shaders.PHONG_FRAGMENT_SHADER
import com.example.lab01.model.shaders.PHONG_VERTEX_SHADER
import com.example.lab01.model.shaders.SKYBOX_FRAGMENT_SHADER
import com.example.lab01.model.shaders.SKYBOX_VERTEX_SHADER
import com.example.lab01.model.utility.loadShader
import com.example.lab01.utils.Pipeline
import com.example.lab01.utils.TextureData
import com.example.lab01.utils.Vector
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Skybox(sideLength: Float = 50f, textureResourceIds: List<Int>) : Shape {

    //Model pipeline
    var pipeline = Pipeline()
    private var modelMatrix = FloatArray(16)

    //Raw data
    private var textureId: Int = -1
    private var halfSide = sideLength / 2
    private val data = floatArrayOf(
        -halfSide, -halfSide, -halfSide,  0.0f,  0.0f, -1.0f, 1f, 1f,
        halfSide, -halfSide, -halfSide,  0.0f,  0.0f, -1.0f, 0f, 1f,
        halfSide,  halfSide, -halfSide,  0.0f,  0.0f, -1.0f, 0f, 0f,
        halfSide,  halfSide, -halfSide,  0.0f,  0.0f, -1.0f, 0f, 0f,
        -halfSide,  halfSide, -halfSide,  0.0f,  0.0f, -1.0f, 1f, 0f,
        -halfSide, -halfSide, -halfSide,  0.0f,  0.0f, -1.0f, 1f, 1f,

        -halfSide, -halfSide,  halfSide,  0.0f,  0.0f, 1.0f, 0f, 1f,
        halfSide, -halfSide,  halfSide,  0.0f,  0.0f, 1.0f, 1f, 1f,
        halfSide,  halfSide,  halfSide,  0.0f,  0.0f, 1.0f, 1f, 0f,
        halfSide,  halfSide,  halfSide,  0.0f,  0.0f, 1.0f, 1f, 0f,
        -halfSide,  halfSide,  halfSide,  0.0f,  0.0f, 1.0f, 0f, 0f,
        -halfSide, -halfSide,  halfSide,  0.0f,  0.0f, 1.0f, 0f, 1f,

        -halfSide,  halfSide,  halfSide, -1.0f,  0.0f,  0.0f, 1f, 0f,
        -halfSide,  halfSide, -halfSide, -1.0f,  0.0f,  0.0f, 0f, 0f,
        -halfSide, -halfSide, -halfSide, -1.0f,  0.0f,  0.0f, 0f, 1f,
        -halfSide, -halfSide, -halfSide, -1.0f,  0.0f,  0.0f, 0f, 1f,
        -halfSide, -halfSide,  halfSide, -1.0f,  0.0f,  0.0f, 1f, 1f,
        -halfSide,  halfSide,  halfSide, -1.0f,  0.0f,  0.0f, 1f, 0f,

        halfSide,  halfSide,  halfSide,  1.0f,  0.0f,  0.0f, 0f, 0f,
        halfSide,  halfSide, -halfSide,  1.0f,  0.0f,  0.0f, 1f, 0f,
        halfSide, -halfSide, -halfSide,  1.0f,  0.0f,  0.0f, 1f, 1f,
        halfSide, -halfSide, -halfSide,  1.0f,  0.0f,  0.0f, 1f, 1f,
        halfSide, -halfSide,  halfSide,  1.0f,  0.0f,  0.0f, 0f, 1f,
        halfSide,  halfSide,  halfSide,  1.0f,  0.0f,  0.0f, 0f, 0f,

        -halfSide, -halfSide, -halfSide,  0.0f, -1.0f,  0.0f, 0f, 0f,
        halfSide, -halfSide, -halfSide,  0.0f, -1.0f,  0.0f, 0f, 1f,
        halfSide, -halfSide,  halfSide,  0.0f, -1.0f,  0.0f, 1f, 1f,
        halfSide, -halfSide,  halfSide,  0.0f, -1.0f,  0.0f, 1f, 1f,
        -halfSide, -halfSide,  halfSide,  0.0f, -1.0f,  0.0f, 1f, 0f,
        -halfSide, -halfSide, -halfSide,  0.0f, -1.0f,  0.0f, 0f, 0f,

        -halfSide,  halfSide, -halfSide,  0.0f,  1.0f,  0.0f, 0f, 1f,
        halfSide,  halfSide, -halfSide,  0.0f,  1.0f,  0.0f, 0f, 0f,
        halfSide,  halfSide,  halfSide,  0.0f,  1.0f,  0.0f, 1f, 0f,
        halfSide,  halfSide,  halfSide,  0.0f,  1.0f,  0.0f, 1f, 0f,
        -halfSide,  halfSide,  halfSide,  0.0f,  1.0f,  0.0f, 1f, 1f,
        -halfSide,  halfSide, -halfSide,  0.0f,  1.0f,  0.0f, 0f, 1f
    )

    //Processed data
    private val coordinatesPerVertex = 3
    private val coordinatesPerNormal = 3
    private val coordinatesPerTexture = 2
    private val coordinatesPerPoint = coordinatesPerVertex + coordinatesPerNormal + coordinatesPerTexture
    private val pointsCount = data.size / coordinatesPerPoint
    private val vertexStride: Int = coordinatesPerVertex * Float.SIZE_BYTES
    private lateinit var vertices: FloatArray
    private lateinit var vertexBuffer: FloatBuffer

    private fun processData() {
        val vertexList = emptyList<Float>().toMutableList()
        for (pointIdx in 0 until pointsCount) {
            vertexList.add(data[pointIdx * coordinatesPerPoint + 0])
            vertexList.add(data[pointIdx * coordinatesPerPoint + 1])
            vertexList.add(data[pointIdx * coordinatesPerPoint + 2])
        }
        vertices = vertexList.toFloatArray()
        vertexBuffer =
            ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(vertices)
                    position(0)
                }
            }
    }

    //Shaders
    private var program: Int

    init {
        textureId = Dependencies.textureLoader.loadSkybox(textureResourceIds)
        processData()
        Matrix.setIdentityM(modelMatrix, 0)
        program = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, loadShader(GLES30.GL_VERTEX_SHADER, SKYBOX_VERTEX_SHADER))
            GLES30.glAttachShader(it, loadShader(GLES30.GL_FRAGMENT_SHADER, SKYBOX_FRAGMENT_SHADER))
            GLES30.glLinkProgram(it)
        }
    }
    override fun draw(view: FloatArray, projection: FloatArray) {
        GLES30.glUseProgram(program)
        GLES30.glDisable(GLES30.GL_DEPTH_TEST)
        pipeline.execute(modelMatrix)
        val posLoc = GLES30.glGetAttribLocation(program, "position")
        val modelLoc = GLES30.glGetUniformLocation(program, "model")
        val viewLoc = GLES30.glGetUniformLocation(program, "view")
        val projectionLoc = GLES30.glGetUniformLocation(program, "projection")
        GLES30.glUniformMatrix4fv(modelLoc, 1, false, modelMatrix, 0)
        GLES30.glUniformMatrix4fv(viewLoc, 1, false, view, 0)
        GLES30.glUniformMatrix4fv(projectionLoc, 1, false, projection, 0)
        GLES30.glVertexAttribPointer(
            posLoc,
            coordinatesPerVertex,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(posLoc)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, textureId)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, pointsCount)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
    }
}