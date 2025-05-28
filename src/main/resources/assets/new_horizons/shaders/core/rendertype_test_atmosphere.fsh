#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;

uniform vec4 ColorModulator;

// New uniforms for bloom parameters.
uniform float bloomThreshold;  // brightness threshold for bloom effect
uniform float bloomIntensity;  // strength of bloom contribution
uniform vec3 bloomColor;       // color tint for the bloom

in vec2 texCoord0;
in vec3 normal;
in vec2 screenCoord;
in float dist;
in vec3 that;
in vec3 lightDirection;

out vec4 fragColor;

// A colored bloom function: if a pixel's brightness exceeds a threshold,
// a bloom contribution colored by bloomColor is added.
vec3 computeBloom(vec3 color, float threshold, float intensity, vec3 tint) {
    // Calculate perceived luminance using a standard weighted sum.
    float brightness = dot(color, vec3(0.2126, 0.7152, 0.0722));
    // If above threshold, mix the bloom tint and add an intensity boost.
    if (brightness > threshold) {
        return color * intensity * tint;
    } else {
        return vec3(0.0);
    }
}

void main() {
    // Convert screen coordinates (normally in [0,1] space) to a -1 to 1 range.
    vec2 uv = (screenCoord - 0.5) * 2.0;

    // 'd' is slightly adjusted from the input distance.
    float d = dist - 0.9;

    // Calculate a base color similar to the original design.
    vec4 baseColor = ColorModulator + vec4(d * 1.6);

    // Compute a simple scattering term: the dot product between the normalized normal and light direction.
    // Raising it to a power creates a soft glow effect on surfaces facing the light.
    float scatter = pow(max(dot(normalize(normal), normalize(lightDirection)), 0.0), 2.0);


    vec3 lightDir2 = vec3(0.,0.0,1.);

	float shadow = max(dot(normal, lightDir2)+0.2, 0.00)*1.4;

    // Apply a subtle color tint based on scattering. Tweak the vector values to adjust the tint.
    vec3 scatteredColor = baseColor.rgb + scatter * vec3(1.0);

    // Compute colored bloom: areas with brightness above bloomThreshold will be tinted.
    vec3 bloom = computeBloom(scatteredColor, bloomThreshold, bloomIntensity, bloomColor);

    // Add the bloom contribution to the scattered color.
    vec3 finalColor = scatteredColor + bloom;

    fragColor = vec4((2.0 * finalColor * shadow) - scatter, length(scatteredColor)*shadow*dist) * ColorModulator;
	fragColor = vec4(max(0.0,fragColor.r),max(0.0,fragColor.g),max(0.0,fragColor.b),max(0.0,fragColor.a));
	fragColor.a = (fragColor.a + (ColorModulator.a*0.1)) * (1.2 * length(fragColor.rgb));

	if (fragColor.a < 0.01){
		return;
	}
}














