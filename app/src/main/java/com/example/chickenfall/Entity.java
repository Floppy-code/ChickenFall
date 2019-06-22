package com.example.chickenfall;

import android.graphics.Bitmap;

public class Entity {
    private int absX;   //X-suradnica polohy danej Entity oproti kamere
    private int absY;   //Y-suradnica polohy danej Entity oproti kamere
    private int spriteLocation; //Cesta k spritu entity v resources
    private Bitmap sprite;  //Bitmapa spritu entity

    /**
     * Konstruktor pre Entitu, kazdy objekt vykreslovany na obrazovku je
     * potomkom entity.
     * @param bitmapLocation cesta k spritu entity v resoures
     */
    public Entity(int bitmapLocation) {
        this.spriteLocation = bitmapLocation;
        this.absX = 0;
        this.absY = 0;
    }

    /**
     * Vrati hodnotu X-suradnice entity vzhladom ku lokacii kamery
     * @return X-suradnica entity
     */
    public int getAbsX() {
        return absX;
    }

    /**
     * Nastavi hodnotu X-suradnice entity vzhladom ku lokacii kamery
     * @param absX X-suradnica entity vzhladom na kameru
     */
    public void setAbsX(int absX) {
        this.absX = absX;
    }

    /**
     * Vrati hodnotu Y-suradnice entity vzhladom ku lokacii kamery
     * @return Y-suradnica entity
     */
    public int getAbsY() {
        return absY;
    }

    /**
     * Nastavi hodnotu Y-suradnice entity vzhladom ku lokacii kamery
     * @param absY Y-suradnica entity vzhladom na kameru
     */
    public void setAbsY(int absY) {
        this.absY = absY;
    }

    /**
     * Vracia lokaciu spritu v resources
     * @return cesta k spritu
     */
    public int getSpriteLocation() {
        return spriteLocation;
    }

    /**
     * Vracia sprite danej entity vo forme bitmapy
     * @return bitmapa spritu entity
     */
    public Bitmap getSprite() {
        return sprite;
    }

    /**
     * Nastavuje bitmapu spritu pre Entitu
     * @param sprite sprite entity vo forme bitmapy
     */
    public void setSprite(Bitmap sprite) {
        this.sprite = sprite;
    }
}
