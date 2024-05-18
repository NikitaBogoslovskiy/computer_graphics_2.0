package com.example.lab01.model.shapes

import android.opengl.GLES30
import android.opengl.Matrix
import com.example.lab01.model.shaders.BASE_FRAGMENT_SHADER
import com.example.lab01.model.shaders.BASE_VERTEX_SHADER
import com.example.lab01.model.shaders.TEXTURED_VERTEX_SHADER
import com.example.lab01.model.shaders.TEXTURED_FRAGMENT_SHADER
import com.example.lab01.model.utility.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class TexturedSquare(private var coordinates: FloatArray = squareCoordinates) : Shape {

    private var modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    //Vertices coordinates
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
    private val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, TEXTURED_VERTEX_SHADER)
    private val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, TEXTURED_FRAGMENT_SHADER)
    private var program: Int = GLES30.glCreateProgram().also {
        GLES30.glAttachShader(it, vertexShader)
        GLES30.glAttachShader(it, fragmentShader)
        GLES30.glLinkProgram(it)
    }
    private val vertexCount: Int = coordinates.size / coordinatesPerVertex
    private val vertexStride: Int = coordinatesPerVertex * Float.SIZE_BYTES

    init {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 1.0f, -0.5f, 0f);
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        val posLoc = GLES30.glGetAttribLocation(program, "a_position")
        val modelLoc = GLES30.glGetUniformLocation(program, "model")
        val viewLoc = GLES30.glGetUniformLocation(program, "view")
        val projectionLoc = GLES30.glGetUniformLocation(program, "projection")
        GLES30.glUseProgram(program)
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
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)
        GLES30.glDisableVertexAttribArray(posLoc)
    }
}