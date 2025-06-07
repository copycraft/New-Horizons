package org.copycraftDev.new_horizons.client;

import java.util.ArrayList;
import java.util.List;

public class InventoryButtonHelper {

    private static final List<Button> buttons = new ArrayList<>();

    static {
        // You can add multiple buttons here
        buttons.add(new Button(150, 60, 20, 20, "!2"));
        buttons.add(new Button(50, 60, 20, 20, "!")); // Example second button
    }

    public static List<Button> getButtons() {
        return buttons;
    }

    public static class Button {
        private final float X;
        private final float Y;
        private final float SX;
        private final float SY;
        private final String text;

        public Button(float X, float Y, float SX, float SY, String text) {
            this.X = X;
            this.Y = Y;
            this.SX = SX;
            this.SY = SY;
            this.text = text;
        }

        public float getX() { return X; }
        public float getY() { return Y; }
        public float getSX() { return SX; }
        public float getSY() { return SY; }
        public String gettext() { return text; }
    }
}
