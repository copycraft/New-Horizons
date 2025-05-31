package org.copycraftDev.new_horizons.lazuli_snnipets;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.NewHorizonsMain;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LazuliShaderRegistry {

    private static final Map<String, ShaderProgram> SHADER_MAP = new HashMap<>();
    private static final Map<String, PostEffectProcessor> POST_PROCESSOR_MAP = new HashMap<>();

    private static Window window;
    private static int resX ;
    private static int resY;

    /**
     * Registers a core shader and stores it in SHADER_MAP.
     */
    public static void registerShader(String name, VertexFormat format) {
        Identifier shaderId = NewHorizonsMain.id(name);

        CoreShaderRegistrationCallback.EVENT.register(ctx -> {
            ctx.register(shaderId, format, shaderProgram -> {
                SHADER_MAP.put(name, shaderProgram);
                System.out.println("[NewHorizons] Shader '" + name + "' registered!");
            });
        });
    }

    /**
     * Registers a post-processing shader using the LazuliPostProcessingRegistry.
     * This will defer shader creation until loadPrograms is called.
     */
    public static void registerPostProcessingShader(String name) {

        System.out.println("Trying to register " + name + " <============================================================================================");

        LazuliPostProcessingRegistry.register((client, factory) -> {
            Identifier shaderId = NewHorizonsMain.id(name);
            Framebuffer framebuffer = client.getFramebuffer();

            try {
                PostEffectProcessor processor = new PostEffectProcessor(
                        client.getTextureManager(),
                        factory,
                        framebuffer,
                        shaderId
                );
                processor.setupDimensions(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());


                POST_PROCESSOR_MAP.put(name, processor);
                System.out.println("[NewHorizons] Post-processing shader '" + name + "' registered in callback.");

            } catch (IOException e) {
                System.err.println("[NewHorizons] Failed to load post-processing shader: " + name);
            }
        });

    }

    public static void register(){
        ClientTickEvents.START_CLIENT_TICK.register((t) ->{

            Window window = MinecraftClient.getInstance().getWindow();

            if (resY != window.getFramebufferHeight() || resX != window.getFramebufferWidth()) {
                windowResized(window.getFramebufferHeight(), window.getFramebufferWidth());
            }
            resX = window.getFramebufferWidth();
            resY = window.getFramebufferHeight();

        });

    }

    private static void windowResized(int height, int width) {
        for (Map.Entry<String, PostEffectProcessor> entry : POST_PROCESSOR_MAP.entrySet()) {
            PostEffectProcessor processor = entry.getValue();
            if (processor != null) {
                processor.setupDimensions(width, height);
                System.out.println("[NewHorizons] Resized post-processor: " + entry.getKey() +
                        " to " + width + "x" + height);
            }
        }
    }



    public static ShaderProgram getShader(String name) {
        return SHADER_MAP.get(name);
    }

    public static PostEffectProcessor getPostProcessor(String name) {
        return POST_PROCESSOR_MAP.get(name);
    }
}
