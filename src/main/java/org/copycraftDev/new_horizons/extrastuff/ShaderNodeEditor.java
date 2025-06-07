package org.copycraftDev.new_horizons.extrastuff;

import com.google.gson.*;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import net.minecraft.client.MinecraftClient;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * A single‐file node‐based post‐processing shader editor for Veil (Fabric 1.21.1 + Veil + ImGui).
 *
 * HOW IT WORKS:
 * 1. We define a ShaderNode/graph data structure and ImGui‐drawing logic.
 * 2. We Mixin into VeilImGuiImpl.beginFrame() at TAIL (so Veil has already created its ImGui frame).
 * 3. If our “isOpen” flag is true, we draw a floating ImGui window called “Shader Graph”.
 * 4. Inside that window, users can add/drag/edit nodes. Clicking “Export to JSON” writes
 *    `shadergraph_export.json` to the game directory.
 *
 * USAGE:
 * 1. Save as src/main/java/org/copycraftDev/new_horizons/extrastuff/ShaderNodeEditor.java
 * 2. Create a mixin JSON under resources/mixins/ that references:
 *      "org.copycraftDev.new_horizons.extrastuff.ShaderNodeEditor$MixinVeil"
 * 3. In fabric.mod.json, ensure "mixins": ["yourmixins.json"] includes that file.
 * 4. Call ShaderNodeEditor.openEditor() (e.g. via a keybind) to show the window;
 *    call ShaderNodeEditor.closeEditor() to hide.
 * 5. Inside the window, click “Export to JSON” to generate shadergraph_export.json.
 */
public class ShaderNodeEditor {
    //
    // ==== 1) CONFIGURABLE CONSTANTS ====
    private static final float NODE_WIDTH  = 220f;
    private static final float NODE_HEIGHT = 120f;
    private static final float SLIDER_MIN  = 0f;
    private static final float SLIDER_MAX  = 10f;

    //
    // ==== 2) “Open” FLAG & PUBLIC API ====
    public static boolean isOpen = false;

    /** Call this to open the editor. **/
    public static void openEditor() {
        isOpen = true;
    }

    /** Call this to close the editor. **/
    public static void closeEditor() {
        isOpen = false;
    }

    /** Toggles open/closed. **/
    public static void toggleEditor() {
        isOpen = !isOpen;
    }

    //
    // ==== 3) GRAPH DATA STRUCTURES ====
    public static class ShaderNode {
        public final UUID id = UUID.randomUUID();
        public String name;
        public float x, y;
        public final LinkedHashMap<String, Float> parameters;

        public ShaderNode(String name, float x, float y, Map<String, Float> defaultParams) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.parameters = new LinkedHashMap<>(defaultParams);
        }
    }

    /** All nodes in the graph. **/
    private static final List<ShaderNode> nodes = new ArrayList<>();
    /** Ensure we add a default node at first open only. **/
    private static boolean initialized = false;

    //
    // ==== 4) IMGUI DRAWING LOGIC ====
    /**
     * Draws the floating “Shader Graph” window and its contained node‐windows.
     * Must be balanced: each ImGui.begin() → ImGui.end().
     */
    public static void drawShaderGraphWindow() {
        // On first open, add one sample “Gaussian Blur” node if none exist:
        if (!initialized) {
            nodes.add(new ShaderNode("Gaussian Blur", 100f, 100f, Map.of("radius", 5.0f)));
            initialized = true;
        }

        // Begin a floating ImGui window called “Shader Graph”
        ImGui.begin("Shader Graph"); // → must end with ImGui.end()

        // Top‐bar buttons to add nodes, export JSON, or close
        if (ImGui.button("Add Gaussian Blur")) {
            nodes.add(new ShaderNode(
                    "Gaussian Blur",
                    50f + nodes.size() * 25f,
                    50f + nodes.size() * 25f,
                    Map.of("radius", 5.0f)
            ));
        }
        ImGui.sameLine();
        if (ImGui.button("Add Color Adjust")) {
            nodes.add(new ShaderNode(
                    "Color Adjust",
                    50f + nodes.size() * 25f,
                    50f + nodes.size() * 25f,
                    Map.of("brightness", 1.0f, "contrast", 1.0f)
            ));
        }
        ImGui.sameLine();
        if (ImGui.button("Export to JSON")) {
            try {
                exportGraphToJson();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ImGui.sameLine();
        if (ImGui.button("Close")) {
            isOpen = false;
        }

        ImGui.separator();
        ImGui.text("Drag nodes by their titlebars, edit parameters, then export.");

        // Render each node as its own draggable ImGui window
        for (ShaderNode node : nodes) {
            drawSingleNode(node);
        }

        ImGui.end(); // CLOSE “Shader Graph” window
    }

    /**
     * Draw one node: as a separate ImGui window at (node.x, node.y),
     * with a slider for each parameter. We let ImGui handle the native
     * titlebar‐drag, then read back window position each frame.
     */
    private static void drawSingleNode(ShaderNode node) {
        // Set initial position/size only the first time this window appears
        ImGui.setNextWindowPos(node.x, node.y, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(NODE_WIDTH, NODE_HEIGHT, ImGuiCond.FirstUseEver);

        // Use the simple begin(name, flags) overload (flags = 0)
        if (ImGui.begin("Node##" + node.id, /*flags=*/ 0)) {
            // Display node name
            ImGui.text(node.name);

            // One slider per parameter
            for (var entry : node.parameters.entrySet()) {
                String paramName = entry.getKey();
                float[] val = { entry.getValue() };
                // Call sliderFloat with min/max and format (no power argument)
                if (ImGui.sliderFloat(paramName, val, SLIDER_MIN, SLIDER_MAX, "%.2f")) {
                    entry.setValue(val[0]);
                }
            }

            // After ImGui has possibly moved the window (titlebar‐drag),
            // read back the window’s current position so we can store it.
            node.x = ImGui.getWindowPosX();
            node.y = ImGui.getWindowPosY();
        }
        ImGui.end();
    }

    //
    // ==== 5) EXPORT GRAPH TO JSON ====
    /**
     * Serializes the node graph into Veil’s expected post‐processing JSON format:
     * { "passes": [ { "type": "gaussian_blur", "parameters": { "radius": 5.0 } }, … ] }
     * Writes it as “shadergraph_export.json” in the Minecraft run directory.
     */
    private static void exportGraphToJson() throws IOException {
        JsonObject root = new JsonObject();
        JsonArray passes = new JsonArray();

        for (ShaderNode node : nodes) {
            JsonObject nodeJson = new JsonObject();
            // Convert “Gaussian Blur” → “gaussian_blur”
            nodeJson.addProperty("type", toSnakeCase(node.name));

            JsonObject params = new JsonObject();
            for (var e : node.parameters.entrySet()) {
                params.addProperty(e.getKey(), e.getValue());
            }
            nodeJson.add("parameters", params);
            passes.add(nodeJson);
        }
        root.add("passes", passes);

        MinecraftClient client = MinecraftClient.getInstance();
        Path out = Path.of(client.runDirectory.getAbsolutePath(), "shadergraph_export.json");
        try (FileWriter writer = new FileWriter(out.toFile())) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
        }
        System.out.println("[ShaderNodeEditor] Exported shader graph to: " + out);
    }

    // Utility: “Gaussian Blur” → “gaussian_blur”
    private static String toSnakeCase(String input) {
        return input.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}
