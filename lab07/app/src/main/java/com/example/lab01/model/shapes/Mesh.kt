package com.example.lab01.model.shapes

import android.opengl.GLES30
import android.opengl.Matrix
import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.model.shaders.PHONG_FRAGMENT_SHADER
import com.example.lab01.model.shaders.PHONG_FRAGMENT_SHADER_WITH_BUMP_MAPPING
import com.example.lab01.model.shaders.PHONG_VERTEX_SHADER
import com.example.lab01.model.shaders.PHONG_VERTEX_SHADER_WITH_BUMP_MAPPING
import com.example.lab01.model.utility.loadShader
import com.example.lab01.utils.MeshLoader
import com.example.lab01.utils.Pipeline
import com.example.lab01.utils.TextureData
import com.example.lab01.utils.Vector
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Mesh(
    private var modelFileId: Int,
    private var textureResourceId: Int = R.drawable.default_texture,
    var color: FloatArray = floatArrayOf(1f, 0.4f, 0f, 0f)
) : Shape {

    //Model pipeline
    var pipeline = Pipeline()
    private var modelMatrix = FloatArray(16)

    //Raw data
    private var textureData = TextureData()

    //Processed data
    private val coordinatesPerVertex = 3
    private val coordinatesPerNormal = 3
    private val coordinatesPerTexture = 2

    private val vertexStride: Int = coordinatesPerVertex * Float.SIZE_BYTES
    private val normalStride: Int = coordinatesPerNormal * Float.SIZE_BYTES
    private val textureStride: Int = coordinatesPerTexture * Float.SIZE_BYTES
    private var maxRadius = 0f
    private var minY = Float.MAX_VALUE
    private var maxY = Float.MIN_VALUE
    private var maxX = Float.MIN_VALUE
    private var pointsCount: Int = 0
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var normalBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer

    private var meshLoader = MeshLoader()

    fun getMaxRadius() = maxRadius
    fun getMinY() = minY
    fun getMaxY() = maxY
    fun getMaxX() = maxX
    fun resetPosition() = Matrix.setIdentityM(modelMatrix, 0)

    private fun processData() {
        val data = meshLoader.loadObj(modelFileId)
        pointsCount = data.vertices.size / coordinatesPerVertex
        shiftVertices(data.vertices)
        vertexBuffer =
            ByteBuffer.allocateDirect(data.vertices.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(data.vertices)
                    position(0)
                }
            }
        normalBuffer =
            ByteBuffer.allocateDirect(data.normals.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(data.normals)
                    position(0)
                }
            }
        textureBuffer =
            ByteBuffer.allocateDirect(data.textures.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(data.textures)
                    position(0)
                }
            }
    }

    private fun shiftVertices(vertices: FloatArray) {
        var pointsList = emptyList<Vector>().toMutableList()
        for(pointIdx in 0 until pointsCount) {
            pointsList.add(Vector(
                x = vertices[pointIdx * 3],
                y = vertices[pointIdx * 3 + 1],
                z = vertices[pointIdx * 3 + 2],
            ))
        }
        val massCenter = pointsList.reduce(Vector::plus) / pointsCount.toFloat()
        pointsList = pointsList.map { it - massCenter }.toMutableList()
        pointsList.forEach {
            val dist = it.norm2()
            if (dist > maxRadius)
                maxRadius = dist
            if (it.y < minY)
                minY = it.y
            if (it.y > maxY)
                maxY = it.y
            if (it.x > maxX)
                maxX = it.x
        }
        for(pointIdx in 0 until pointsCount) {
            vertices[pointIdx * 3] = pointsList[pointIdx].x
            vertices[pointIdx * 3 + 1] = pointsList[pointIdx].y
            vertices[pointIdx * 3 + 2] = pointsList[pointIdx].z
        }
    }

    //Shaders
    private var program: Int = -1

    init {
        init()
    }

    private fun init() {
        program = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, loadShader(GLES30.GL_VERTEX_SHADER, PHONG_VERTEX_SHADER))
            GLES30.glAttachShader(it, loadShader(GLES30.GL_FRAGMENT_SHADER, PHONG_FRAGMENT_SHADER))
            GLES30.glLinkProgram(it)
        }
        textureData = Dependencies.textureLoader.loadTexture(textureResourceId)
        processData()
        Matrix.setIdentityM(modelMatrix, 0)
    }

    private fun setBaseParams(program: Int, view: FloatArray, projection: FloatArray) {
        val posLoc = GLES30.glGetAttribLocation(program, "position")
        val colLoc = GLES30.glGetUniformLocation(program, "color")
        val modelLoc = GLES30.glGetUniformLocation(program, "model")
        val modelInvTLoc = GLES30.glGetUniformLocation(program, "modelInvT")
        val viewLoc = GLES30.glGetUniformLocation(program, "view")
        val projectionLoc = GLES30.glGetUniformLocation(program, "projection")
        val modelInv = FloatArray(16)
        val modelInvT = FloatArray(16)
        Matrix.invertM(modelInv, 0, modelMatrix, 0)
        Matrix.transposeM(modelInvT, 0, modelInv, 0)
        GLES30.glUniformMatrix4fv(modelInvTLoc, 1, false, modelInvT, 0)
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
        GLES30.glUniform4fv(colLoc, 1, color, 0)
        GLES30.glEnableVertexAttribArray(posLoc)
    }

    private fun setTexturesParams(program: Int) {
        val texLoc = GLES30.glGetAttribLocation(program, "a_texture")
        val textureUnitLoc = GLES30.glGetUniformLocation(program, "texture_unit")
        GLES30.glUniform1i(textureUnitLoc, textureData.textureNumber)
        GLES30.glVertexAttribPointer(
            texLoc,
            coordinatesPerTexture,
            GLES30.GL_FLOAT,
            false,
            textureStride,
            textureBuffer
        )
        GLES30.glEnableVertexAttribArray(texLoc)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + textureData.textureNumber)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureData.textureId)
    }

    private fun setLightParams(program: Int) {
        val data = Dependencies.lightManager.getLightsData()

        val ambientLoc = GLES30.glGetUniformLocation(program, "ambient")
        val diffuseLoc = GLES30.glGetUniformLocation(program, "diffuse")
        val specularLoc = GLES30.glGetUniformLocation(program, "specular")
        val k0Loc = GLES30.glGetUniformLocation(program, "k0")
        val k1Loc = GLES30.glGetUniformLocation(program, "k1")
        val k2Loc = GLES30.glGetUniformLocation(program, "k2")
        val lightColLoc = GLES30.glGetUniformLocation(program, "light_color")
        val lightPositionLoc = GLES30.glGetUniformLocation(program, "light_position")
        val torchDirectionLoc = GLES30.glGetUniformLocation(program, "torch_direction")
        val torchInnerCutoffLoc = GLES30.glGetUniformLocation(program, "torch_inner_cutoff")
        val torchOuterCutoffLoc = GLES30.glGetUniformLocation(program, "torch_outer_cutoff")
        val cameraPositionLoc = GLES30.glGetUniformLocation(program, "camera_position")
        val normalLoc = GLES30.glGetAttribLocation(program, "a_normal")

        GLES30.glUniform1fv(ambientLoc, data.ambient.size, data.ambient, 0)
        GLES30.glUniform1fv(diffuseLoc, data.diffuse.size, data.diffuse, 0)
        GLES30.glUniform1fv(specularLoc, data.specular.size, data.specular, 0)
        GLES30.glUniform1fv(k0Loc, data.k0.size, data.k0, 0)
        GLES30.glUniform1fv(k1Loc, data.k1.size, data.k1, 0)
        GLES30.glUniform1fv(k2Loc, data.k2.size, data.k2, 0)
        GLES30.glUniform4fv(lightColLoc, data.lightColor.size / 4, data.lightColor, 0)
        GLES30.glUniform3fv(lightPositionLoc, data.lightPosition.size / 3, data.lightPosition, 0)
        GLES30.glUniform3fv(torchDirectionLoc, data.torchDirection.size / 3, data.torchDirection, 0)
        GLES30.glUniform1fv(torchInnerCutoffLoc, data.torchInnerCutoff.size, data.torchInnerCutoff, 0)
        GLES30.glUniform1fv(torchOuterCutoffLoc, data.torchOuterCutoff.size, data.torchOuterCutoff, 0)

        GLES30.glUniform3fv(cameraPositionLoc, 1, Dependencies.camera.getPosition().toFloatArray(), 0)
        GLES30.glVertexAttribPointer(
            normalLoc,
            coordinatesPerNormal,
            GLES30.GL_FLOAT,
            false,
            normalStride,
            normalBuffer
        )
        GLES30.glEnableVertexAttribArray(normalLoc)
    }

    private fun getCurrentProgram() = program

    override fun draw(view: FloatArray, projection: FloatArray) {
        val program = getCurrentProgram()
        GLES30.glUseProgram(program)
        pipeline.execute(modelMatrix)
        setBaseParams(program, view, projection)
        setTexturesParams(program)
        setLightParams(program)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, pointsCount);
    }
}