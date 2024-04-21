package com.example.lab01.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.lab01.Dependencies


data class TextureData(var textureNumber: Int, var textureId: Int, var width: Int, var height: Int)

class TextureLoader {
    private var alreadyLoaded = 0

    fun loadTexture(resourceId: Int): TextureData {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            return TextureData(-1, 0, 0, 0)
        }

        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(
            Dependencies.context.resources, resourceId, options
        )
        if (bitmap == null) {
            GLES20.glDeleteTextures(1, textureIds, 0)
            return TextureData(-1, 0, 0, 0)
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + alreadyLoaded)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST
        )
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        return TextureData(alreadyLoaded++, textureIds[0], bitmap.width, bitmap.height)
    }

}