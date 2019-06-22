package com.example.chickenfall;

import android.graphics.Bitmap;

public class Entity {
    private int absX;
    private int absY;
    private int spriteLocation;
    private Bitmap sprite;
    private boolean visible;

    public Entity(int bitmapLocation) {
        this.spriteLocation = bitmapLocation;
        visible = true;
        this.absX = 0;
        this.absY = 0;
    }

    public int getAbsX() {
        return absX;
    }

    public void setAbsX(int absX) {
        this.absX = absX;
    }

    public int getAbsY() {
        return absY;
    }

    public void setAbsY(int absY) {
        this.absY = absY;
    }

    public int getSpriteLocation() {
        return spriteLocation;
    }

    public Bitmap getSprite() {
        return sprite;
    }

    public void setSprite(Bitmap sprite) {
        this.sprite = sprite;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "absX=" + absX +
                ", absY=" + absY +
                '}';
    }
}
