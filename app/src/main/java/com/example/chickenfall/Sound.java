package com.example.chickenfall;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Obalovacia trieda pre Media Player
 */
public class Sound {
    private MediaPlayer musicPlayer;    //MediaPlayer instancia
    private Context context = null;     //Context aplikacie

    /**
     * Konstruktor obalovacej triedy
     * @param context kotext aplikacie
     * @param sound cesta k suboru so zvukom
     */
    public Sound(Context context, int sound) {
        this.context = context;

        musicPlayer = MediaPlayer.create(this.context, sound);
    }

    /**
     * Nastavenie nekonecneho opakovania zvuku
     * @param repeat
     */
    public void setRepeat(boolean repeat) {
        musicPlayer.setLooping(repeat);
    }

    /**
     * Prehratie zvuku
     */
    public void play() {
        musicPlayer.start();
    }

    public void stop() {
        musicPlayer.stop();
    }

    /**
     * Uvolnenie instancie MusicPlayer
     */
    public void release() {
        musicPlayer.release();
        musicPlayer = null;
    }
}
