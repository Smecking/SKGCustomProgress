package com.smecking.customprogress;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private CustomProgress custom_progress;
    private int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        custom_progress = (CustomProgress) findViewById(R.id.custom_progress);
        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                current++;
                if (current > 100) {
                    current = 0;
                }
                custom_progress.setCurrentProgress(current);
                handler1.postDelayed(this, 100);
            }
        }, 100);
    }
}
