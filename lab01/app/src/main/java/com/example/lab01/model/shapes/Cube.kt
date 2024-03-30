package com.example.lab01.model.shapes

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.lab01.Dependencies
import com.example.lab01.model.shaders.BASE_FRAGMENT_SHADER
import com.example.lab01.model.shaders.BASE_VERTEX_SHADER
import com.example.lab01.model.shaders.LIGHT_FRAGMENT_SHADER
import com.example.lab01.model.shaders.LIGHT_VERTEX_SHADER
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

    var pipeline = Pipeline()
    private var modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    //Vertices coordinates
    private val coordinates = getCoordinates()
    private val indices = shortArrayOf(0, 2, 3, 0, 1, 3, 4, 6, 7, 4, 5, 7, 8, 9, 10, 11, 8, 10, 12,
        13, 14, 15, 12, 14, 16, 17, 18, 16, 19, 18, 20, 21, 22, 20, 23, 22);
    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(coordinates.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(coordinates)
                position(0)
            }
        }
    private val indicesBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(indices.size * Short.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(indices)
                position(0)
            }
        }

    //Shaders, program and drawing pipeline
    private val coordinatesPerVertex = 6
    private val vertexStride: Int = coordinatesPerVertex * Float.SIZE_BYTES
    private val vertexCount = coordinates.size / coordinatesPerVertex
    private var vertexShader: Int
    private var fragmentShader: Int
    private var program: Int

    init {
        Matrix.setIdentityM(modelMatrix, 0)
        if (Dependencies.pointLight == null) {
            vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, BASE_VERTEX_SHADER)
            fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, BASE_FRAGMENT_SHADER)
        } else {
            vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, LIGHT_VERTEX_SHADER)
            fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, LIGHT_FRAGMENT_SHADER)
        }
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    private fun getCoordinates(): FloatArray {
        val halfSide = sideLength / 2
        return floatArrayOf(
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
    }

    override fun draw(vPMatrix: FloatArray) {
        pipeline.execute(modelMatrix)
        Matrix.multiplyMM(mvpMatrix, 0, vPMatrix, 0, modelMatrix, 0)
        val posLoc = GLES20.glGetAttribLocation(program, "position")
        val colLoc = GLES20.glGetUniformLocation(program, "color")
        val mvpMatrixLoc = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUseProgram(program)
        GLES20.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0)
        GLES20.glVertexAttribPointer(
            posLoc,
            coordinatesPerVertex / 2,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        GLES20.glUniform4fv(colLoc, 1, color, 0)
        if (Dependencies.pointLight != null) {
            val lightColLoc = GLES20.glGetUniformLocation(program, "lightColor")
            val ambientValueLoc = GLES20.glGetUniformLocation(program, "ambientValue")
            GLES20.glUniform4fv(lightColLoc, 1, Dependencies.pointLight!!.color, 0)
            GLES20.glUniform1f(ambientValueLoc, Dependencies.pointLight!!.getAmbientValue())
        }
        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(posLoc)
    }
}