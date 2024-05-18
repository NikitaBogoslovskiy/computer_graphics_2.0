package com.example.lab01.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import com.example.lab01.Dependencies


data class TextureData(var textureNumber: Int = -1, var textureId: Int = 0, var width: Int = 0, var height: Int = 0)

class TextureLoader {
    private var alreadyLoaded = 0

    fun loadTexture(resourceId: Int): TextureData {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            return TextureData()
        }

        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(
            Dependencies.context.resources, resourceId, options
        )
        if (bitmap == null) {
            GLES30.glDeleteTextures(1, textureIds, 0)
            return TextureData()
        }

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + alreadyLoaded)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST
        )
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)

        return TextureData(alreadyLoaded++, textureIds[0], bitmap.width, bitmap.height)
    }

    fun loadSkybox(ids: List<Int>): Int {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            return -1
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, textureIds[0]);

        val options = BitmapFactory.Options()
        options.inScaled = false
        for (i in ids.indices) {
            val bitmap = BitmapFactory.decodeResource(
                Dependencies.context.resources, ids[i], options
            )
            if (bitmap == null) {
                GLES30.glDeleteTextures(1, textureIds, 0)
                return -1
            }
            GLUtils.texImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, bitmap, 0)
            bitmap.recycle()
        }

        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_CUBE_MAP,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_LINEAR
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_CUBE_MAP,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_CUBE_MAP,
            GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_CLAMP_TO_EDGE
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_CUBE_MAP,
            GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_CLAMP_TO_EDGE
        )

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        return textureIds[0]
    }
}