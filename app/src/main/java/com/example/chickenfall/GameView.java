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

public class GameView extends SurfaceView {

    private SurfaceHolder sHolder;
    private GameThread gameThread;

    private Sound musicPlayer;

    private int displaySizeX;
    private int displaySizeY;

    private double scale;

    private int touchX = 0;
    private int touchY = 0;
    private boolean touch = false;

    private Background gameBackground = null;

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
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                musicPlayer.setRepeat(true);
                musicPlayer.play();

                gameThread.setRun(true);
                gameThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

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

    public double getScale() {
        return scale;
    }

    public int getTouchX() {
        return touchX;
    }

    public int getTouchY() {
        return touchY;
    }

    public boolean isTouch() {
        return touch;
    }

    private Bitmap changeBitmapSize(Bitmap source, int width, int height) {
        return Bitmap.createScaledBitmap(source, width, height, false);
    }

    private void setChickenSpriteOrientation(Chicken chicken, boolean orientation) {
        chicken.setSpriteOrientation(orientation);
    }

    public void loadSprites() {
        //KONFIGURACIA POZADIA--------------------------------------
        gameBackground = gameThread.getLevelBackground();
        gameBackground.setSprite(BitmapFactory.decodeResource(getResources(), gameBackground.getSpriteLocation()));
        gameBackground.setSprite(changeBitmapSize(gameBackground.getSprite(), (int)(3840 * scale), (int)(1080 * scale)));

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
        gameThread.getLeftArrow().setSprite(BitmapFactory.decodeResource(getResources(), gameThread.getLeftArrow().getSpriteLocation()));
        gameThread.getRightArrow().setSprite(BitmapFactory.decodeResource(getResources(), gameThread.getRightArrow().getSpriteLocation()));
        gameThread.getReloadAmmunition().setSprite(BitmapFactory.decodeResource(getResources(), gameThread.getReloadAmmunition().getSpriteLocation()));
        gameThread.getReloadAmmunition().setSprite(changeBitmapSize(gameThread.getReloadAmmunition().getSprite(), (int)(75 * scale), (int)(75 * scale)));

        //KONFIGURACIA NABOJOV
        Ammunition[] ammo = gameThread.getAmmunition();
        for (int i = 0; i < gameThread.AMMO_COUNT; i++) {
            ammo[i].setSprite(BitmapFactory.decodeResource(getResources(), ammo[i].getSpriteLocation()));
            ammo[i].setSprite(changeBitmapSize(ammo[i].getSprite(), (int)(75 * scale), (int)(75 * scale)));
        }
    }

    public void reloadNullSprites() {
        if(gameThread.getNpcChickenList().size() == 1) {
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
                        npc.getSprites()[j] = changeBitmapSize(npc.getSprites()[j], (int)((230 - npc.getDistanceFromScreen()) * scale), (int)((150 - npc.getDistanceFromScreen()) * scale));
                    }
                    this.setChickenSpriteOrientation(npc, npc.isDirection());
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            try {
                //Vykreslenie pozadia
                canvas.drawBitmap(gameBackground.getSprite(), gameBackground.getPosX(), gameBackground.getPosY(), null);

                //Vykreslenie NPC
                if (gameThread.getNpcChickenList().size() != 0) {
                    for (Chicken chicken : gameThread.getNpcChickenList()) {
                        if (chicken.getAbsX() > (0 - chicken.getSprite().getWidth()) && chicken.getAbsX() < 1920){  //Kontrola vykreslovania len tych co maju byt na obrazovke
                            canvas.drawBitmap(chicken.getSprite(), (int)(chicken.getAbsX() * scale), (int)(chicken.getAbsY() * this.scale), null);
                        }
                    }
                }

                //Vykreslenie Objektov pred NPC
                if (gameThread.getEntityList().size() != 0) {
                    for (Entity entity : gameThread.getEntityList()) {
                        if (entity.getAbsX() > (0 - entity.getSprite().getWidth()) && entity.getAbsX() < 1920) {    //Kontrola vykreslovania len tych co maju byt na obrazovke
                            canvas.drawBitmap(entity.getSprite(), (int)(entity.getAbsX() * scale), (int)(entity.getAbsY() * scale), null);
                        }
                    }
                }

                //Vykreslenie SW tlacidiel
                canvas.drawBitmap(gameThread.getLeftArrow().getSprite(), (int)(gameThread.getLeftArrow().getAbsX() * scale), (int)(gameThread.getLeftArrow().getAbsY() * scale), null);
                canvas.drawBitmap(gameThread.getRightArrow().getSprite(), (int)(gameThread.getRightArrow().getAbsX() * scale), (int)(gameThread.getRightArrow().getAbsY() * scale), null);
                canvas.drawBitmap(gameThread.getReloadAmmunition().getSprite(), (int)(gameThread.getReloadAmmunition().getAbsX() * scale), (int)(gameThread.getReloadAmmunition().getAbsY() * scale), null);

                //Vykreslenie nabojov
                for (int i = 0; i < gameThread.AMMO_COUNT; i++) {
                    Ammunition ammo = gameThread.getAmmunition()[i];
                    if (ammo.isVisible()) {
                        canvas.drawBitmap(ammo.getSprite(), (int)(ammo.getAbsX() * scale), (int)(ammo.getAbsY() * scale), null);
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
                canvas.drawText(Integer.toString(gameThread.getGameTime()), (int)((this.displaySizeX / 2) - 64), (int)(65 * scale), timerColor);

                //Vykreslenie score
                Paint scoreColor = new Paint(Color.BLACK);
                scoreColor.setTextSize(64);
                canvas.drawText("SCORE: " + Integer.toString(gameThread.getScore()), (int)(10 * scale), (int)(65 * scale), scoreColor);

                //DEBUG Vykreslenie
                /*
                Paint textColor = new Paint(Color.BLACK);
                textColor.setTextSize(32);
                canvas.drawText(("X: " + touchX + "   Y: " + touchY + " TOUCH: " + touch), 33, 33, textColor);  //TOUCH PROPERTIES
                canvas.drawText("X: " + this.displaySizeX + "   Y: " + this.displaySizeY, 33, 66, textColor);   //DISPLAY SIZE
                */
            } catch (NullPointerException e) {
                System.out.println("Nullptr Exception - probably a missing sprite!");
            }
        }
    }

}
