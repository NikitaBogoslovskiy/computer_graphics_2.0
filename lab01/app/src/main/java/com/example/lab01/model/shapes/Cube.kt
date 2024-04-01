package com.example.lab01.model.shapes

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.lab01.Dependencies
import com.example.lab01.model.light.LightShading
import com.example.lab01.model.shaders.BASE_FRAGMENT_SHADER
import com.example.lab01.model.shaders.BASE_VERTEX_SHADER
import com.example.lab01.model.shaders.GOURAUD_FRAGMENT_SHADER
import com.example.lab01.model.shaders.GOURAUD_VERTEX_SHADER
import com.example.lab01.model.shaders.PHONG_FRAGMENT_SHADER
import com.example.lab01.model.shaders.PHONG_VERTEX_SHADER
import com.example.lab01.model.utility.loadShader
import com.example.lab01.utils.Pipeline
import com.example.lab01.utils.Vector
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

val cubeColor = floatArrayOf(0.5f, 0.5f, 0f, 1f)

class Cube(private var sideLength: Float = 1.5f,
           var color: FloatArray = cubeColor) : Shape {

    //Model pipeline
    var pipeline = Pipeline()
    private var modelMatrix = FloatArray(16)

    //Raw data
    private var halfSide = sideLength / 2
    private val data = floatArrayOf(
        -halfSide, -halfSide, -halfSide,  0.0f,  0.0f, -1.0f,
        halfSide, -halfSide, -halfSide,  0.0f,  0.0f, -1.0f,
        halfSide,  halfSide, -halfSide,  0.0f,  0.0f, -1.0f,
        halfSide,  halfSide, -halfSide,  0.0f,  0.0f, -1.0f,
        -halfSide,  halfSide, -halfSide,  0.0f,  0.0f, -1.0f,
        -halfSide, -halfSide, -halfSide,  0.0f,  0.0f, -1.0f,

        -halfSide, -halfSide,  halfSide,  0.0f,  0.0f, 1.0f,
        halfSide, -halfSide,  halfSide,  0.0f,  0.0f, 1.0f,
        halfSide,  halfSide,  halfSide,  0.0f,  0.0f, 1.0f,
        halfSide,  halfSide,  halfSide,  0.0f,  0.0f, 1.0f,
        -halfSide,  halfSide,  halfSide,  0.0f,  0.0f, 1.0f,
        -halfSide, -halfSide,  halfSide,  0.0f,  0.0f, 1.0f,

        -halfSide,  halfSide,  halfSide, -1.0f,  0.0f,  0.0f,
        -halfSide,  halfSide, -halfSide, -1.0f,  0.0f,  0.0f,
        -halfSide, -halfSide, -halfSide, -1.0f,  0.0f,  0.0f,
        -halfSide, -halfSide, -halfSide, -1.0f,  0.0f,  0.0f,
        -halfSide, -halfSide,  halfSide, -1.0f,  0.0f,  0.0f,
        -halfSide,  halfSide,  halfSide, -1.0f,  0.0f,  0.0f,

        halfSide,  halfSide,  halfSide,  1.0f,  0.0f,  0.0f,
        halfSide,  halfSide, -halfSide,  1.0f,  0.0f,  0.0f,
        halfSide, -halfSide, -halfSide,  1.0f,  0.0f,  0.0f,
        halfSide, -halfSide, -halfSide,  1.0f,  0.0f,  0.0f,
        halfSide, -halfSide,  halfSide,  1.0f,  0.0f,  0.0f,
        halfSide,  halfSide,  halfSide,  1.0f,  0.0f,  0.0f,

        -halfSide, -halfSide, -halfSide,  0.0f, -1.0f,  0.0f,
        halfSide, -halfSide, -halfSide,  0.0f, -1.0f,  0.0f,
        halfSide, -halfSide,  halfSide,  0.0f, -1.0f,  0.0f,
        halfSide, -halfSide,  halfSide,  0.0f, -1.0f,  0.0f,
        -halfSide, -halfSide,  halfSide,  0.0f, -1.0f,  0.0f,
        -halfSide, -halfSide, -halfSide,  0.0f, -1.0f,  0.0f,

        -halfSide,  halfSide, -halfSide,  0.0f,  1.0f,  0.0f,
        halfSide,  halfSide, -halfSide,  0.0f,  1.0f,  0.0f,
        halfSide,  halfSide,  halfSide,  0.0f,  1.0f,  0.0f,
        halfSide,  halfSide,  halfSide,  0.0f,  1.0f,  0.0f,
        -halfSide,  halfSide,  halfSide,  0.0f,  1.0f,  0.0f,
        -halfSide,  halfSide, -halfSide,  0.0f,  1.0f,  0.0f
    )

    //Processed data
    private val coordinatesPerVertex = 3
    private val coordinatesPerNormal = 3
    private val coordinatesPerPoint = coordinatesPerVertex + coordinatesPerNormal
    private val pointsCount = data.size / coordinatesPerPoint
    private val vertexStride: Int = coordinatesPerVertex * Float.SIZE_BYTES
    private val normalStride: Int = coordinatesPerNormal * Float.SIZE_BYTES
    private lateinit var vertices: FloatArray
    private lateinit var normals: FloatArray
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var normalBuffer: FloatBuffer

    private fun processData() {
        val vertexList = emptyList<Float>().toMutableList()
        val normalList = emptyList<Float>().toMutableList()
        for (pointIdx in 0 until pointsCount) {
            vertexList.add(data[pointIdx * coordinatesPerPoint])
            vertexList.add(data[pointIdx * coordinatesPerPoint + 1])
            vertexList.add(data[pointIdx * coordinatesPerPoint + 2])
            normalList.add(data[pointIdx * coordinatesPerPoint + 3])
            normalList.add(data[pointIdx * coordinatesPerPoint + 4])
            normalList.add(data[pointIdx * coordinatesPerPoint + 5])
        }
        vertices = vertexList.toFloatArray()
        normals = normalList.toFloatArray()
        vertexBuffer =
            ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(vertices)
                    position(0)
                }
            }
        normalBuffer =
            ByteBuffer.allocateDirect(normals.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(normals)
                    position(0)
                }
            }
    }

    //Shaders
    private var lightOffProgram: Int
    private var gouraudProgram: Int
    private var phongProgram: Int

    init {
        processData()
        Matrix.setIdentityM(modelMatrix, 0)
        lightOffProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, loadShader(GLES20.GL_VERTEX_SHADER, BASE_VERTEX_SHADER))
            GLES20.glAttachShader(it, loadShader(GLES20.GL_FRAGMENT_SHADER, BASE_FRAGMENT_SHADER))
            GLES20.glLinkProgram(it)
        }
        phongProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, loadShader(GLES20.GL_VERTEX_SHADER, PHONG_VERTEX_SHADER))
            GLES20.glAttachShader(it, loadShader(GLES20.GL_FRAGMENT_SHADER, PHONG_FRAGMENT_SHADER))
            GLES20.glLinkProgram(it)
        }
        gouraudProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, loadShader(GLES20.GL_VERTEX_SHADER, GOURAUD_VERTEX_SHADER))
            GLES20.glAttachShader(it, loadShader(GLES20.GL_FRAGMENT_SHADER, GOURAUD_FRAGMENT_SHADER))
            GLES20.glLinkProgram(it)
        }
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        pipeline.execute(modelMatrix)
        val program = if (!Dependencies.pointLight.active) {
            lightOffProgram
        } else {
            when(Dependencies.pointLight.shading) {
                LightShading.GOURAUD -> gouraudProgram
                LightShading.PHONG -> phongProgram
            }
        }
        val posLoc = GLES20.glGetAttribLocation(program, "position")
        val colLoc = GLES20.glGetUniformLocation(program, "color")
        val modelLoc = GLES20.glGetUniformLocation(program, "model")
        val viewLoc = GLES20.glGetUniformLocation(program, "view")
        val projectionLoc = GLES20.glGetUniformLocation(program, "projection")
        GLES20.glUseProgram(program)
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
        if (Dependencies.pointLight.active) {
            val modelTypeLoc = GLES20.glGetUniformLocation(program, "model_type")
            val modelInvTLoc = GLES20.glGetUniformLocation(program, "modelInvT")
            val lightColLoc = GLES20.glGetUniformLocation(program, "light_color")
            val lightPositionLoc = GLES20.glGetUniformLocation(program, "light_position")
            val ambientValueLoc = GLES20.glGetUniformLocation(program, "ambient_value")
            val diffuseValueLoc = GLES20.glGetUniformLocation(program, "diffuse_value")
            val specularValueLoc = GLES20.glGetUniformLocation(program, "specular_value")
            val cameraPositionLoc = GLES20.glGetUniformLocation(program, "camera_position")
            val normalLoc = GLES20.glGetAttribLocation(program, "a_normal")
            val modelInv = FloatArray(16)
            val modelInvT = FloatArray(16)
            Matrix.invertM(modelInv, 0, modelMatrix, 0)
            Matrix.transposeM(modelInvT, 0, modelInv, 0)
            GLES20.glUniform1i(modelTypeLoc, Dependencies.pointLight.model.toInt())
            GLES20.glUniformMatrix4fv(modelInvTLoc, 1, false, modelInvT, 0)
            GLES20.glUniform4fv(lightColLoc, 1, Dependencies.pointLight.color, 0)
            GLES20.glUniform3fv(lightPositionLoc, 1, Dependencies.pointLight.position, 0)
            GLES20.glUniform1f(ambientValueLoc, Dependencies.pointLight.getAmbientValue())
            GLES20.glUniform1f(diffuseValueLoc, Dependencies.pointLight.getDiffuseValue())
            GLES20.glUniform1f(specularValueLoc, Dependencies.pointLight.getSpecularValue())
            GLES20.glUniform3fv(cameraPositionLoc, 1, Dependencies.camera.getPosition().toFloatArray(), 0)
            GLES20.glVertexAttribPointer(
                normalLoc,
                coordinatesPerNormal,
                GLES20.GL_FLOAT,
                false,
                normalStride,
                normalBuffer
            )
            GLES20.glEnableVertexAttribArray(normalLoc)
        }
        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, pointsCount);
        GLES20.glDisableVertexAttribArray(posLoc)
    }
}