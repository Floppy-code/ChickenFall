package com.example.chickenfall;

import android.graphics.Bitmap;

public class Background {
    private Bitmap sprite;  //Sprite pozadia vo forme bitmapy
    private int spriteLocation; //Lokacia bitmapy v resources
    private int posX;   //X suradnica polohy pozadia
    private int posY;   //Y suradnica polohy pozadia

    /**
     * Konstruktor triedy background, sluzi na vykreslovania
     * pozadia pocas hry, menu alebo skore tabulky.
     * @param spriteLocation lokacia spritu v resources
     * @param posX pociatocna poloha pre X suradnicu
     * @param posY pociatocna poloha pre Y suradnicu
     */
    public Background(int spriteLocation, int posX, int posY) {
        this.spriteLocation = spriteLocation;
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * @return vracia X suradnicu polohy pozadia
     */
    public int getPosX() {
        return posX;
    }

    /**
     * @return vracia Y suradnicu polohy pozadia
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Nastavi X suradnicu polohy pozadia
     * @param posX int X-suradnica
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Nastavi Y suradnicu polohy pozadia
     * @param posY int Y-suradnica
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * Vracia cestu k spritu v resources
     * @return cesta k spritu
     */
    public int getSpriteLocation() {
        return spriteLocation;
    }

    /**
     * Vracia bitmapu spritu pozadia
     * @return bitmap sprite
     */
    public Bitmap getSprite() {
        return sprite;
    }

    /**
     * Nastavi bitmapu spritu pozadia
     * @param sprite bitmap sprite
     */
    public void setSprite(Bitmap sprite) {
        this.sprite = sprite;
    }
}
