package com.example.chickenfall;

public class Ammunition extends Entity {
    private boolean visible = true; //Urcuje ci je dana entita viditelna

    /**
     * Konstruktor triedy Ammunition. Sluzi na zobrazenie
     * spritov nabojov na obrazovke
     *
     * @param bitmapLocation int lokacia spritu v zlozke resources
     */
    public Ammunition(int bitmapLocation) {
        super(bitmapLocation);
    }

    /**
     * Getter pre premennu visible
     * @return  hodnota premennej visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Setter pre premennu visible
     * @param visible parameter na nastavenie hodnoty visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
