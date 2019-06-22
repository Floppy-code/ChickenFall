package com.example.chickenfall;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.util.Random;

public class Chicken extends Entity {
    private boolean hit;
    private boolean direction; //false - dolava      true - doprava
    private boolean spriteDirection; //false - dolava      true - doprava
    private int screenX;
    private int screenY;
    private int distanceFromScreen;
    private int[] spriteLocationArray;
    private Bitmap[] sprites;
    private int frame = 0;

    public Chicken(int bitmapLocation) {
        super(bitmapLocation);
        this.hit = false;
        this.spriteLocationArray = new int[4];
        this.sprites = new Bitmap[4];
        this.spriteLocationArray[0] = R.mipmap.entity_chicken_m1;
        this.spriteLocationArray[1] = R.mipmap.entity_chicken_m2;
        this.spriteLocationArray[2] = R.mipmap.entity_chicken_m3;
        this.spriteLocationArray[3] = R.mipmap.entity_chicken_m4;

        Random rand = new Random();
        frame = rand.nextInt(60);
    }

    public void tick() {
        int chickenSpeed = 4;
        if(hit) {
            if(direction) {
                this.setAbsY(this.getAbsY() + 15);
                this.setAbsX(this.getAbsX() + (chickenSpeed - (distanceFromScreen / 50)));
                this.screenY = (this.getScreenY() + 15);
                this.screenX = (this.getScreenX() + (chickenSpeed - (distanceFromScreen / 50)));
            } else {
                this.setAbsY(this.getAbsY() + 15);
                this.setAbsX(this.getAbsX() - (chickenSpeed - (distanceFromScreen / 50)));
                this.screenY = (this.getScreenY() + 15);
                this.screenX = (this.getScreenX() - (chickenSpeed - (distanceFromScreen / 50)));
            }
        } else {
            if(direction) {
                this.setAbsX(this.getAbsX() + (chickenSpeed - (distanceFromScreen / 50)));
                this.screenX = (this.screenX + (chickenSpeed - (distanceFromScreen / 50)));
            } else {
                this.setAbsX(this.getAbsX() - (chickenSpeed - (distanceFromScreen / 50)));
                this.screenX = (this.screenX - (chickenSpeed - (distanceFromScreen / 50)));
            }
        }
        frame++;
        frame = frame % 60;
    }

    @Override
    public Bitmap getSprite() {
        if (frame >= 0 && frame < 8) {
            return sprites[0];
        } else if (frame >= 8 && frame < 17) {
            return sprites[1];
        } else if (frame >= 17 && frame < 25) {
            return sprites[2];
        } else if (frame >= 25 && frame < 32) {
            return sprites[3];
        } else if (frame >= 32 && frame < 40) {
            return sprites[2];
        } else if (frame >= 40 && frame < 48) {
            return sprites[1];
        } else if (frame >= 48 && frame < 60) {
            return sprites[0];
        }
        return sprites[0];
    }

    public int getScreenX() {
        return screenX;
    }

    public void setScreenX(int screenX) {
        this.screenX = screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    public void setScreenY(int screenY) {
        this.screenY = screenY;
    }

    public int[] getSpriteLocationArray() {
        return spriteLocationArray;
    }

    public Bitmap[] getSprites() {
        return sprites;
    }

    public int getDistanceFromScreen() {
        return distanceFromScreen;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public boolean isDirection() {
        return direction;
    }

    public void flipSprite() {
        for (int i = 0; i < sprites.length; i++) {
            Matrix matrix = new Matrix();
            matrix.preScale(-1.0f, 1.0f);
            sprites[i] = Bitmap.createBitmap(sprites[i], 0, 0, sprites[i].getWidth(), sprites[i].getHeight(), matrix, true);
        }
    }

    public void setSpriteOrientation(boolean o) {
        //TRUE - doprava
        if(spriteDirection != o) {
            this.flipSprite();
            spriteDirection = !spriteDirection;
        }
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public void setDistanceFromScreen(int distanceFromScreen) {
        this.distanceFromScreen = distanceFromScreen;
    }
}
