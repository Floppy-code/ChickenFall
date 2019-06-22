package com.example.chickenfall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Trieda GameView vyuziva na vykreslovanie obrazu SurfaceView, v zavislosti od API systemu
 * sa vyuziva Hardwarove alebo Softwarove rendrovanie.
 */
public class GameView extends SurfaceView {

    private SurfaceHolder sHolder;  //Surface Holder aplikacie
    private GameThread gameThread;  //Hlavny thread hry

    private Sound musicPlayer;      //Prehravac hudby pocas hry

    private int displaySizeX;       //Velkost displeju po osi X v pixeloch
    private int displaySizeY;       //Velkost displeju po osi Y v pixeloch

    private double scale;           //Skala alebo "nasobic" velkosti obrazu pre prisposobenie
                                    //roznym rozliseniam

    private int touchX = 0;         //Miesto posledneho dotyku na osi X
    private int touchY = 0;         //Miesto posledneho dotyku na osi Y
    private boolean touch = false;  //Na obrazovke je prave dotyk

    private Background gameBackground = null;   //Pozadie pre hru
    private Background menuBackground = null;   //Pozadie pre menu
    private Background scoreBackground = null;  //Pozadie pre skore


    /**
     * Konstruktor pre GameView, prebera kontext a inicializuje sa v nom Music Player, GameThread,
     * Surface Holder.
     * @param context
     */
    public GameView(Context context) {
        super(context);

        musicPlayer = new Sound(context, R.raw.reaction);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getRealSize(size);
        this.displaySizeX = size.x;
        this.displaySizeY = size.y;

        this.scale = (double) displaySizeY / 1080;

        gameThread = new GameThread(this);

        sHolder = getHolder();
        sHolder.addCallback(new SurfaceHolder.Callback() {
            /**
             * Metoda udava co sa ma diat potom ako je vytvoreny surface
             * @param holder Surface Holder
             */
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                musicPlayer.setRepeat(true);
                musicPlayer.play();

                gameThread.setRun(true);
                gameThread.start();
            }

            /**
             * Metoda udava co sa ma diat potom ako je zmeneny surface
             * @param holder Surface Holder
             */
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            /**
             * Metoda udava co sa ma diat potom ako je zniceny surface
             * @param holder Surface Holder
             */
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameThread.setRun(false);
                while(retry) {
                    try {
                        gameThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        System.out.println("SURFACE DESTRUCTION EXCEPTION");
                    }
                }
                musicPlayer.release();
            }
        });
    }

    /**
     * Metoda kontroluje dotyky na obrazovku a podla toho nastavuje pozicie X a Y spolu s
     * premennou touch.
     * @param event Motion Event na obrazovke
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:   //TAP
                this.touchX = (int)(event.getX() / scale);
                this.touchY = (int)(event.getY() / scale);
                this.touch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                //this.touchX = (int)event.getX();
                //this.touchY = (int)event.getY();
                break;
            case MotionEvent.ACTION_UP:     //LIFT
                this.touch = false;
                break;
            }
        return true;
    }

    /**
     * Vracia poziciu na osi X kde nastal posledny dotyk na obrazovke
     * @return
     */
    public int getTouchX() {
        return touchX;
    }

    /**
     * Vracia poziciu na osi Y kde nastal posledny dotyk na obrazovke
     * @return
     */
    public int getTouchY() {
        return touchY;
    }

    /**
     * Vracia ci sa momentalne vykonava dotyk na obrazovku
     * @return
     */
    public boolean isTouch() {
        return touch;
    }

    /**
     * Zmeni velkost danej bitmapy podla urcenych parametrov.
     * @param source zdroj bitmapy
     * @param width cielova sirka bitmapy v pixeloch
     * @param height cielova vyska bitmapy v pixeloch
     * @return
     */
    private Bitmap changeBitmapSize(Bitmap source, int width, int height) {
        return Bitmap.createScaledBitmap(source, width, height, false);
    }

    /**
     * Nastavi orientaciu spritu pre dane npc
     * @param chicken npc
     * @param orientation smer orientacie spritu
     */
    private void setChickenSpriteOrientation(Chicken chicken, boolean orientation) {
        chicken.setSpriteOrientation(orientation);
    }

    /**
     * Nacita do pamate sprity entit a pozadia hry.
     */
    public void loadSprites() {
        //KONFIGURACIA POZADIA
        try {
            gameBackground = gameThread.getLevelBackground();
            gameBackground.setSprite(BitmapFactory.decodeResource(getResources(), gameBackground.getSpriteLocation()));
            gameBackground.setSprite(changeBitmapSize(gameBackground.getSprite(), (int) (3840 * scale), (int) (1080 * scale)));

            menuBackground = new Background(R.mipmap.menu_background, 0, 0);
            menuBackground.setSprite(BitmapFactory.decodeResource(getResources(), menuBackground.getSpriteLocation()));
            menuBackground.setSprite(changeBitmapSize(menuBackground.getSprite(), (int) (1920 * scale), (int) (1080 * scale)));

            scoreBackground = new Background(R.mipmap.score_board, 0, 0);
            scoreBackground.setSprite(BitmapFactory.decodeResource(getResources(), scoreBackground.getSpriteLocation()));
            scoreBackground.setSprite(changeBitmapSize(scoreBackground.getSprite(), (int) (1920 * scale), (int) (1080 * scale)));
        } catch (NullPointerException e) {
            System.out.println("Nullptr exception - Background sprite not found!");
        }

        //KONFIGURACIA NPC
        try {
            for (Chicken chickenNPC : gameThread.getNpcChickenList()) {
                Bitmap sprite = BitmapFactory.decodeResource(getResources(), chickenNPC.getSpriteLocation());
                sprite = changeBitmapSize(sprite, (int)((230 - chickenNPC.getDistanceFromScreen()) * scale), (int)((150 - chickenNPC.getDistanceFromScreen()) * scale));
                chickenNPC.setSprite(sprite);

                for (int i = 0; i < chickenNPC.getSprites().length; i++) {
                    chickenNPC.getSprites()[i] = BitmapFactory.decodeResource(getResources(), chickenNPC.getSpriteLocationArray()[i]);
                    chickenNPC.getSprites()[i] = changeBitmapSize(chickenNPC.getSprites()[i], (int)((230 - chickenNPC.getDistanceFromScreen()) * scale), (int)((150 - chickenNPC.getDistanceFromScreen()) * scale));
                }
                this.setChickenSpriteOrientation(chickenNPC, chickenNPC.isDirection());
            }
        } catch (NullPointerException e) {
            System.out.println("Nullptr exception while trying to load npc sprites!");
            e.getMessage();
        }

        try {
            //KONFIGURACIA ENTIT
            for (Entity entity : gameThread.getEntityList()) {
                Bitmap bmp = changeBitmapSize(BitmapFactory.decodeResource(getResources(), entity.getSpriteLocation()), (int)(835 * scale), (int)(400 * scale));
                entity.setSprite(bmp);
            }
        } catch (NullPointerException e) {
            System.out.println("Nullptr exception while trying to load entity sprites!");
            e.getMessage();
        }

        //KONFIGURACIA TLACIDIEL
        try {
            gameThread.getLeftArrow().setSprite(BitmapFactory.decodeResource(getResources(), gameThread.getLeftArrow().getSpriteLocation()));
            gameThread.getRightArrow().setSprite(BitmapFactory.decodeResource(getResources(), gameThread.getRightArrow().getSpriteLocation()));
            gameThread.getReloadAmmunition().setSprite(BitmapFactory.decodeResource(getResources(), gameThread.getReloadAmmunition().getSpriteLocation()));
            gameThread.getReloadAmmunition().setSprite(changeBitmapSize(gameThread.getReloadAmmunition().getSprite(), (int) (75 * scale), (int) (75 * scale)));
        } catch (NullPointerException e) {
            System.out.println("Nullptr exception - GUI sprites not found!");
        }

        //KONFIGURACIA NABOJOV
        try {
            Ammunition[] ammo = gameThread.getAmmunition();
            for (int i = 0; i < gameThread.AMMO_COUNT; i++) {
                ammo[i].setSprite(BitmapFactory.decodeResource(getResources(), ammo[i].getSpriteLocation()));
                ammo[i].setSprite(changeBitmapSize(ammo[i].getSprite(), (int) (75 * scale), (int) (75 * scale)));
            }
        } catch (NullPointerException e) {
            System.out.println("Nullptr exception - Ammo sprites not found!");
        }
    }

    /**
     * Nacita sprity len tym entitam ktore maju hodnotu spritov null. Pouzite napr. pri respawne npc.
     */
    public void reloadNullSprites() {
        try {
            if (gameThread.getNpcChickenList().size() == 1) {
                for (Chicken chickenNPC : gameThread.getNpcChickenList()) {
                    Bitmap sprite = BitmapFactory.decodeResource(getResources(), chickenNPC.getSpriteLocation());
                    sprite = changeBitmapSize(sprite, 230 - chickenNPC.getDistanceFromScreen(), 150 - chickenNPC.getDistanceFromScreen());
                    chickenNPC.setSprite(sprite);
                }
            } else {
                for (int i = 0; i < gameThread.getNpcChickenList().size(); i++) {
                    if (gameThread.getNpcChickenList().get(i).getSprite() == null) {
                        Chicken npc = gameThread.getNpcChickenList().get(i);
                        if (i + 1 != gameThread.getNpcChickenList().size()) {
                            npc.setSprite(gameThread.getNpcChickenList().get(i + 1).getSprite());
                        } else {
                            npc.setSprite(gameThread.getNpcChickenList().get(i - 1).getSprite());
                        }
                        for (int j = 0; j < npc.getSprites().length; j++) {
                            npc.getSprites()[j] = BitmapFactory.decodeResource(getResources(), npc.getSpriteLocationArray()[j]);
                            npc.getSprites()[j] = changeBitmapSize(npc.getSprites()[j], (int) ((230 - npc.getDistanceFromScreen()) * scale), (int) ((150 - npc.getDistanceFromScreen()) * scale));
                        }
                        this.setChickenSpriteOrientation(npc, npc.isDirection());
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Nullptr exception while trying to reload null sprites!");
        }
    }

    /**
     * Vykreslenie hlavneho menu hry spolu s tlacidlami
     * @param c canvas na vykreslenie
     */
    private void drawMenu(Canvas c) {
        c.drawBitmap(menuBackground.getSprite(), menuBackground.getPosX(), menuBackground.getPosY(), null);

        Paint text = new Paint(Color.BLACK);
        text.setTextSize((int)(128 * scale));
        c.drawText("START GAME", (int)(565 * scale), (int)(540 * scale), text);
    }

    /**
     * Vykreslenie score screenu hry
     * @param c canvas na vykreslenie
     */
    public void drawScore(Canvas c) {
        c.drawBitmap(scoreBackground.getSprite(), scoreBackground.getPosX(), scoreBackground.getPosY(), null);

        Paint text = new Paint(Color.BLACK);
        text.setTextSize((int)(128 * scale));
        c.drawText("YOUR SCORE IS", (int)(490 * scale), (int)(175 * scale), text);
        c.drawText(Integer.toString(gameThread.getScore()),(int)(800 * scale), (int)(302 * scale), text);

        c.drawText("REPLAY GAME", (int)(535 * scale), (int)(604 * scale), text);
    }

    /**
     * Vykreslovanie volane pomocou tiku hry, rozhoduje nad vykreslenim menu/score alebo
     * vykreslovanim hlavnej hry.
     * @param canvas canvas na vykreslenie
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            try {
                if(gameThread.isShowMenu()) {
                    if(!gameThread.isEndgame()) {
                        this.drawMenu(canvas);
                    } else {
                        this.drawScore(canvas);
                    }
                } else {
                    //Vykreslenie pozadia
                    canvas.drawBitmap(gameBackground.getSprite(), gameBackground.getPosX(), gameBackground.getPosY(), null);

                    //Vykreslenie NPC
                    if (gameThread.getNpcChickenList().size() != 0) {
                        for (Chicken chicken : gameThread.getNpcChickenList()) {
                            if (chicken.getAbsX() > (0 - chicken.getSprite().getWidth()) && chicken.getAbsX() < 1920) {  //Kontrola vykreslovania len tych co maju byt na obrazovke
                                canvas.drawBitmap(chicken.getSprite(), (int) (chicken.getAbsX() * scale), (int) (chicken.getAbsY() * this.scale), null);
                            }
                        }
                    }

                    //Vykreslenie Objektov pred NPC
                    if (gameThread.getEntityList().size() != 0) {
                        for (Entity entity : gameThread.getEntityList()) {
                            if (entity.getAbsX() > (0 - entity.getSprite().getWidth()) && entity.getAbsX() < 1920) {    //Kontrola vykreslovania len tych co maju byt na obrazovke
                                canvas.drawBitmap(entity.getSprite(), (int) (entity.getAbsX() * scale), (int) (entity.getAbsY() * scale), null);
                            }
                        }
                    }

                    //Vykreslenie SW tlacidiel
                    canvas.drawBitmap(gameThread.getLeftArrow().getSprite(), (int) (gameThread.getLeftArrow().getAbsX() * scale), (int) (gameThread.getLeftArrow().getAbsY() * scale), null);
                    canvas.drawBitmap(gameThread.getRightArrow().getSprite(), (int) (gameThread.getRightArrow().getAbsX() * scale), (int) (gameThread.getRightArrow().getAbsY() * scale), null);
                    canvas.drawBitmap(gameThread.getReloadAmmunition().getSprite(), (int) (gameThread.getReloadAmmunition().getAbsX() * scale), (int) (gameThread.getReloadAmmunition().getAbsY() * scale), null);

                    //Vykreslenie nabojov
                    for (int i = 0; i < gameThread.AMMO_COUNT; i++) {
                        Ammunition ammo = gameThread.getAmmunition()[i];
                        if (ammo.isVisible()) {
                            canvas.drawBitmap(ammo.getSprite(), (int) (ammo.getAbsX() * scale), (int) (ammo.getAbsY() * scale), null);
                        }
                    }

                    //Vykreslenie casu do konca hry
                    Paint timerColor = new Paint();
                    timerColor.setTextSize(64);
                    if (gameThread.getGameTime() <= 20) {
                        timerColor.setColor(Color.RED);
                    } else {
                        timerColor.setColor(Color.BLACK);
                    }
                    canvas.drawText(Integer.toString(gameThread.getGameTime()), (int) ((this.displaySizeX / 2) - 64), (int) (65 * scale), timerColor);

                    //Vykreslenie score
                    Paint scoreColor = new Paint(Color.BLACK);
                    scoreColor.setTextSize(64);
                    canvas.drawText("SCORE: " + Integer.toString(gameThread.getScore()), (int) (10 * scale), (int) (65 * scale), scoreColor);

                    //DEBUG Vykreslenie (poloha dotyku)
                    /*
                    Paint textColor = new Paint(Color.BLACK);
                    textColor.setTextSize(32);
                    canvas.drawText(("X: " + touchX + "   Y: " + touchY + " TOUCH: " + touch), 33, 33, textColor);  //TOUCH PROPERTIES
                    canvas.drawText("X: " + this.displaySizeX + "   Y: " + this.displaySizeY, 33, 66, textColor);   //DISPLAY SIZE
                    */
                }
            } catch (NullPointerException e) {
                System.out.println("Nullptr Exception on draw() - probably a missing sprite!");
            }
        }
    }

}
