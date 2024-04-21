package com.example.lab01.utils

import android.content.Context
import com.example.lab01.Dependencies
import com.example.lab01.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


data class MeshData(var vertices: FloatArray, var normals: FloatArray, var textures: FloatArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeshData

        if (!vertices.contentEquals(other.vertices)) return false
        if (!normals.contentEquals(other.normals)) return false
        return textures.contentEquals(other.textures)
    }

    override fun hashCode(): Int {
        var result = vertices.contentHashCode()
        result = 31 * result + normals.contentHashCode()
        result = 31 * result + textures.contentHashCode()
        return result
    }
}


class MeshLoader {
    fun loadObj(fileId: Int): MeshData {
        val vertices = emptyList<Float>().toMutableList()
        val normals = emptyList<Float>().toMutableList()
        val textures = emptyList<Float>().toMutableList()
        val faces = emptyList<String>().toMutableList()

        val inputStream = InputStreamReader(
            Dependencies.context.resources.openRawResource(
                fileId
            )
        )
        val reader = BufferedReader(inputStream)
        while (true) {
            val line = reader.readLine() ?: break
            val parts = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            when (parts[0]) {
                "v" -> {
                    // vertices
                    vertices.add(java.lang.Float.valueOf(parts[1]))
                    vertices.add(java.lang.Float.valueOf(parts[2]))
                    vertices.add(java.lang.Float.valueOf(parts[3]))
                }

                "vt" -> {
                    // textures
                    textures.add(java.lang.Float.valueOf(parts[1]))
                    textures.add(java.lang.Float.valueOf(parts[2]))
                }

                "vn" -> {
                    // normals
                    normals.add(java.lang.Float.valueOf(parts[1]))
                    normals.add(java.lang.Float.valueOf(parts[2]))
                    normals.add(java.lang.Float.valueOf(parts[3]))
                }

                "f" -> {
                    // faces: vertex/texture/normal
                    faces.add(parts[1])
                    faces.add(parts[2])
                    faces.add(parts[3])
                }
            }
        }
        reader.close()

        val numFaces = faces.size
        val normalsBuffer = FloatArray(numFaces * 3)
        val texturesBuffer = FloatArray(numFaces * 2)
        val verticesBuffer = FloatArray(numFaces * 3)
        var verticesIndex = 0
        var normalsIndex = 0
        var texturesIndex = 0
        for (face in faces) {
            val parts = face.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            var index = 3 * (parts[0].toShort() - 1)
            verticesBuffer[verticesIndex++] = vertices.get(index++)
            verticesBuffer[verticesIndex++] = vertices.get(index++)
            verticesBuffer[verticesIndex++] = vertices.get(index)
            index = 2 * (parts[1].toShort() - 1)
            texturesBuffer[normalsIndex++] = textures.get(index++)
            // NOTE: Bitmap gets y-inverted
            texturesBuffer[normalsIndex++] = 1 - textures.get(index)
            index = 3 * (parts[2].toShort() - 1)
            normalsBuffer[texturesIndex++] = normals.get(index++)
            normalsBuffer[texturesIndex++] = normals.get(index++)
            normalsBuffer[texturesIndex++] = normals.get(index)
        }

        return MeshData(
            vertices = verticesBuffer,
            normals = normalsBuffer,
            textures = texturesBuffer)
    }
}
