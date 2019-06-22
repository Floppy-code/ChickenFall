package com.example.chickenfall;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.util.Random;

public class Chicken extends Entity {
    private boolean hit;        //Ukazovatel ci bolo npc trafene
    private boolean direction;  //Smer ktorym npc leti          false - dolava      true - doprava
    private boolean spriteDirection; //Smer otocenia spritu npc false - dolava      true - doprava
    private int screenX;        //X suradnica npc na obrazovke
    private int screenY;        //Y suradnica npc na obrazovke
    private int distanceFromScreen; //"Z suradnica" naznacuje ako daleko je npc od kamery
    private int[] spriteLocationArray;  //Tabulka lokacii spritov v resources, urcena pre animacie
    private Bitmap[] sprites;   //Tabulka bitmap spritov, urcena pre animacie
    private int frame = 0;      //Counter pre pocet snimkov/tikov hry ktore ubehli

    /**
     * Konstruktor pre triedu Chicken, sluzi ako hlavne npc v
     * hre. Nacitava default sprite a sprite urcene pre animacie
     * @param bitmapLocation Lokacia default spritu v resources
     */
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

    /**
     * Stara sa o spravanie npc pocas hry. Ovlada animacie spritov
     * a pohyb NPC
     */
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

    /**
     * Na zaklede toho kolkaty frame v sekunde ide, posle iny
     * sprite. Tymto je mozne docielit efekt animacie.
     * @return
     */
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

    /**
     * Vracia X-suradnicu kde sa nachadza npc oproti lavemu
     * hornemu rohu pozadia
     * @return X-suradnica oproti pozadiu
     */
    public int getScreenX() {
        return screenX;
    }

    /**
     * Nastavuje X suradnicu npc oproti pozadiu
     * @param screenX X-suradnica
     */
    public void setScreenX(int screenX) {
        this.screenX = screenX;
    }

    /**
     * Vracia Y-suradnicu kde sa nachadza npc oproti lavemu
     * hornemu rohu pozadia
     * @return Y-suradnica oproti pozadiu
     */
    public int getScreenY() {
        return screenY;
    }

    /**
     * Nastavuje Y suradnicu npc oproti pozadiu
     * @param screenY Y-suradnica
     */
    public void setScreenY(int screenY) {
        this.screenY = screenY;
    }

    /**
     * Vracia cele pole s cestami spritov v resources
     * @return pole ciest spritov
     */
    public int[] getSpriteLocationArray() {
        return spriteLocationArray;
    }

    /**
     * Vracia pole bitmap vsetkych spritov
     * @return pole bitmap spritov
     */
    public Bitmap[] getSprites() {
        return sprites;
    }

    /**
     * Vracia vzdialenost npc od obrazovky/kamery
     * @return vzdialenost "Z-osy"
     */
    public int getDistanceFromScreen() {
        return distanceFromScreen;
    }

    /**
     * Vracia udaj o tom, ci bolo npc zasiahnute
     * @return zasiahnutie
     */
    public boolean isHit() {
        return hit;
    }

    /**
     * Nastavuje hodnotu zasiahnutia pre dane npc
     * @param hit zasiahnutie npc
     */
    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public boolean isDirection() {
        return direction;
    }

    /**
     * Otoci sprity pre animacie horizontalne, vyuziva sa pri
     * zmene smeru letu.
     */
    public void flipSprite() {
        for (int i = 0; i < sprites.length; i++) {
            Matrix matrix = new Matrix();
            matrix.preScale(-1.0f, 1.0f);
            sprites[i] = Bitmap.createBitmap(sprites[i], 0, 0, sprites[i].getWidth(), sprites[i].getHeight(), matrix, true);
        }
    }

    /**
     * Otoci sprite vlavo alebo vpravo okolo horizontalnej osi
     * @param o smer otocenia  false - vlavo, true - vpravo
     */
    public void setSpriteOrientation(boolean o) {
        //TRUE - doprava
        if(spriteDirection != o) {
            this.flipSprite();
            spriteDirection = !spriteDirection;
        }
    }

    /***
     * Nastavuje smer ktorym npc v dany tick leti
     * @param direction smer letu   true - vlavo
     */
    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    /***
     * Nastavuje vzdialenost npc od obrazovky/kamery
     * @param distanceFromScreen vzdialenost v metroch
     */
    public void setDistanceFromScreen(int distanceFromScreen) {
        this.distanceFromScreen = distanceFromScreen;
    }
}
