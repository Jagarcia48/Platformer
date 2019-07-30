package edu.utep.cs.cs4381.platformer;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends Activity{
    private PlatformView platformView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point resolution = new Point();
        display.getSize(resolution);
        platformView = new PlatformView(this, resolution.x, resolution.y);
        setContentView(platformView);
        //setContentView(R.layout.activity_main);
    }
    @Override
    protected void onPause() {
        super.onPause();
        platformView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        platformView.resume();
    }

}
