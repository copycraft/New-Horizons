#version 150

// Output color
out vec4 fragColor;

// Uniforms
uniform vec2 iResolution;
uniform float iTime;

// Constants
#define PLANET_SIZE 1.1
#define ATMOSPHERE_THICKNESS 0.8
#define SHOW_SUN 0.0

struct ray {
    vec3 ro;
    vec3 rd;
};

struct sph {
    vec3 c;
    float r;
};

const vec3 center = vec3(0.0);

const int VIEW_SAMPLES = 64;
const int DEPTH_SAMPLES = 8;

const sph atm = sph(center, 1.0 * PLANET_SIZE * ATMOSPHERE_THICKNESS);
const vec3 sunInt = vec3(1.0);
const sph earth = sph(center, 0.5 * PLANET_SIZE);
const vec3 waves = 700.0 / vec3(700.0, 510.0, 440.0);
const vec3 RGBScatter = waves * waves * waves * waves;
const float scatterStrength = 50.0;
const float densFall = 7.0;

const float pi = 4.0 * atan(1.0);

float phase(float cosTheta) {
    return (1.0 + cosTheta * cosTheta) / (16.0 * pi);
}

// Returns the intersection distance with a sphere (or -1 if none)
float sphereDist(ray r, sph s, float n) {
    vec3 rc = r.ro - s.c;
    float c = dot(rc, rc) - (s.r * s.r);
    float b = dot(r.rd, rc);
    float d = b * b - c;
    float t = -b - n * sqrt(abs(d));
    float st = step(0.0, min(t, d));
    return max(0.0, mix(-1.0, t, st));
}

float suc(ray r, vec3 center, float radius) {
    vec3 o = r.ro - center;
    float a = dot(o, o);
    float b = dot(r.rd, o);
    float c = dot(r.rd, r.rd);
    float k = sqrt(a * c - b * b);
    float integ = radius * (pi / 2.0 - atan(b, k)) / k / 3.0;
    integ *= integ;
    return integ;
}

// Density function for the atmosphere
float density(vec3 p) {
    float l = distance(p, center) - earth.r;
    l /= atm.r - earth.r;
    return exp(-l * densFall) * (1.0 - l);
}

// Approximates integrated density along a line segment
float depthFunc(vec3 s, vec3 e) {
    float d = 0.0;
    for (int i = 0; i < DEPTH_SAMPLES; i++) {
        vec3 p = s + (e - s) * (float(i) / float(DEPTH_SAMPLES));
        d += density(p);
    }
    return d / float(DEPTH_SAMPLES) * distance(s, e);
}

// Computes the light scattering contribution along the ray
vec3 lightScattering(ray r, vec3 sunPos, vec3 orig) {
    if (distance(r.ro, center) >= atm.r) {
        float d1 = sphereDist(r, atm, 1.0);
        if (d1 == 0.0) return orig;
        r.ro += r.rd * d1;
    }
    float d2 = sphereDist(r, earth, 1.0);
    if (d2 == 0.0) d2 = sphereDist(r, atm, -1.0);
    float viewDepth = 0.0;
    vec3 l = vec3(0.0);
    for (int i = 0; i < VIEW_SAMPLES; i++) {
        vec3 p = r.ro + r.rd * ((float(i) + 0.5) / float(VIEW_SAMPLES + 1)) * d2;
        ray k;
        k.ro = p;
        k.rd = normalize(sunPos - p);
        if (sphereDist(k, earth, 1.0) == 0.0) {
            float sunDepth = depthFunc(k.ro, k.ro + k.rd * sphereDist(k, atm, -1.0));
            viewDepth = depthFunc(r.ro, p);
            vec3 transmitance = exp(-(sunDepth + viewDepth) * RGBScatter);
            l += transmitance * density(p) * phase(dot(r.rd, normalize(sunPos - p)));
        }
    }
    vec3 origTransmitance = exp(-viewDepth * RGBScatter);
    return orig * origTransmitance + l / float(VIEW_SAMPLES) * d2 * sunInt * RGBScatter * scatterStrength;
}

void main() {
    // Get the current fragment coordinate
    vec2 uv = gl_FragCoord.xy / iResolution.xy;

    vec3 O = vec3(0.0);
    float uvz = -1.0;
    float roz = -3.0;
    vec3 pos = vec3(0.0);

    // Convert screen coordinate to normalized device coordinates
    vec3 ndc = vec3((uv * 2.0 - 1.0) * iResolution.xy / iResolution.y, uvz);
    vec3 ro = vec3(0.0, 0.0, roz);

    // Set up the viewing ray
    ray r;
    r.ro = ro + pos;
    r.rd = normalize(ndc - ro);

    // Define the sun position (rotating over time)
    sph sun = sph(vec3(0.0, 10.0 * cos(iTime), 10.0 * sin(iTime)), SHOW_SUN);

    // Add any additional sun contribution (using suc as a proxy)
    O += suc(r, sun.c, sun.r) * sunInt;

    // Calculate the atmospheric light scattering effect
    O = lightScattering(r, sun.c, O);

    // Set final fragment color
    fragColor = vec4(O, 1.0);
}
