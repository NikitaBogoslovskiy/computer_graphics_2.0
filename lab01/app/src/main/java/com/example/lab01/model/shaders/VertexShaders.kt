package com.example.lab01.model.shaders

const val BASE_VERTEX_SHADER =
    """
        uniform mat4 model;
        uniform mat4 view;
        uniform mat4 projection;
        attribute vec4 position;
        void main() {
            gl_Position = projection * view * model * position;
        }
    """

const val MULTICOLOR_VERTEX_SHADER =
    """
        uniform mat4 model;
        uniform mat4 view;
        uniform mat4 projection;
        attribute vec4 position;
        attribute vec4 a_color;
        varying vec4 v_color;
       
        void main() {
            v_color = a_color;
            gl_Position =  projection * view * model * position;
        }
    """

const val TEXTURED_VERTEX_SHADER =
    """
        uniform mat4 model;
        uniform mat4 view;
        uniform mat4 projection;
        attribute vec4 a_position;
        varying vec4 v_position;
       
        void main() {
            v_position = a_position;
            gl_Position =  projection * view * model * a_position;
        }
    """

const val LIGHT_VERTEX_SHADER =
    """
        uniform mat4 model;
        uniform mat4 modelInvT;
        uniform mat4 view;
        uniform mat4 projection;
        
        attribute vec3 position;
        attribute vec3 a_normal;
        
        varying vec3 v_normal;
        varying vec3 frag_position;
        
        void main() {
            gl_Position =  projection * view * model * vec4(position, 1.0);
            v_normal = mat3(modelInvT) * a_normal;
            frag_position = vec3(model * vec4(position, 1.0));
        }
    """