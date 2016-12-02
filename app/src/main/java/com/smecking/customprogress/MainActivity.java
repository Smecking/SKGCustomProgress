package com.smecking.customprogress;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private CustomProgress custom_progress;
    private static final int MSG=0x110;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int progress=custom_progress.getProgress();
            custom_progress.setProgress(progress++);
            if (progress>=100){
                mhandler.removeMessages(MSG);
            }
            mhandler.sendEmptyMessageDelayed(MSG,100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        custom_progress= (CustomProgress) findViewById(R.id.custom_progress);
        mhandler.sendEmptyMessage(MSG);
    }
}
