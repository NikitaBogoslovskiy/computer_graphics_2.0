package com.example.lab01.model.shaders

const val BASE_VERTEX_SHADER =
    """
        uniform mat4 uMVPMatrix;
        attribute vec4 position;
        void main() {
            gl_Position = uMVPMatrix * position;
        }
    """

const val MULTICOLOR_VERTEX_SHADER =
    """
        uniform mat4 uMVPMatrix;
        attribute vec4 position;
        attribute vec4 a_color;
        varying vec4 v_color;
       
        void main() {
            v_color = a_color;
            gl_Position = uMVPMatrix * position;
        }
    """