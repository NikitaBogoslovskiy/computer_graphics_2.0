package com.example.lab01.model.shaders

const val BASE_VERTEX_SHADER =
    """
        attribute vec4 position;
        void main() {
            gl_Position = position;
        }
    """

const val MULTICOLOR_VERTEX_SHADER =
    """
        attribute vec4 position;
        attribute vec4 a_color;
        varying vec4 v_color;
       
        void main() {
            v_color = a_color;
            gl_Position = position;
        }
    """