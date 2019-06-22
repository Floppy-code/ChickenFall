package com.example.chickenfall;

import android.content.Context;
import android.media.MediaPlayer;

public class Sound {
    private MediaPlayer musicPlayer;
    private Context context = null;

    public Sound(Context context, int sound) {
        this.context = context;

        musicPlayer = MediaPlayer.create(this.context, sound);
    }

    public void setRepeat(boolean repeat) {
        musicPlayer.setLooping(repeat);
    }

    public void play() {
        musicPlayer.start();
    }

    public void stop() {
        musicPlayer.stop();
    }

    public void release() {
        musicPlayer.release();
        musicPlayer = null;
    }
}
