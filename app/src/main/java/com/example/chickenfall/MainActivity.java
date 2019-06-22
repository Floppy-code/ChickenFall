package com.example.chickenfall;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    /**
     * Metoda urcujuca cinnosti po vytvoreni aktivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(new GameView(this));
    }

    /**
     * Metoda urcujuca cinnosti po pauze aktivity
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Metoda urcujuca cinnosti po opatovnom otvoreni aktivity
     */
    @Override
    protected void onResume() {
        super.onResume();
    }
}
