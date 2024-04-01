package com.example.lab01.model.shaders

const val BASE_FRAGMENT_SHADER =
    """
        precision mediump float;
        uniform vec4 color;
        
        void main() {
           gl_FragColor = color;
        }
    """

const val MULTICOLOR_FRAGMENT_SHADER =
    """
        precision mediump float;
        varying vec4 v_color;
        
        void main() {
           gl_FragColor = v_color;
        }
    """

const val TEXTURED_FRAGMENT_SHADER =
    """
        precision mediump float;
        varying vec4 v_position;
        
        void main() {
            float k = 25.0;
            int sum = int(v_position.x * k);
            if ((sum - (sum / 2 * 2)) == 0) {
                gl_FragColor = vec4(0, 0.8, 0.8, 1);
            } else {
                gl_FragColor = vec4(1, 1, 1, 1);
            }
        }
    """

const val GOURAUD_FRAGMENT_SHADER =
    """
        precision mediump float;
        
        uniform vec4 color;
        
        varying vec3 combined_light;
        
        void main() {
           gl_FragColor = vec4(combined_light, 1.0) * color;
        }
    """

const val PHONG_FRAGMENT_SHADER =
    """
        precision mediump float;
        
        uniform int model_type; 
        uniform vec4 color;
        uniform float ambient_value;
        uniform float diffuse_value;
        uniform float specular_value;
        uniform vec4 light_color;
        uniform vec3 light_position;
        uniform vec3 camera_position;
        
        varying vec3 v_normal;
        varying vec3 frag_position;
        
        void main() {
           vec3 combined_light = vec3(0.0);
           
           vec3 norm = normalize(v_normal);
           vec3 light_dir = normalize(light_position - frag_position); 
           float diff = max(dot(norm, light_dir), 0.0);
           vec3 diffuse = vec3(diffuse_value * diff * light_color);
           combined_light += diffuse;
           
           if (model_type == 1) {
               vec3 ambient = vec3(ambient_value * light_color);
               
               vec3 view_dir = normalize(camera_position - frag_position);
               vec3 reflect_dir = reflect(-light_dir, norm);
               float spec = pow(max(dot(view_dir, reflect_dir), 0.0), 64.0);
               vec3 specular = vec3(specular_value * spec * light_color); 
               
               combined_light += ambient + specular;
           }
           
           gl_FragColor = vec4(combined_light, 1.0) * color;
        }
    """