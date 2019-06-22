package com.example.chickenfall;

import android.graphics.Canvas;
import android.os.Build;

import java.util.ArrayList;
import java.util.Random;

/*
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            TODO LIST
    - Timer do konca hry            DONE
    - Pocitanie nabojov v zbrani    DONE
    - Reloadovanie zbrane (aj zvuk) DONE
    - Pocitanie skore               DONE
    - Menu                          DONE
    - Vysledna screen               DONE
    - Fixnut otacanie kuriat        DONE
    - Fixnut skalovanie menu        DONE
    - Crash fix
    - Stringy ulozit do string.xml

XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 */

public class GameThread extends Thread {

    private GameView view;          //Zakladny view aplikacie
    private boolean run = false;    //Premenna pouzivana pre hlavny while loop hry

    public static final int CHICKEN_COUNT = 8;      //Najvyssi pocet npc ktore mozu byt sucastne na obrazovke
    public static final int ENTITY_SPRITE_COUNT = 1;//Urcuje pocet spritov nahodne generovanych entit na mape
    public static final int ENTITY_GRASS_COUNT = 5; //Urcuje pocet Entit s texturov travy vykreslenych na mape
    public static final int SHOT_WAIT_TIME = 80;    //Pocet tikov pocas ktorych nebude mozne vystrelit po predchadzajucom vystrele
    public static final int RELOAD_WAIT_TIME = 160; //Pocet tikov pocas ktorych nebude mozne vystrelit po nabiti zbrane
    public static final int AMMO_COUNT = 6;         //Pocet nabojov v zbrani
    public static final int SCORE_PER_CHICKEN = 25; //Skore pridane za zasah jedneho NPC
    public static final int DEFAULT_GAME_TIME = 90; //Pocet sekund pocas ktorych je mozne hrat

    private Background levelBackground = null;      //Pozadie hry, "mapa"
    private ArrayList<Chicken> npcChickenList = null;   //Zoznam prave spawnutych NPC
    private ArrayList<Entity> entityList = null;    //Zoznam existujucich entit (trava, stromy, atd.)
    private Ammunition[] ammunition;                //Pole "nabojov" vykreslenych na obrazovku

    private int[] entitySpriteLocation;     //Pole pre lokacie nahodne vybranych spritov entit
    private int shotFrameCounter = 2000;    //Pocitadlo framov/tikov pocas ktorych nedoslo k vystrelu

    private int timerFrameCounter = 0;      //Pocitadlo tikov pre cas hry (60 tikov - 1 sekunda)
    private int gameTime = DEFAULT_GAME_TIME;   //Zostavajuci cas do konca hry

    private int unusedAmmo = AMMO_COUNT;    //Pocet nevystrelenych nabojov zbrane
    private int score = 0;                  //Skore hraca v danej hre

    private boolean showMenu = true;        //Premenna ktora urcuje ci sa ma zobrazovat menu
    private boolean endgame = false;        //Premenna ktora urcuje ci sa ma zobrazovat skore tabulka

    //SOUND
    private Sound gunShot;  //Prehravac zvuku vystrelu
    private Sound reload;   //Prehravac zvuku nabitia zbrane

    //CONTROL
    private ScreenButton leftArrow = null;  //Tlacidlo posunu obrazovky dolava
    private ScreenButton rightArrow = null; //Tlacidlo posunu obrazovky doprava
    private ScreenButton reloadAmmunition = null;   //Tlacidlo pre nabite zbrane

    /**
     * Konstruktor pre hlavne vlakno hry. Stara sa o hlavne funkcionality hry ako je timer, spawnovanie npc,
     * spawnovanie entity a ich manazment.
     * @param view pohlad aplikacie
     */
    public GameThread(GameView view) {
        this.view = view;
        this.npcChickenList = new ArrayList<Chicken>();
        this.entityList = new ArrayList<Entity>();
        this.ammunition = new Ammunition[AMMO_COUNT];

        this.gunShot = new Sound(view.getContext(), R.raw.shot);
        this.reload = new Sound(view.getContext(), R.raw.reload);

        this.levelBackground = new Background(R.mipmap.background_hillside, 0, 0);

        this.entitySpriteLocation = new int[ENTITY_SPRITE_COUNT];
        this.entitySpriteLocation[0] = R.mipmap.entity_treestump;

        this.leftArrow = new ScreenButton(R.mipmap.arrow_l, 20, 900);
        this.rightArrow = new ScreenButton(R.mipmap.arrow_r, 1700, 900);
        this.reloadAmmunition = new ScreenButton(R.mipmap.reload, 1395, 5);

        int ammoLocationX = 1470;
        for (int i = 0; i < AMMO_COUNT; i++) {
            Ammunition ammo = new Ammunition(R.mipmap.shotgun_shell);
            ammo.setAbsY(0);
            ammo.setAbsX(ammoLocationX);
            ammoLocationX += 75;
            this.ammunition[i] = ammo;
        }
    }

    /**
     * Vracia pole so spritami pre ukazovatel municie
     * @return pole spritov
     */
    public Ammunition[] getAmmunition() {
        return ammunition;
    }

    /**
     * Vracia objekt s pozadim mapy/hry
     * @return background entity
     */
    public Background getLevelBackground() {
        return levelBackground;
    }

    /**
     * Vracia zoznam s npc
     * @return zoznam npc
     */
    public ArrayList<Chicken> getNpcChickenList() {
        return npcChickenList;
    }

    /**
     * Vracia tlacidlo pre nabite zbrane
     * @return screenButton nabitia
     */
    public ScreenButton getReloadAmmunition() {
        return reloadAmmunition;
    }

    /**
     * Vracia tlacidlo pre posun obrazu dolava
     * @return screenButton posunu vlavo
     */
    public ScreenButton getLeftArrow() {
        return leftArrow;
    }

    /**
     * Vracia tlacidlo pre posun obrazu doprava
     * @return screenButton posunu vpravo
     */
    public ScreenButton getRightArrow() {
        return rightArrow;
    }

    /**
     * Sluzi na kontrolu ci skoncila hra
     * @return koniec hry
     */
    public boolean isEndgame() {
        return endgame;
    }

    /**
     * Sluzi na kontrolu ci je teraz nutne vykreslovat menu
     * @return vykreslenie menu
     */
    public boolean isShowMenu() {
        return showMenu;
    }

    /**
     * Vracia skore ktore hrac nahral v poslednom kole
     * @return skore hraca
     */
    public int getScore() {
        return score;
    }

    /**
     * Nastavi hlavnu premennu pre while loop hry, povoluje alebo zakazuje priebeh hry
     * @param state parameter pre hru
     */
    public void setRun(boolean state) {
        this.run = state;
    }

    /**
     * Vracia zoznam entit ktore sluzia napr. na zobrazenie travy, stromov v skratke vsetkych
     * objektov ktore niesu pozadie, npc alebo prvok GUI
     * @return
     */
    public ArrayList<Entity> getEntityList() {
        return entityList;
    }

    /**
     * Vrati momentalny cas ktory ostava do konca hry
     * @return cas do konca hry
     */
    public int getGameTime() {
        return gameTime;
    }

    /**
     * Nastavi pocet pouzitelnych nabojov na povodny maximalny pocet, resetuje
     * entity nabojov pre GUI na visible
     */
    private void reloadGun() {
        this.unusedAmmo = AMMO_COUNT;
        this.reload.play();
        for (int i = 0; i < AMMO_COUNT; i++) {
            this.ammunition[i].setVisible(true);
        }
    }

    /**
     * Kontroluje ci na suradniciach ktore ma dana entita je momentalne dotyk.
     * @param btn entita na kontrolu
     * @return entita je oznacena / neoznacena
     */
    private boolean checkButtonPress(Entity btn) {
        int buttonSpriteWidth = btn.getSprite().getWidth();
        int buttonSpriteHeight = btn.getSprite().getHeight();

        if(view.getTouchX() >= btn.getAbsX() && view.getTouchX() <= btn.getAbsX() + buttonSpriteWidth && view.isTouch()) {    //LEFT ARROW
            if(view.getTouchY() <= btn.getAbsY() + buttonSpriteHeight && view.getTouchY() >= btn.getAbsY()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Kontroluje ci nejaky z hlabnych prvkov GUI nebol stlaceny
     * 0 - nic  1 - dolava  2 - doprava  3 - reload
     * @return ktore tlacidlo bolo stlacene
     */
    private int checkButtonPress() {
        if (this.checkButtonPress(this.leftArrow)) { return 1; }
        if (this.checkButtonPress(this.rightArrow)) { return 2; }
        if (this.checkButtonPress(this.reloadAmmunition)) { return 3; }
        return 0;
    }

    /**
     * Otaca kameru dolava/doprava podla vstupu.
     * Otacanie kamery je docielene pomocou zmeny suradnic entit oproti suradniciam kamery.
     * Napr. pohyb kamery doprava znamena dekrement od suradnic entit
     * Suradnicovy system kamery:
     * 0,0 ------------------------------------------------->X 1920, 0
     * |
     * |
     * |
     * |
     * Y 0, 1080
     * @param direction
     */
    private void moveScreen(int direction) {
        int cameraMoveSpeed = 8;

        if (direction == 1) {
            levelBackground.setPosX(levelBackground.getPosX() + cameraMoveSpeed);
            for (Chicken chicken : npcChickenList) {
                chicken.setAbsX(chicken.getAbsX() + cameraMoveSpeed);
            }
            for (Entity entity : this.entityList) {
                entity.setAbsX(entity.getAbsX() + cameraMoveSpeed);
            }
        } else {
            levelBackground.setPosX(levelBackground.getPosX() - cameraMoveSpeed);
            for (Chicken chicken : npcChickenList) {
                chicken.setAbsX(chicken.getAbsX() - cameraMoveSpeed);
            }
            for (Entity entity : this.entityList) {
                entity.setAbsX(entity.getAbsX() - cameraMoveSpeed);
            }
        }
    }

    /**
     * Na nahodne pozicie na mape vygeneruje zadany pocet npc. Nahodne generovane su X, Y suradnice
     * a taktiez aj vzdialenost od kamery.
     * @param count pocet ktory bude vygenerovany
     */
    private void spawnChickens(int count) {
        int upperLimitY = 600;
        int lowerLimitY = 50;
        int upperLimitX = 3790;
        int lowerLimitX = 50;
        int upperLimitDist = 160;
        int lowerLimitDist = -120;

        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            npcChickenList.add(new Chicken(R.mipmap.entity_chicken));
        }
        for (Chicken chicken : npcChickenList) {
            if(chicken.getAbsX() == 0 || chicken.getAbsY() == 0) {
                int randomX = rand.nextInt(upperLimitX - lowerLimitX) + lowerLimitX;
                int randomY = rand.nextInt(upperLimitY - lowerLimitY) + lowerLimitY;
                chicken.setAbsX(randomX + this.levelBackground.getPosX());
                chicken.setAbsY(randomY);
                chicken.setScreenX(chicken.getAbsX());
                chicken.setScreenY(chicken.getAbsY());
                chicken.setDirection(rand.nextBoolean());
                chicken.setDistanceFromScreen((rand.nextInt(upperLimitDist) + lowerLimitDist));
            }
        }
        int i, key, j;
        for (i = 1; i < npcChickenList.size(); i++)
        {
            key = npcChickenList.get(i).getDistanceFromScreen();
            j = i - 1;

            while (j >= 0 && npcChickenList.get(j).getDistanceFromScreen() < key)
            {
                npcChickenList.get(j + 1).setDistanceFromScreen(npcChickenList.get(j).getDistanceFromScreen());
                j = j - 1;
            }
            npcChickenList.get(j + 1).setDistanceFromScreen(key);
        }
    }

    /**
     * "Vymaze" npc ktore su momentalne vygenerovane na mape. Tato metoda sluzi najma na
     * vycistenie mapy po konci hry
     */
    private void despawnChickens() {
        int count = this.npcChickenList.size();
        for (int i = 0; i < count; i++) {
            this.npcChickenList.remove(0);
        }
    }

    /**
     * Vygeneruje entity travy a na nahodne suradnice X a Y vygeneruje doplnkove entity ako su
     * stromy, ploty, a dalsie.
     * @param count pocet nahodnych entit na vygenerovanie
     */
    private void spawnEntities(int count) {
        int upperLimitY = 860;
        int lowerLimitY = 770;
        int upperLimitX = 3840;
        int lowerLimitX = 0;

        Random rand = new Random();

        int grassX = -10;
        for(int i = 0; i < ENTITY_GRASS_COUNT; i++) {
            Entity entity;
            entity = new Entity(R.mipmap.entity_grass3opti2);
            entity.setAbsY(rand.nextInt(100) + 700);
            entity.setAbsX(grassX);
            grassX += 720;
            this.entityList.add(entity);
        }

        for (int i = 0; i < count; i++) {  //Random pre trunky atd
            Entity entity = new Entity(entitySpriteLocation[rand.nextInt(ENTITY_SPRITE_COUNT)]);
            entity.setAbsX(rand.nextInt(upperLimitX - lowerLimitX) + lowerLimitX);
            entity.setAbsY(rand.nextInt(upperLimitY - lowerLimitY) + lowerLimitY);
            this.entityList.add(entity);
        }
    }

    /**
     * "Vymaze" vsetky vygenerovane entity.
     */
    private void despawnEntities() {
        int count = this.entityList.size();
        for (int i = 0; i < count; i++) {
            this.entityList.remove(0);
        }
    }

    /**
     * Tato metoda sa stara o hlavny beh hry, kontroluje cas, ovlada pohyb npc a vyvolava metody
     * na manazment hernej plochy. T.j. otacanie kamery, kontrola municie, ovladanie npc,
     * despawn zostrelenych npc a zvuky vystrelu/nabitia zbrane.
     */
    private void tick() {
        int leftBorderLimit = 0;        //Lava hranica pre obraz
        int rightBorderLimit = -1920;   //Prava hranica pre obraz

        //Prechod na scoreboard ak cas je 0
        if(this.gameTime == 0) {
            this.endgame = true;
            this.showMenu = true;
        }

        //POSUN OBRAZU
        if(this.checkButtonPress(this.leftArrow)) {
            this.moveScreen(1);
        } else if(this.checkButtonPress(this.rightArrow)) {
            this.moveScreen(2);
        } else if(this.checkButtonPress(this.reloadAmmunition)) {
            this.reloadGun();
        }

        //Kontrola aby kamera nesla za hranice pozadia
        if (levelBackground.getPosX() > leftBorderLimit) {
            this.moveScreen(2);
        } else if (levelBackground.getPosX() < rightBorderLimit) {
            this.moveScreen(1);
        }

        //POHYB A TRAFENIE SLIEPOK
        for(Chicken chicken : npcChickenList) {
            if (chicken.getScreenX() < -230) {
                chicken.setDirection(true);
                chicken.setSpriteOrientation(true);
            }
            if (chicken.getScreenX() > 3840) {
                chicken.setDirection(false);
                chicken.setSpriteOrientation(false);
            }
            if (shotFrameCounter >= SHOT_WAIT_TIME) {
                if (this.checkButtonPress(chicken)) {
                    if (this.unusedAmmo != 0) {
                        chicken.setHit(true);
                    }
                }
            }
            chicken.tick();
        }

        //VYSTREL
        if (this.checkButtonPress() == 0 && this.view.isTouch() && shotFrameCounter >= SHOT_WAIT_TIME && this.unusedAmmo != 0) {
            this.gunShot.play();
            shotFrameCounter = 0;

            this.unusedAmmo -= 1;
            Ammunition lastUnused = null;
            for(int i = 0; i < AMMO_COUNT; i++) {
                if(ammunition[i].isVisible()) {
                    lastUnused = ammunition[i];
                }
            }
            try {
                lastUnused.setVisible(false);
            } catch (NullPointerException e) {
                System.out.println("Ammo sprite nullptr exception!");
            }
        }

        //Delete zostrelenych sliepok
        for (int i = 0; i < npcChickenList.size(); ) {
            if(npcChickenList.get(i).isHit() && npcChickenList.get(i).getAbsY() > 1080) {
                npcChickenList.remove(i);
                this.score += SCORE_PER_CHICKEN;
            } else {
                i++;
            }
        }

        //RESPAWN SLIEPOK
        if(npcChickenList.size() < GameThread.CHICKEN_COUNT) {
            spawnChickens(1);
            view.reloadNullSprites();
        }

        //KONTROLA CASU DO KONCA
        if (this.timerFrameCounter == 60) {
            this.gameTime--;
            this.timerFrameCounter = 0;
        } else {
            this.timerFrameCounter++;
        }

        this.shotFrameCounter++;
    }

    /**
     * Beh hry pocas toho ako sa zobrazuje menu, kontroluje ci neboli tlacidla stlacene.
     */
    private void tickMenu() {
        if(view.getTouchX() >= 565 && view.getTouchX() <= 565 + 772 && view.isTouch()) {    //LEFT ARROW
            if(view.getTouchY() <= 400 + 128 && view.getTouchY() >= 400) {
                this.showMenu = false;
            }
        }
    }

    /**
     * Beh hry pocas toho ako sa zobrazuje skore, po stlacene "Replay Game" nastavuje
     * vsetky potrebne atributy pre opakovany beh hry.
     */
    private void tickScore() {
        if(view.getTouchX() >= 544 && view.getTouchX() <= 544 + 830 && view.isTouch()) {    //LEFT ARROW
            if(view.getTouchY() <= 508 + 128 && view.getTouchY() >= 508) {
                this.endgame = false;
                this.showMenu = false;
                this.score = 0;
                this.run = true;
                this.unusedAmmo = AMMO_COUNT;
                this.gameTime = DEFAULT_GAME_TIME;

                this.despawnChickens();
                this.spawnChickens(GameThread.CHICKEN_COUNT);

                this.despawnEntities();
                this.spawnEntities(2);

                this.view.loadSprites();

                for(int i = 0; i < AMMO_COUNT; i++) {
                    this.ammunition[i].setVisible(true);
                }
            }
        }
    }

    /**
     * Metoda ktora riadi prve spustenie hry, v ktorej prebieha hlavny while loop
     * hry a ktora sa stara o zamknutie canvas, poslanie canvas do GameView a po
     * vykresleni na cavas ho posiela na obrazovku.
     */
    @Override
    public void run() {
        super.run();
        long lastTime = System.nanoTime();
        int frames = 0;

        this.spawnChickens(GameThread.CHICKEN_COUNT);
        this.spawnEntities(2);
        view.loadSprites();

        while(run) {
            Canvas c = null;

            //Tick hry
            if(this.showMenu) {
                if (this.endgame) {
                    this.tickScore();
                } else {
                    this.tickMenu();
                }
            } else {
                try {
                    this.tick();
                } catch (NullPointerException e) {
                    System.out.println("Nullptr exception - entities are not ready yet!");
                }
            }

            //Vykreslovanie
            try {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {    //API kontrola
                    c = view.getHolder().lockHardwareCanvas();
                } else {
                    c = view.getHolder().lockCanvas();
                }
                synchronized (view.getHolder()) {
                    view.draw(c);
                }
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }

            //FPS Counter
            frames++;
            if (System.nanoTime() - lastTime >= 1000000000L) {
                System.out.println("**FPS: " + frames);
                frames = 0;
                lastTime = System.nanoTime();
            }
        }
        this.gunShot.release();
        this.reload.release();
    }
}
