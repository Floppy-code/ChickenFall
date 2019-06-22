package com.example.chickenfall;

import android.graphics.Bitmap;

public class Background {
    private Bitmap sprite;
    private int spriteLocation;
    private int posX;
    private int posY;

    public Background(int spriteLocation, int posX, int posY) {
        this.spriteLocation = spriteLocation;
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
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
}
