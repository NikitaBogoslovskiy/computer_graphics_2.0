package com.example.lab01.model.shapes

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.lab01.model.shaders.BASE_FRAGMENT_SHADER
import com.example.lab01.model.shaders.BASE_VERTEX_SHADER
import com.example.lab01.model.shaders.MULTICOLOR_FRAGMENT_SHADER
import com.example.lab01.model.shaders.MULTICOLOR_VERTEX_SHADER
import com.example.lab01.model.utility.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

var triangleCoordinates = floatArrayOf(
    -0.5f,  -0.5f, 0.0f,
    0.0f, 0.5f, 0.0f,
    0.5f, -0.5f, 0.0f
)
val triangleColors = floatArrayOf(
    1.0f, 0.0f, 0.0f, 1.0f,
    0.0f, 1.0f, 0.0f, 1.0f,
    0.0f, 0.0f, 1.0f, 1.0f
)

class Triangle(private var coordinates: FloatArray = triangleCoordinates,
               private var colors: FloatArray = triangleColors) : Shape {

    private var modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    //Vertices and colors buffers
    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(coordinates.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(coordinates)
                position(0)
            }
        }

    private val colorBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(colors.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(colors)
                position(0)
            }
        }

    //Shaders
    private val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, MULTICOLOR_VERTEX_SHADER)
    private val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, MULTICOLOR_FRAGMENT_SHADER)
    private var program: Int = GLES20.glCreateProgram().also {
        GLES20.glAttachShader(it, vertexShader)
        GLES20.glAttachShader(it, fragmentShader)
        GLES20.glLinkProgram(it)
    }

    //Data for drawing
    private val coordinatesPerVertex = 3
    private val vertexCount: Int = coordinates.size / coordinatesPerVertex
    private val vertexStride: Int = coordinatesPerVertex * Float.SIZE_BYTES
    private val channelsPerColor = 4
    private val colorStride: Int = channelsPerColor * Float.SIZE_BYTES

    init {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, -1f, 0f, 0f);
    }

    override fun draw(vPMatrix: FloatArray) {
        Matrix.multiplyMM(mvpMatrix, 0, vPMatrix, 0, modelMatrix, 0)
        val posLoc = GLES20.glGetAttribLocation(program, "position")
        val colLoc = GLES20.glGetAttribLocation(program, "a_color")
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
        GLES20.glVertexAttribPointer(
            colLoc,
            channelsPerColor,
            GLES20.GL_FLOAT,
            false,
            colorStride,
            colorBuffer
        )
        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glEnableVertexAttribArray(colLoc)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        GLES20.glDisableVertexAttribArray(posLoc)
        GLES20.glDisableVertexAttribArray(colLoc)
    }
}