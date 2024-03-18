package com.example.lab01.model.shapes

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.lab01.model.shaders.BASE_FRAGMENT_SHADER
import com.example.lab01.model.shaders.BASE_VERTEX_SHADER
import com.example.lab01.model.utility.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

val polygonColor = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)

class RegularPolygon(private var verticesNumber: Int = 5,
                     private var radius: Float = 0.75f,
                     private var color: FloatArray = polygonColor) : Shape {

    private var modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    //Vertices coordinates
    private val coordinates = getCoordinates()
    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(coordinates.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(coordinates)
                position(0)
            }
        }

    //Shaders, program and drawing pipeline
    private val coordinatesPerVertex = 3
    private val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, BASE_VERTEX_SHADER)
    private val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, BASE_FRAGMENT_SHADER)
    private var program: Int = GLES20.glCreateProgram().also {
        GLES20.glAttachShader(it, vertexShader)
        GLES20.glAttachShader(it, fragmentShader)
        GLES20.glLinkProgram(it)
    }
    private val vertexCount: Int = coordinates.size / coordinatesPerVertex
    private val vertexStride: Int = coordinatesPerVertex * Float.SIZE_BYTES

    init {

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, -2.2f, 0f, 0f);
    }

    private fun getCoordinates(): FloatArray {
        val coordinates = FloatArray(3 * verticesNumber)
        val rotationAngle = (2 * PI / verticesNumber).toFloat()
        val startAngle = (- PI / 2).toFloat()
        for(idx in 0 until verticesNumber) {
            val currentAngle = startAngle + idx * rotationAngle
            coordinates[idx * 3] = radius * cos(currentAngle)
            coordinates[idx * 3 + 1] = radius * sin(currentAngle)
            coordinates[idx * 3 + 2] = 0f
        }
        return coordinates
    }

    override fun draw(vPMatrix: FloatArray) {
        Matrix.multiplyMM(mvpMatrix, 0, vPMatrix, 0, modelMatrix, 0)
        val posLoc = GLES20.glGetAttribLocation(program, "position")
        val colLoc = GLES20.glGetUniformLocation(program, "color")
        val mvpMatrixLoc = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUseProgram(program)
        GLES20.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0)
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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)
        GLES20.glDisableVertexAttribArray(posLoc)
    }
}