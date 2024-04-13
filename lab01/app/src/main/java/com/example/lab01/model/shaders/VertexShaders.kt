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

const val GOURAUD_VERTEX_SHADER =
    """
        uniform mat4 model;
        uniform mat4 modelInvT;
        uniform mat4 view;
        uniform mat4 projection;
        
        uniform int model_type; 
        uniform float ambient_value;
        uniform float diffuse_value;
        uniform float specular_value;
        uniform float k0;
        uniform float k1;
        uniform float k2;
        uniform vec4 light_color;
        uniform vec3 light_position;
        uniform vec3 camera_position;
        
        attribute vec3 position;
        attribute vec3 a_normal;
        attribute vec2 a_texture;
        
        varying vec2 v_texture;
        varying vec3 combined_light;
        
        void main() {
            gl_Position =  projection * view * model * vec4(position, 1.0);
            v_texture = a_texture;
            
            vec3 position = vec3(model * vec4(position, 1.0));
            vec3 normal = mat3(modelInvT) * a_normal;
            
            vec3 combined_light_t = vec3(0.0);
            
            vec3 norm = normalize(normal);
            vec3 light_vec = light_position - position;
            vec3 light_dir = normalize(light_vec); 
            float diff = max(dot(norm, light_dir), 0.0);
            vec3 diffuse = vec3(diffuse_value * diff * light_color);
            combined_light_t += diffuse;
            
            if (model_type == 1) {
               vec3 ambient = vec3(ambient_value * light_color);
               
               vec3 view_dir = normalize(camera_position - position);
               vec3 reflect_dir = reflect(-light_dir, norm);
               float spec = pow(max(dot(view_dir, reflect_dir), 0.0), 64.0);
               vec3 specular = vec3(specular_value * spec * light_color); 
               
               combined_light_t += ambient + specular;
            }
            
            float dist = length(light_vec);
            float attenuation = 1.0 / (k0 + k1 * dist + k2 * dist * dist);
            
            combined_light = attenuation * combined_light_t;
        }
    """

const val PHONG_VERTEX_SHADER =
    """
        uniform mat4 model;
        uniform mat4 modelInvT;
        uniform mat4 view;
        uniform mat4 projection;
        
        attribute vec3 position;
        attribute vec2 a_texture;
        attribute vec3 a_normal;
        
        varying vec2 v_texture;
        varying vec3 v_normal;
        varying vec3 frag_position;
        
        void main() {
            gl_Position =  projection * view * model * vec4(position, 1.0);
            v_texture = a_texture;
            v_normal = mat3(modelInvT) * a_normal;
            frag_position = vec3(model * vec4(position, 1.0));
        }
    """