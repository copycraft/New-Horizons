package org.copycraftDev.new_horizons.lazuli_snnipets;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class LazuliGeometryBuilder {

    /**
     * Builds a textured sphere using triangle strips.
     *
     * @param res           The resolution of the sphere (higher = smoother)
     * @param radius        The radius of the sphere
     * @param center        The center position of the sphere
     * @param camera        The player camera
     * @param matrix4f2     The transformation matrix
     * @param bufferBuilder The buffer to store vertices
     */
    public static void buildTexturedSphere(int res, float radius, Vec3d center, Vec3d axle, float roll,  Camera camera, Matrix4f matrix4f2, BufferBuilder bufferBuilder) {
        Vec3d displacement = camera.getPos().add(center); // Get displacement relative to camera
        float x = (float) -displacement.x;
        float y = (float) -displacement.y;
        float z = (float) -displacement.z;

        float angle2 = 0f;
        float nextAngle2;
        float thisRingRadius;
        float nextRingRadius;

        Vec3d yAxle = axle;
        Vec3d xAxle = (yAxle.x == 0 && yAxle.z == 0) ? new Vec3d(1, 0, 0) : new Vec3d(yAxle.x, 0, yAxle.z).normalize().rotateY(90);
        Vec3d zAxle = LazuliMathUtils.rotateAroundAxis(xAxle,yAxle,90);

        for (int p = 0; p < res; p++) {
            angle2 += (float) (Math.PI / res);
            nextAngle2 = angle2 - (float) (Math.PI / res);
            thisRingRadius = (float) Math.sin(angle2) * radius;
            nextRingRadius = (float) Math.sin(nextAngle2) * radius;
            double thisRingY = Math.cos(angle2) * radius;
            double nextRingY = Math.cos(nextAngle2) * radius;

            float angle = roll;
            for (int i = 0; i < res * 2; i++) {

                //Vertex 1
                Vec3d v1 = new Vec3d(
                        Math.sin(angle) * thisRingRadius,
                        thisRingY,
                        Math.cos(angle) * thisRingRadius
                );
                //Vertex 2
                Vec3d v2 = new Vec3d(
                        Math.sin(angle) * nextRingRadius,
                        nextRingY,
                        Math.cos(angle) * nextRingRadius
                );

                angle += (float) (Math.PI / res);

                //Vertex 3
                Vec3d v3 = new Vec3d(
                        Math.sin(angle) * nextRingRadius,
                        nextRingY,
                        Math.cos(angle) * nextRingRadius
                );
                //Vertex 4
                Vec3d v4 = new Vec3d(
                        Math.sin(angle) * thisRingRadius,
                        thisRingY,
                        Math.cos(angle) * thisRingRadius
                );

                v1 = yAxle.multiply(v1.y).add(xAxle.multiply(v1.x)).add(zAxle.multiply(v1.z));
                v2 = yAxle.multiply(v2.y).add(xAxle.multiply(v2.x)).add(zAxle.multiply(v2.z));
                v3 = yAxle.multiply(v3.y).add(xAxle.multiply(v3.x)).add(zAxle.multiply(v3.z));
                v4 = yAxle.multiply(v4.y).add(xAxle.multiply(v4.x)).add(zAxle.multiply(v4.z));


                //Equatorial texture coordinates
                double U1 = ((angle - roll) / Math.PI) / 2;
                double U2 = ((angle - roll) / Math.PI) / 2 + (0.5 / res);
                //Longitudinal texture coordinates
                double V1 = angle2 / Math.PI;
                double V2 = nextAngle2 / Math.PI;

                //actually add the vertexes
                addVertexTextureNormal(v1.subtract(displacement), U1, V1, v1, matrix4f2, bufferBuilder);
                addVertexTextureNormal(v2.subtract(displacement), U1, V2, v2, matrix4f2, bufferBuilder);
                addVertexTextureNormal(v3.subtract(displacement), U2, V2, v3, matrix4f2, bufferBuilder);
                addVertexTextureNormal(v4.subtract(displacement), U2, V1, v4, matrix4f2, bufferBuilder);
            }
        }
    }

    /**
     * Builds a simple sprite billboard (flat quad that always faces the camera).
     *
     * @param radius        The radius of the billboard
     * @param center        The center position of the sprite
     * @param camera        The player camera
     * @param matrix4f2     The transformation matrix
     * @param bufferBuilder The buffer to store vertices
     */
    public static void buildSpriteBillboard(float radius, Vec3d center, Camera camera, Matrix4f matrix4f2, BufferBuilder bufferBuilder) {
        Vec3d displacement = camera.getPos().add(center);
        float x = (float) -displacement.x;
        float y = (float) -displacement.y;
        float z = (float) -displacement.z;

//        addVertex(new Vec3d(x, y, z), 0, 0, matrix4f2, bufferBuilder);
//        addVertex(new Vec3d(x, y + radius, z), 0, 1, matrix4f2, bufferBuilder);
//        addVertex(new Vec3d(x + radius, y + radius, z), 1, 1, matrix4f2, bufferBuilder);
//        addVertex(new Vec3d(x + radius, y, z), 1, 0, matrix4f2, bufferBuilder);
    }

    /**
     * Helper function to add a vertex with position, texture, color, and normal data.
     *
     * @param pos           The position of the vertex
     * @param u             Texture U coordinate
     * @param v             Texture V coordinate
     * @param matrix4f2     The transformation matrix
     * @param bufferBuilder The buffer to store vertices
     */
    private static void addVertexTextureNormal(Vec3d pos, double u, double v, Vec3d normal, Matrix4f matrix4f2, BufferBuilder bufferBuilder) {
        normal = normal.normalize();

        bufferBuilder.vertex(matrix4f2, (float) pos.x, (float) pos.y, (float) pos.z)
                .texture((float) u, (float) v)
                .color(0, 0, 0, 1)
                .normal((float) normal.x, (float) normal.y, (float) normal.z);
    }
}