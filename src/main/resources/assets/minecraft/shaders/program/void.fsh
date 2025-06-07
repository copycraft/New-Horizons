#version 150

uniform float    u_time;
uniform vec2     u_resolution;
uniform sampler2D u_endPortalTex; // bind to "textures/environment/end_portal.png"

out vec4 fragColor;

const float vignetteWidth = 0.2;

float waveDistortion(vec2 pos, float offset) {
    return sin(pos.x * 10.0 + offset) * 0.015 +
    sin(pos.y *  7.0 + offset * 1.5) * 0.015;
}

void main() {
    vec2  uv       = gl_FragCoord.xy / u_resolution;
    float t        = u_time;
    vec2  centered = abs(uv - 0.5);
    float half     = 0.5;
    float w        = vignetteWidth + waveDistortion(uv, t);

    if (centered.x > (half - w) || centered.y > (half - w)) {
        // Tile the End Portal texture by repeating twice across both axes
        vec2 tiledUV = fract(uv * 2.0);
        fragColor    = texture(u_endPortalTex, tiledUV);
    } else {
        discard;
    }
}
