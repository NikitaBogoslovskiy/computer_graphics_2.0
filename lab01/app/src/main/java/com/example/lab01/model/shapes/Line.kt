package com.example.lab01.model.shapes

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.model.shaders.BASE_FRAGMENT_SHADER_LINE
import com.example.lab01.model.shaders.BASE_FRAGMENT_SHADER_SPRITE
import com.example.lab01.model.shaders.BASE_VERTEX_SHADER_LINE
import com.example.lab01.model.shaders.BASE_VERTEX_SHADER_SPRITE
import com.example.lab01.model.utility.loadShader
import com.example.lab01.utils.Pipeline
import com.example.lab01.utils.TextureData
import com.example.lab01.utils.Vector
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Line(var startPoint: Vector = Vector(),
           var endPoint: Vector = Vector(),
           var startColor: Vector = Vector(),
           var endColor: Vector = Vector()) : Shape {

    var alpha = 1f
    private var modelMatrix = FloatArray(16)

    //Processed data
    private val coordinatesPerVertex = 2
    private val coordinatesPerColor = 4
    private val pointsCount = 2
    private val vertexStride: Int = coordinatesPerVertex * Float.SIZE_BYTES
    private val colorStride: Int = coordinatesPerColor * Float.SIZE_BYTES
    private var vertices = FloatArray(pointsCount * coordinatesPerVertex * Float.SIZE_BYTES)
    private var colors = FloatArray(pointsCount * coordinatesPerColor * Float.SIZE_BYTES)
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer

    fun processData() {
        vertices[0] = startPoint.x
        vertices[1] = startPoint.y
        vertices[2] = endPoint.x
        vertices[3] = endPoint.y
        colors[0] = startColor.x
        colors[1] = startColor.y
        colors[2] = startColor.z
        colors[3] = alpha
        colors[4] = endColor.x
        colors[5] = endColor.y
        colors[6] = endColor.z
        colors[7] = alpha

        vertexBuffer =
            ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(vertices)
                    position(0)
                }
            }
        colorBuffer =
            ByteBuffer.allocateDirect(colors.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(colors)
                    position(0)
                }
            }
    }

    //Shaders
    private var program: Int

    init {
        processData()
        Matrix.setIdentityM(modelMatrix, 0)
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, loadShader(GLES20.GL_VERTEX_SHADER, BASE_VERTEX_SHADER_LINE))
            GLES20.glAttachShader(
                it,
                loadShader(GLES20.GL_FRAGMENT_SHADER, BASE_FRAGMENT_SHADER_LINE)
            )
            GLES20.glLinkProgram(it)
        }
    }

    private fun setBaseParams(program: Int, view: FloatArray, projection: FloatArray) {
        val posLoc = GLES20.glGetAttribLocation(program, "position")
        val colLoc = GLES20.glGetAttribLocation(program, "a_color")
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
        GLES20.glVertexAttribPointer(
            colLoc,
            coordinatesPerColor,
            GLES20.GL_FLOAT,
            false,
            colorStride,
            colorBuffer
        )
        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glEnableVertexAttribArray(colLoc)
    }

    private fun getCurrentProgram() = program

    override fun draw(view: FloatArray, projection: FloatArray) {
        val program = getCurrentProgram()
        GLES20.glUseProgram(program)
        setBaseParams(program, view, projection)
        GLES20.glLineWidth(3f)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, pointsCount)
    }
}