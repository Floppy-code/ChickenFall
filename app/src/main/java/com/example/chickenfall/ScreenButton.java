package com.example.chickenfall;

public class ScreenButton extends Entity {

    public ScreenButton(int bitmapLocation, int locationX, int locationY) {
        super(bitmapLocation);
        this.setAbsX(locationX);
        this.setAbsY(locationY);
    }
}
