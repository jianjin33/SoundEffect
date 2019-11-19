package com.jianjin33.soundeffect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class CustomActivity extends AppCompatActivity {

    static {
        System.loadLibrary("effect-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        TextView tv = findViewById(R.id.sample_text);
        //native_effect("drumloop.wav");
    }

    //public native void native_effect(String path);
}
