package org.copycraftDev.new_horizons.lazuli_snnipets;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import static java.lang.Math.*;
import static java.lang.Math.sin;

public class LunarGeometryBuilder {

    static Vec3d mainDisplacement = new Vec3d(0,0,0);
    static double pitch  = 0;
    static double yaw  = 0;
    static double roll = 0;
    public static float clampDist = 60000;


    public static void buildTexturedSphere(int res, float radius, Vec3d center, Vec3d axle, float roll, boolean flipNormals,  Camera camera, Matrix4f matrix4f2, BufferBuilder bufferBuilder) {
        Vec3d displacement = camera.getPos().subtract(center);
        float angle2 = 0f;
        float nextAngle2;
        float thisRingRadius;
        float nextRingRadius;


        Vec3d yAxle = axle;
        Vec3d xAxle = (yAxle.x == 0 && yAxle.z == 0) ? new Vec3d(1, 0, 0) : new Vec3d(yAxle.x, 0, yAxle.z).normalize().rotateY(90);
        Vec3d zAxle = LazuliMathUtils.rotateAroundAxis(xAxle,yAxle,90);

        for (int p = 0; p < res; p++) {
            angle2 += (float) (PI / res);
            nextAngle2 = angle2 - (float) (PI / res);
            thisRingRadius = (float) sin(angle2) * radius;
            nextRingRadius = (float) sin(nextAngle2) * radius;
            double thisRingY = cos(angle2) * radius;
            double nextRingY = cos(nextAngle2) * radius;

            float angle = roll;
            for (int i = 0; i < res * 2; i++) {


                Vec3d v1 = new Vec3d(
                        sin(angle) * thisRingRadius,
                        thisRingY,
                        cos(angle) * thisRingRadius
                );

                Vec3d v2 = new Vec3d(
                        sin(angle) * nextRingRadius,
                        nextRingY,
                        cos(angle) * nextRingRadius
                );

                angle += (float) (PI / res);


                Vec3d v3 = new Vec3d(
                        sin(angle) * nextRingRadius,
                        nextRingY,
                        cos(angle) * nextRingRadius
                );

                Vec3d v4 = new Vec3d(
                        sin(angle) * thisRingRadius,
                        thisRingY,
                        cos(angle) * thisRingRadius
                );

                v1 = yAxle.multiply(v1.y).add(xAxle.multiply(v1.x)).add(zAxle.multiply(v1.z));
                v2 = yAxle.multiply(v2.y).add(xAxle.multiply(v2.x)).add(zAxle.multiply(v2.z));
                v3 = yAxle.multiply(v3.y).add(xAxle.multiply(v3.x)).add(zAxle.multiply(v3.z));
                v4 = yAxle.multiply(v4.y).add(xAxle.multiply(v4.x)).add(zAxle.multiply(v4.z));



                double U1 = ((angle - roll) / PI) / 2;
                double U2 = ((angle - roll) / PI) / 2 + (0.5 / res);

                double V1 = angle2 / PI;
                double V2 = nextAngle2 / PI;



                if(flipNormals) {
                    addVertexTextureNormal(v4.subtract(displacement), U2, V1, v4, matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v3.subtract(displacement), U2, V2, v3, matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v2.subtract(displacement), U1, V2, v2, matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v1.subtract(displacement), U1, V1, v1, matrix4f2, bufferBuilder);
                } else {
                    addVertexTextureNormal(v1.subtract(displacement), U1, V1, v1, matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v2.subtract(displacement), U1, V2, v2, matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v3.subtract(displacement), U2, V2, v3, matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v4.subtract(displacement), U2, V1, v4, matrix4f2, bufferBuilder);
                }
            }
        }
    }



    public static void buildRing(float innerRadius, float outerRadius, int segments,
                                 Vec3d center, Camera camera, Matrix4f matrix4f2, BufferBuilder bufferBuilder) {
        Vec3d displacement = camera.getPos().subtract(center);
        double twoPi = Math.PI * 2;
        for (int i = 0; i < segments; i++) {
            double angle1 = twoPi * i / segments;
            double angle2 = twoPi * (i + 1) / segments;


            Vec3d inner1 = new Vec3d(Math.cos(angle1) * innerRadius, 0, Math.sin(angle1) * innerRadius);
            Vec3d inner2 = new Vec3d(Math.cos(angle2) * innerRadius, 0, Math.sin(angle2) * innerRadius);

            Vec3d outer1 = new Vec3d(Math.cos(angle1) * outerRadius, 0, Math.sin(angle1) * outerRadius);
            Vec3d outer2 = new Vec3d(Math.cos(angle2) * outerRadius, 0, Math.sin(angle2) * outerRadius);


            Vec3d v1 = inner1.subtract(displacement);
            Vec3d v2 = outer1.subtract(displacement);
            Vec3d v3 = outer2.subtract(displacement);
            Vec3d v4 = inner2.subtract(displacement);


            float u1 = (float) i / segments;
            float u2 = (float) (i + 1) / segments;

            Vec3d normal = new Vec3d(0, 1, 0);


            addVertexTextureNormal(v1, u1, 0.0, normal, matrix4f2, bufferBuilder);
            addVertexTextureNormal(v2, u1, 1.0, normal, matrix4f2, bufferBuilder);
            addVertexTextureNormal(v3, u2, 1.0, normal, matrix4f2, bufferBuilder);
            addVertexTextureNormal(v4, u2, 0.0, normal, matrix4f2, bufferBuilder);
        }
    }




    public static void buildTexturedSphereWithCameraRelativeNormals(int res, float radius, Vec3d center , float roll, boolean flipNormals,  Camera camera, Matrix4f matrix4f2, BufferBuilder bufferBuilder) {
        Vec3d displacement = camera.getPos().subtract(center);
        Vec3d axle = displacement.normalize();

        float angle2 = 0f;
        float nextAngle2;
        float thisRingRadius;
        float nextRingRadius;

        Vec3d yAxle = axle;
        Vec3d xAxle = LazuliMathUtils.rotateAroundAxis(yAxle, new Vec3d(yAxle.x, 0, yAxle.z).normalize().rotateY(90), 90);
        Vec3d zAxle = LazuliMathUtils.rotateAroundAxis(xAxle,yAxle,90).normalize();

        for (int p = 0; p < res; p++) {
            angle2 += (float) (PI / res);
            nextAngle2 = angle2 - (float) (PI / res);
            thisRingRadius = (float) sin(angle2) * radius;
            nextRingRadius = (float) sin(nextAngle2) * radius;
            double thisRingY = cos(angle2) * radius;
            double nextRingY = cos(nextAngle2) * radius;

            float angle = roll;
            for (int i = 0; i < res * 2; i++) {


                Vec3d v1 = new Vec3d(
                        sin(angle) * thisRingRadius,
                        thisRingY,
                        cos(angle) * thisRingRadius
                );

                Vec3d v2 = new Vec3d(
                        sin(angle) * nextRingRadius,
                        nextRingY,
                        cos(angle) * nextRingRadius
                );

                angle += (float) (PI / res);


                Vec3d v3 = new Vec3d(
                        sin(angle) * nextRingRadius,
                        nextRingY,
                        cos(angle) * nextRingRadius
                );

                Vec3d v4 = new Vec3d(
                        sin(angle) * thisRingRadius,
                        thisRingY,
                        cos(angle) * thisRingRadius
                );

                Vec3d v1b = yAxle.multiply(v1.y).add(xAxle.multiply(v1.x)).add(zAxle.multiply(v1.z));
                Vec3d v2b = yAxle.multiply(v2.y).add(xAxle.multiply(v2.x)).add(zAxle.multiply(v2.z));
                Vec3d v3b = yAxle.multiply(v3.y).add(xAxle.multiply(v3.x)).add(zAxle.multiply(v3.z));
                Vec3d v4b = yAxle.multiply(v4.y).add(xAxle.multiply(v4.x)).add(zAxle.multiply(v4.z));


                //Equatorial texture coordinates
                double U1 = ((angle - roll) / PI) / 2;
                double U2 = ((angle - roll) / PI) / 2 + (0.5 / res);
                //Longitudinal texture coordinates
                double V1 = angle2 / PI;
                double V2 = nextAngle2 / PI;

                //actually add the vertexes

                if(flipNormals) {
                    addVertexTextureNormal(v4.subtract(displacement), U2, V1, v4b.normalize(), matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v3.subtract(displacement), U2, V2, v3b.normalize(), matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v2.subtract(displacement), U1, V2, v2b.normalize(), matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v1.subtract(displacement), U1, V1, v1b.normalize(), matrix4f2, bufferBuilder);
                } else {
                    addVertexTextureNormal(v1.subtract(displacement), U1, V1, v1b.normalize(), matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v2.subtract(displacement), U1, V2, v2b.normalize(), matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v3.subtract(displacement), U2, V2, v3b.normalize(), matrix4f2, bufferBuilder);
                    addVertexTextureNormal(v4.subtract(displacement), U2, V1, v4b.normalize(), matrix4f2, bufferBuilder);
                }
            }
        }
    }

    public static void buildRing(int res, float radius1, float radius2, Vec3d center, Vec3d axle, float roll, boolean flipNormals,  Camera camera, Matrix4f matrix4f2, BufferBuilder bufferBuilder) {
        Vec3d displacement = camera.getPos().subtract(center); // Get displacement relative to camera

        Vec3d yAxle = axle;
        Vec3d xAxle = (yAxle.x == 0 && yAxle.z == 0) ? new Vec3d(1, 0, 0) : new Vec3d(yAxle.x, 0, yAxle.z).normalize().rotateY(90);
        Vec3d zAxle = LazuliMathUtils.rotateAroundAxis(xAxle,yAxle,90);


        float angle = roll;
        for (int i = 0; i < res * 2; i++) {

            //Vertex 1
            Vec3d v1 = new Vec3d(
                    sin(angle) * radius1,
                    0,
                    cos(angle) * radius1
            );
            //Vertex 2
            Vec3d v2 = new Vec3d(
                    sin(angle) * radius2,
                    0,
                    cos(angle) * radius2
            );

            angle += (float) (PI / res);

            //Vertex 3
            Vec3d v3 = new Vec3d(
                    sin(angle) * radius2,
                    0,
                    cos(angle) * radius2
            );
            //Vertex 4
            Vec3d v4 = new Vec3d(
                    sin(angle) * radius1,
                    0,
                    cos(angle) * radius1
            );

            v1 = yAxle.multiply(v1.y).add(xAxle.multiply(v1.x)).add(zAxle.multiply(v1.z));
            v2 = yAxle.multiply(v2.y).add(xAxle.multiply(v2.x)).add(zAxle.multiply(v2.z));
            v3 = yAxle.multiply(v3.y).add(xAxle.multiply(v3.x)).add(zAxle.multiply(v3.z));
            v4 = yAxle.multiply(v4.y).add(xAxle.multiply(v4.x)).add(zAxle.multiply(v4.z));


            //Equatorial texture coordinates
            double U1 = ((angle - roll) / PI) / 2;
            double U2 = ((angle - roll) / PI) / 2 + (0.5 / res);
            //Longitudinal texture coordinates
            double V1 = 0;
            double V2 = 1;

            //actually add the vertexes

            if(flipNormals) {
                addVertexTextureNormal(v4.subtract(displacement), U2, V1, v4, matrix4f2, bufferBuilder);
                addVertexTextureNormal(v3.subtract(displacement), U2, V2, v3, matrix4f2, bufferBuilder);
                addVertexTextureNormal(v2.subtract(displacement), U1, V2, v2, matrix4f2, bufferBuilder);
                addVertexTextureNormal(v1.subtract(displacement), U1, V1, v1, matrix4f2, bufferBuilder);
            } else {
                addVertexTextureNormal(v1.subtract(displacement), U1, V1, v1, matrix4f2, bufferBuilder);
                addVertexTextureNormal(v2.subtract(displacement), U1, V2, v2, matrix4f2, bufferBuilder);
                addVertexTextureNormal(v3.subtract(displacement), U2, V2, v3, matrix4f2, bufferBuilder);
                addVertexTextureNormal(v4.subtract(displacement), U2, V1, v4, matrix4f2, bufferBuilder);
            }

        }
    }


    public static void buildSpriteBillboard(float radius, Vec3d center, Camera camera, Matrix4f matrix4f2, BufferBuilder bufferBuilder) {
        Vec3d displacement = camera.getPos().subtract(center);  // Get displacement relative to camera


        // Add the vertices to the buffer
        addVertexTextureNormal(Vec3d.ZERO.subtract(displacement), 1, 1, displacement.normalize(), matrix4f2, bufferBuilder);
        addVertexTextureNormal(Vec3d.ZERO.subtract(displacement), 0, 1, displacement.normalize(), matrix4f2, bufferBuilder);
        addVertexTextureNormal(Vec3d.ZERO.subtract(displacement), 0, 0, displacement.normalize(), matrix4f2, bufferBuilder);
        addVertexTextureNormal(Vec3d.ZERO.subtract(displacement), 1, 0, displacement.normalize(), matrix4f2, bufferBuilder);
    }

    static void addVertexTextureNormal(Vec3d pos, double u, double v, Vec3d normal, Matrix4f matrix4f2, BufferBuilder bufferBuilder) {
        normal = normal.normalize();

        Vec3d pos2 = pos.add(mainDisplacement).rotateZ((float) yaw).rotateX((float) roll).rotateY((float) pitch);
        Vec3d normal2 = normal.rotateZ((float) yaw).rotateX((float) roll).rotateY((float) pitch);

        if (pos2.length()>clampDist) {
            pos2 = pos2.normalize().multiply(clampDist+(0.01*(pos2.length()-clampDist)));
        }
        bufferBuilder.vertex(matrix4f2, (float) pos2.x, (float) pos2.y, (float) pos2.z)
                .texture((float) u, (float) v)
                .color(0, 0, 0, 1)
                .normal((float) normal2.x, (float) normal2.y, (float) normal2.z);
    }

    private static void addVertexTexture(Vec3d pos, double u, double v, Matrix4f matrix4f2, BufferBuilder bufferBuilder) {
        bufferBuilder.vertex(matrix4f2, (float) pos.x, (float) pos.y, (float) pos.z)
                .texture((float) u, (float) v)
                .color(0, 0, 0, 1);
    }

    public static void displaceRenderingSpacePos(Vec3d dis){
        mainDisplacement = mainDisplacement.add(dis);
    }

    public static void setRenderingSpacePos(Vec3d dis){
        mainDisplacement = dis;
    }

    public static void rotatedSpaceDisplaceRenderingSpacePos(Vec3d dis){
        mainDisplacement = mainDisplacement.add(dis.rotateZ((float) -yaw).rotateX((float) -roll).rotateY((float) -pitch));
    }

    public static void displaceRenderingSpaceDir(double pitchDis, double yawDis, double rollDis){
        pitch += pitchDis;
        yaw += yawDis;
        roll += rollDis;
    }

    public static void setRenderingSpaceDir(double pitchDis, double yawDis, double rollDis){
        pitch = pitchDis;
        yaw = yawDis;
        roll = rollDis;
    }
}