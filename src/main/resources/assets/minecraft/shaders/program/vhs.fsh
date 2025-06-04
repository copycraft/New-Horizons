#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

out vec4 fragColor;

float vignette(vec2 uv) {
    vec2 dist = uv - vec2(0.5);
    float len = dot(dist, dist);
    return smoothstep(0.65, 0.35, len);
}

float staticNoise(vec2 uv) {
    vec2 grid = floor(uv * InSize.xy / 4.0);
    float noise = fract(sin(dot(grid, vec2(127.1, 311.7))) * 43758.5453);
    return noise;
}

void main() {

    float offset = oneTexel.x * 1.5;
    vec3 col;
    col.r = texture(DiffuseSampler, texCoord + vec2(offset, 0.0)).r;
    col.g = texture(DiffuseSampler, texCoord).g;
    col.b = texture(DiffuseSampler, texCoord - vec2(offset, 0.0)).b;


    vec3 blur = vec3(0.0);
    blur += texture(DiffuseSampler, texCoord + oneTexel * vec2(-1, -1)).rgb;
    blur += texture(DiffuseSampler, texCoord + oneTexel * vec2( 0, -1)).rgb;
    blur += texture(DiffuseSampler, texCoord + oneTexel * vec2( 1, -1)).rgb;
    blur += texture(DiffuseSampler, texCoord + oneTexel * vec2(-1,  0)).rgb;
    blur += texture(DiffuseSampler, texCoord + oneTexel * vec2( 0,  0)).rgb;
    blur += texture(DiffuseSampler, texCoord + oneTexel * vec2( 1,  0)).rgb;
    blur += texture(DiffuseSampler, texCoord + oneTexel * vec2(-1,  1)).rgb;
    blur += texture(DiffuseSampler, texCoord + oneTexel * vec2( 0,  1)).rgb;
    blur += texture(DiffuseSampler, texCoord + oneTexel * vec2( 1,  1)).rgb;
    blur /= 9.0;


    col = mix(col, blur, 0.4);


    float noise = staticNoise(texCoord);
    col += vec3(noise * 0.05);


    if (mod(floor(texCoord.y * InSize.y), 3.0) < 1.0) {
        col *= 0.96;
        col.rg *= 0.95;
    }


    float vig = vignette(texCoord);
    col *= vig;


    float gray = dot(col, vec3(0.299, 0.587, 0.114));
    col = mix(vec3(gray), col, 0.85);
    col *= vec3(0.95, 0.95, 1.0);


    fragColor = vec4(col, 1.0);
}
