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
        
        uniform vec4 color;
        uniform sampler2D texture_unit;
        
        uniform float ambient[6];
        uniform float diffuse[5];
        uniform float specular[5];
        uniform float k0[5];
        uniform float k1[5];
        uniform float k2[5];
        uniform vec4 light_color[6];
        uniform vec3 light_position[5];
        uniform vec3 torch_direction[1];
        uniform float torch_inner_cutoff[1];
        uniform float torch_outer_cutoff[1];
        
        uniform vec3 camera_position;
        
        varying vec2 v_texture;
        varying vec3 v_normal;
        varying vec3 frag_position;
        
        void main() {
           vec3 combined_light = vec3(0.0);
           combined_light += ambient[5] * vec3(light_color[5]);
           vec3 norm = normalize(v_normal);
           
           for(int i = 0; i < 5; i++) {
               vec3 light_vec = light_position[i] - frag_position;
               vec3 light_dir = normalize(light_vec);
               float dist = length(light_vec);
               float attenuation = 1.0 / (k0[i] + k1[i] * dist + k2[i] * dist * dist);
               
               //ambient
               vec3 ambient_value = attenuation * vec3(ambient[i] * light_color[i]);
               
               //diffuse
               float diff = max(dot(norm, light_dir), 0.0);
               vec3 diffuse_value = attenuation * vec3(diffuse[i] * diff * light_color[i]);
               
               //specular
               vec3 view_dir = normalize(camera_position - frag_position);
               vec3 reflect_dir = reflect(-light_dir, norm);
               float spec = pow(max(dot(view_dir, reflect_dir), 0.0), 64.0);
               vec3 specular_value = attenuation * vec3(specular[i] * spec * light_color[i]); 
               
               if (i == 4) {
                    float theta = dot(light_dir, normalize(-torch_direction[0]));
                    float epsilon = torch_inner_cutoff[0] - torch_outer_cutoff[0];
                    float intensity = clamp((theta - torch_outer_cutoff[0]) / epsilon, 0.0, 1.0);
                    diffuse_value *= intensity;
                    specular_value *= intensity;
               }
               
               //combined
               combined_light += ambient_value + diffuse_value + specular_value;
           }
           
           vec4 texture = texture2D(texture_unit, v_texture);
           vec4 combined_color = mix(color, texture, texture.w);
           
           gl_FragColor = vec4(combined_light * vec3(combined_color), 1.0);
        }
    """

const val PHONG_FRAGMENT_SHADER_WITH_BUMP_MAPPING =
    """
        #extension GL_OES_standard_derivatives : enable
        precision mediump float;
        
        uniform int model_type; 
        uniform vec4 color;
        uniform sampler2D texture_unit1;
        uniform sampler2D texture_unit2;
        uniform sampler2D texture_bump;
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
        uniform vec2 bump_step;
        
        varying vec2 v_texture;
        varying vec3 v_normal;
        varying vec3 frag_position;
        
        void main() {
           vec3 combined_light = vec3(0.0);
           
           vec2 x_start = vec2(v_texture.x, v_texture.y);
           vec2 x_end = vec2(v_texture.x, v_texture.y);
           vec2 y_start = vec2(v_texture.x, v_texture.y - bump_step.y / 2.0);
           vec2 y_end = vec2(v_texture.x, v_texture.y + bump_step.y / 2.0);
           vec3 x_grad = vec3(texture2D(texture_bump, x_start) - texture2D(texture_bump, x_end));
           vec3 y_grad = vec3(texture2D(texture_bump, y_start) - texture2D(texture_bump, y_end));
           
           vec4 current_point = texture2D(texture_bump, v_texture);
           //vec3 x_grad = vec3(texture2D(texture_bump, v_texture - dFdx(v_texture) / 10.0) - texture2D(texture_bump, v_texture + dFdx(v_texture) / 10.0));
           //vec3 y_grad = vec3(texture2D(texture_bump, v_texture - dFdy(v_texture) / 10.0) - texture2D(texture_bump, v_texture + dFdy(v_texture) / 10.0));
           //vec3 x_grad = vec3(texture2D(texture_bump, v_texture + dFdx(v_texture)) - current_point);
           //vec3 y_grad = vec3(texture2D(texture_bump, v_texture + dFdy(v_texture)) - current_point);
                      
           vec3 norm = normalize(v_normal + v_texture.x * x_grad + v_texture.y * y_grad);
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

const val SKYBOX_FRAGMENT_SHADER =
    """
        precision mediump float;
        
        uniform samplerCube skybox;
        
        varying vec4 v_texture;
        
        void main() {
           mediump vec3 cube = vec3(textureCube(skybox, v_texture.xyz));
           gl_FragColor = vec4(cube, 1.0);
        }
    """