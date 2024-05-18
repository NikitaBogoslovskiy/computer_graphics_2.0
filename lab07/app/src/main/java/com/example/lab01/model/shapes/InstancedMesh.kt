package com.example.lab01.model.shapes

import android.opengl.GLES30
import android.opengl.Matrix
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.model.shaders.PHONG_FRAGMENT_SHADER
import com.example.lab01.model.shaders.PHONG_VERTEX_SHADER
import com.example.lab01.model.shaders.PHONG_VERTEX_SHADER_INSTANCED
import com.example.lab01.model.utility.loadShader
import com.example.lab01.utils.MeshLoader
import com.example.lab01.utils.Pipeline
import com.example.lab01.utils.TextureData
import com.example.lab01.utils.Vector
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class InstancedMesh(
    private var textureResourceId: Int = R.drawable.default_texture,
    private var modelFileId: Int,
    private var color: FloatArray = floatArrayOf(1f, 0.4f, 0f, 0f)
) : Shape {

    //Model pipeline
    var pipeline = Pipeline()
    private var modelMatrices = emptyList<FloatArray>().toMutableList()
    private var modelInvTMatrices = emptyList<FloatArray>().toMutableList()
    private var hasExecutedUniqueList = emptyList<Boolean>().toMutableList()

    //Raw data
    private var textureData = TextureData()

    //Processed data
    private val coordinatesPerVertex = 3
    private val coordinatesPerNormal = 3
    private val coordinatesPerTexture = 2
    private val modelMatrixSize = 16

    private val vertexStride: Int = coordinatesPerVertex * Float.SIZE_BYTES
    private val normalStride: Int = coordinatesPerNormal * Float.SIZE_BYTES
    private val textureStride: Int = coordinatesPerTexture * Float.SIZE_BYTES
    private val modelMatrixStride: Int = modelMatrixSize * Float.SIZE_BYTES

    private var pointsCount: Int = 0
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var normalBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private lateinit var modelMatricesBuffer: FloatBuffer
    private lateinit var modelInvTMatricesBuffer: FloatBuffer

    private var meshLoader = MeshLoader()

    private fun processData() {
        val data = meshLoader.loadObj(modelFileId)
        pointsCount = data.vertices.size / coordinatesPerVertex
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

    private fun updateModelInvTMatrices() {
        for(i in modelMatrices.indices) {
            val modelInv = FloatArray(16)
            val modelInvT = FloatArray(16)
            Matrix.invertM(modelInv, 0, modelMatrices[i], 0)
            Matrix.transposeM(modelInvT, 0, modelInv, 0)
            modelInvTMatrices[i] = modelInvT
        }
    }

    private fun updateMatricesBuffers() {
        modelMatricesBuffer.clear()
        modelInvTMatricesBuffer.clear()
        modelMatricesBuffer.put(modelMatrices.map { it.toMutableList() }.flatten().toFloatArray())
        modelMatricesBuffer.position(0)
        modelInvTMatricesBuffer.put(modelInvTMatrices.map { it.toMutableList() }.flatten().toFloatArray())
        modelInvTMatricesBuffer.position(0)
    }

    private fun allocateMatricesBuffers() {
        modelMatricesBuffer = ByteBuffer.allocateDirect(modelMatrices.size * modelMatrixSize * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                position(0)
            }
        }
        modelInvTMatricesBuffer = ByteBuffer.allocateDirect(modelInvTMatrices.size * modelMatrixSize * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                position(0)
            }
        }
    }

    //Shaders
    private var program: Int = -1

    init {
        init()
    }

    private fun init() {
        textureData = Dependencies.textureLoader.loadTexture(textureResourceId)
        processData()
        program = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, loadShader(GLES30.GL_VERTEX_SHADER, PHONG_VERTEX_SHADER_INSTANCED))
            GLES30.glAttachShader(it, loadShader(GLES30.GL_FRAGMENT_SHADER, PHONG_FRAGMENT_SHADER))
            GLES30.glLinkProgram(it)
        }
        allocateMatricesBuffers()
    }

    private fun setBaseParams(program: Int, view: FloatArray, projection: FloatArray) {
        val posLoc = GLES30.glGetAttribLocation(program, "position")
        val colLoc = GLES30.glGetUniformLocation(program, "color")
        val modelLoc = GLES30.glGetAttribLocation(program, "model")
        val modelInvTLoc = GLES30.glGetAttribLocation(program, "modelInvT")
        val viewLoc = GLES30.glGetUniformLocation(program, "view")
        val projectionLoc = GLES30.glGetUniformLocation(program, "projection")
        val mat = FloatArray(16)
        Matrix.setIdentityM(mat, 0)
/*        GLES30.glUniformMatrix4fv(modelLoc, 1, false, modelMatrices.map { it.toMutableList() }.flatten().toFloatArray(), 0)
        GLES30.glUniformMatrix4fv(modelInvTLoc, 1, false, modelInvTMatrices.map { it.toMutableList() }.flatten().toFloatArray(), 0)*/
        GLES30.glUniformMatrix4fv(viewLoc, 1, false, view, 0)
        GLES30.glUniformMatrix4fv(projectionLoc, 1, false, projection, 0)
        GLES30.glUniform4fv(colLoc, 1, color, 0)
        GLES30.glVertexAttribPointer(
            posLoc,
            coordinatesPerVertex,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(posLoc)
        GLES30.glVertexAttribPointer(
            modelLoc,
            modelMatrixSize,
            GLES30.GL_FLOAT,
            false,
            modelMatrixStride,
            modelMatricesBuffer
        )
        GLES30.glVertexAttribDivisor(modelLoc, 1)
        GLES30.glEnableVertexAttribArray(modelLoc)
        GLES30.glVertexAttribPointer(
            modelInvTLoc,
            modelMatrixSize,
            GLES30.GL_FLOAT,
            false,
            modelMatrixStride,
            modelInvTMatricesBuffer
        )
        GLES30.glVertexAttribDivisor(modelInvTLoc, 1)
        GLES30.glEnableVertexAttribArray(modelInvTLoc)
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
        val modelTypeLoc = GLES30.glGetUniformLocation(program, "model_type")
        val lightColLoc = GLES30.glGetUniformLocation(program, "light_color")
        val lightPositionLoc = GLES30.glGetUniformLocation(program, "light_position")
        val ambientValueLoc = GLES30.glGetUniformLocation(program, "ambient_value")
        val diffuseValueLoc = GLES30.glGetUniformLocation(program, "diffuse_value")
        val specularValueLoc = GLES30.glGetUniformLocation(program, "specular_value")
        val k0Loc = GLES30.glGetUniformLocation(program, "k0")
        val k1Loc = GLES30.glGetUniformLocation(program, "k1")
        val k2Loc = GLES30.glGetUniformLocation(program, "k2")
        val cameraPositionLoc = GLES30.glGetUniformLocation(program, "camera_position")
        val normalLoc = GLES30.glGetAttribLocation(program, "a_normal")
        GLES30.glUniform1i(modelTypeLoc, Dependencies.pointLight.model.toInt())
        GLES30.glUniform4fv(lightColLoc, 1, Dependencies.pointLight.color, 0)
        GLES30.glUniform3fv(lightPositionLoc, 1, Dependencies.pointLight.position, 0)
        GLES30.glUniform1f(ambientValueLoc, Dependencies.pointLight.getAmbientValue())
        GLES30.glUniform1f(diffuseValueLoc, Dependencies.pointLight.getDiffuseValue())
        GLES30.glUniform1f(specularValueLoc, Dependencies.pointLight.getSpecularValue())
        GLES30.glUniform1f(k0Loc, Dependencies.pointLight.getK0Value())
        GLES30.glUniform1f(k1Loc, Dependencies.pointLight.getK1Value())
        GLES30.glUniform1f(k2Loc, Dependencies.pointLight.getK2Value())
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
        if (modelMatrices.size == 0)
            return

/*        for(i in modelMatrices.indices) {
            pipeline.execute(modelMatrices[i], hasExecutedUniqueList, i)
        }*/
        updateModelInvTMatrices()
        updateMatricesBuffers()

        val program = getCurrentProgram()
        GLES30.glUseProgram(program)
        setBaseParams(program, view, projection)
        setTexturesParams(program)
        setLightParams(program)
        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, pointsCount, modelMatrices.size)
    }

    fun addInstance() {
        val mat = FloatArray(16)
        Matrix.setIdentityM(mat, 0)
        modelMatrices.add(mat)
        modelInvTMatrices.add(mat.copyOf())
        hasExecutedUniqueList.add(false)
        allocateMatricesBuffers()
    }
}