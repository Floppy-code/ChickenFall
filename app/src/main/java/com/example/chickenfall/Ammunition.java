package com.example.chickenfall;

public class Ammunition extends Entity {
    private boolean visible = true;

    public Ammunition(int bitmapLocation) {
        super(bitmapLocation);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
