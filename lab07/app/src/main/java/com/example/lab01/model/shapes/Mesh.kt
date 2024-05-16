package com.example.lab01.model.shapes

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.lab01.Dependencies
import com.example.lab01.R
import com.example.lab01.model.light.LightShading
import com.example.lab01.model.shaders.BASE_FRAGMENT_SHADER
import com.example.lab01.model.shaders.BASE_VERTEX_SHADER
import com.example.lab01.model.shaders.GOURAUD_FRAGMENT_SHADER
import com.example.lab01.model.shaders.GOURAUD_VERTEX_SHADER
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
    private var textureResourceId1: Int = R.drawable.default_texture,
    private var textureResourceId2: Int = R.drawable.default_texture,
    private var color: FloatArray = floatArrayOf(1f, 0.4f, 0f, 0f)
) : Shape {

    //Model pipeline
    var pipeline = Pipeline()
    private var modelMatrix = FloatArray(16)

    //Raw data
    private val textureData1: TextureData
    private val textureData2: TextureData

    //Processed data
    private val coordinatesPerVertex = 3
    private val coordinatesPerNormal = 3
    private val coordinatesPerTexture = 2

    private val vertexStride: Int = coordinatesPerVertex * Float.SIZE_BYTES
    private val normalStride: Int = coordinatesPerNormal * Float.SIZE_BYTES
    private val textureStride: Int = coordinatesPerTexture * Float.SIZE_BYTES

    private var pointsCount: Int = 0
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var normalBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer

    private var meshLoader = MeshLoader()

    fun getCurrentPosition() = Vector(modelMatrix[12], modelMatrix[13], modelMatrix[14])

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

    //Shaders
    private var lightOffProgram: Int
    private var gouraudProgram: Int
    private var phongProgram: Int

    init {
        textureData1 = Dependencies.textureLoader.loadTexture(textureResourceId1)
        textureData2 = Dependencies.textureLoader.loadTexture(textureResourceId2)
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
        val textureUnit1Loc = GLES20.glGetUniformLocation(program, "texture_unit1")
        val textureUnit2Loc = GLES20.glGetUniformLocation(program, "texture_unit2")
        val texture1IntensityLoc = GLES20.glGetUniformLocation(program, "texture1_intensity")
        val texture2IntensityLoc = GLES20.glGetUniformLocation(program, "texture2_intensity")
        GLES20.glUniform1i(textureUnit1Loc, textureData1.textureNumber)
        GLES20.glUniform1i(textureUnit2Loc, textureData2.textureNumber)
        GLES20.glUniform1f(texture1IntensityLoc, Dependencies.pointLight.getTexture1Intensity())
        GLES20.glUniform1f(texture2IntensityLoc, Dependencies.pointLight.getTexture2Intensity())
        GLES20.glVertexAttribPointer(
            texLoc,
            coordinatesPerTexture,
            GLES20.GL_FLOAT,
            false,
            textureStride,
            textureBuffer
        )
        GLES20.glEnableVertexAttribArray(texLoc)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureData1.textureNumber)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureData1.textureId)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureData2.textureNumber)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureData2.textureId)
    }

    private fun setLightParams(program: Int) {
        val modelTypeLoc = GLES20.glGetUniformLocation(program, "model_type")
        val modelInvTLoc = GLES20.glGetUniformLocation(program, "modelInvT")
        val lightColLoc = GLES20.glGetUniformLocation(program, "light_color")
        val lightPositionLoc = GLES20.glGetUniformLocation(program, "light_position")
        val ambientValueLoc = GLES20.glGetUniformLocation(program, "ambient_value")
        val diffuseValueLoc = GLES20.glGetUniformLocation(program, "diffuse_value")
        val specularValueLoc = GLES20.glGetUniformLocation(program, "specular_value")
        val k0Loc = GLES20.glGetUniformLocation(program, "k0")
        val k1Loc = GLES20.glGetUniformLocation(program, "k1")
        val k2Loc = GLES20.glGetUniformLocation(program, "k2")
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
        GLES20.glUniform1f(k0Loc, Dependencies.pointLight.getK0Value())
        GLES20.glUniform1f(k1Loc, Dependencies.pointLight.getK1Value())
        GLES20.glUniform1f(k2Loc, Dependencies.pointLight.getK2Value())
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

    private fun getCurrentProgram() = if (!Dependencies.pointLight.active) {
        lightOffProgram
    } else {
        when(Dependencies.pointLight.shading) {
            LightShading.GOURAUD -> gouraudProgram
            LightShading.PHONG -> phongProgram
        }
    }

    override fun draw(view: FloatArray, projection: FloatArray) {
        val program = getCurrentProgram()
        GLES20.glUseProgram(program)
        pipeline.execute(modelMatrix)
        setBaseParams(program, view, projection)
        setTexturesParams(program)
        if (Dependencies.pointLight.active) {
            setLightParams(program)
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, pointsCount);
    }
}