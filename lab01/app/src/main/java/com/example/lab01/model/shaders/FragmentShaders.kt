package com.example.lab01.model.shaders

const val BASE_FRAGMENT_SHADER =
    """
        precision mediump float;
        uniform vec4 color;
        uniform sampler2D texture_unit1;
        uniform sampler2D texture_unit2;
        uniform float texture1_intensity;
        uniform float texture2_intensity;
        
        varying vec2 v_texture;
        
        void main() {
           vec4 texture1 = texture2D(texture_unit1, v_texture);
           vec4 texture2 = texture2D(texture_unit2, v_texture);
           vec4 combined_color = mix(color, texture2, texture2.w * texture1_intensity);
           gl_FragColor = mix(combined_color, texture1, texture1.w * texture2_intensity);
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
        uniform sampler2D texture_unit1;
        uniform sampler2D texture_unit2;
        uniform float texture1_intensity;
        uniform float texture2_intensity;
        
        varying vec2 v_texture;
        varying vec3 combined_light;
        
        void main() {
           vec4 texture1 = texture2D(texture_unit1, v_texture);
           vec4 texture2 = texture2D(texture_unit2, v_texture);
           vec4 combined_color = mix(color, texture2, texture2.w * texture1_intensity);
           combined_color = mix(combined_color, texture1, texture1.w * texture2_intensity);
           gl_FragColor = vec4(combined_light * vec3(combined_color), 1.0);
        }
    """

const val PHONG_FRAGMENT_SHADER =
    """
        precision mediump float;
        
        uniform int model_type; 
        uniform vec4 color;
        uniform sampler2D texture_unit1;
        uniform sampler2D texture_unit2;
        uniform float ambient_value;
        uniform float diffuse_value;
        uniform float specular_value;
        uniform float k0;
        uniform float k1;
        uniform float k2;
        uniform float texture1_intensity;
        uniform float texture2_intensity;
        uniform vec4 light_color;
        uniform vec3 light_position;
        uniform vec3 camera_position;
        
        varying vec2 v_texture;
        varying vec3 v_normal;
        varying vec3 frag_position;
        
        void main() {
           vec3 combined_light = vec3(0.0);
           
           vec3 norm = normalize(v_normal);
           vec3 light_vec = light_position - frag_position;
           vec3 light_dir = normalize(light_vec); 
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
           
           float dist = length(light_vec);
           float attenuation = 1.0 / (k0 + k1 * dist + k2 * dist * dist);
           
           vec4 texture1 = texture2D(texture_unit1, v_texture);
           vec4 texture2 = texture2D(texture_unit2, v_texture);
           vec4 combined_color = mix(color, texture2, texture2.w * texture1_intensity);
           combined_color = mix(combined_color, texture1, texture1.w * texture2_intensity);
           
           gl_FragColor = vec4(attenuation * combined_light * vec3(combined_color), 1.0);
        }
    """