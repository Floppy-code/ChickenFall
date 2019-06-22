package com.example.chickenfall;

/**
 * Potomok entity, vyuzivany pre GUI prvky
 */
public class ScreenButton extends Entity {

    /**
     * Konstruktor GUI prvku
     * @param bitmapLocation cesta k bitmape tlacidla
     * @param locationX pozicia pre osu X na obrazovke
     * @param locationY pozicia pre osu Y na obrazovke
     */
    public ScreenButton(int bitmapLocation, int locationX, int locationY) {
        super(bitmapLocation);
        this.setAbsX(locationX);
        this.setAbsY(locationY);
    }
}
