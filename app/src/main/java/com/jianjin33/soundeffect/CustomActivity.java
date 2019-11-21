package com.jianjin33.soundeffect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;

import org.fmod.FMOD;

import java.io.File;

public class CustomActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int START = 1000;
    public static final int PAUSE = 1001;

    private EffectProcess mEffectProcess;
    private String mPath;
    private Handler mThreadHandler;
    private SpectrumDrawView animView;
    private boolean isPlaying;
    private View playBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(perms[1]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }
        mEffectProcess = new EffectProcess();
        FMOD.init(this);

        playBtn = findViewById(R.id.play);
        animView = findViewById(R.id.anim);
        playBtn.setOnClickListener(this);
        findViewById(R.id.mode_normal).setOnClickListener(this);
        findViewById(R.id.mode_luoli).setOnClickListener(this);
        findViewById(R.id.mode_dashu).setOnClickListener(this);
        findViewById(R.id.mode_jingsong).setOnClickListener(this);
        findViewById(R.id.mode_gaoguai).setOnClickListener(this);
        findViewById(R.id.mode_kongling).setOnClickListener(this);
        playBtn.setSelected(true);
        File media = new File(Environment.getExternalStorageDirectory(), "test.mp3");
        mPath = media.getPath();
        //mPath = "file:///android_asset/test.mp3";

        HandlerThread thread = new HandlerThread("play-thread");
        thread.start();

        mThreadHandler = new Handler(thread.getLooper()) {
            public void handleMessage(Message msg) {
                int mode = msg.what;
                switch (mode) {
                    case START:
                        mEffectProcess.native_start();
                        break;
                    case PAUSE:
                        mEffectProcess.native_pause();
                        break;
                    case EffectProcess.MODE_NORMAL:
                        mEffectProcess.native_effect(mPath, EffectProcess.MODE_NORMAL);
                        break;
                    case EffectProcess.MODE_LUOLI:
                        mEffectProcess.native_effect(mPath, EffectProcess.MODE_LUOLI);
                        break;
                    case EffectProcess.MODE_DASHU:
                        mEffectProcess.native_effect(mPath, EffectProcess.MODE_DASHU);
                        break;
                    case EffectProcess.MODE_JINGSONG:
                        mEffectProcess.native_effect(mPath, EffectProcess.MODE_JINGSONG);
                        break;
                    case EffectProcess.MODE_GAOGUAI:
                        mEffectProcess.native_effect(mPath, EffectProcess.MODE_GAOGUAI);
                        break;
                    case EffectProcess.MODE_KONGLING:
                        mEffectProcess.native_effect(mPath, EffectProcess.MODE_KONGLING);
                        break;
                }
            }

            ;
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                if (isPlaying) {
                    mThreadHandler.sendEmptyMessage(PAUSE);
                } else {
                    mThreadHandler.sendEmptyMessage(START);
                }
                isPlaying = !isPlaying;
                handleState(isPlaying);
                break;
            case R.id.mode_normal:
                mThreadHandler.sendEmptyMessage(EffectProcess.MODE_NORMAL);
                break;
            case R.id.mode_luoli:
                mThreadHandler.sendEmptyMessage(EffectProcess.MODE_LUOLI);
                break;
            case R.id.mode_dashu:
                mThreadHandler.sendEmptyMessage(EffectProcess.MODE_DASHU);
                break;
            case R.id.mode_jingsong:
                mThreadHandler.sendEmptyMessage(EffectProcess.MODE_JINGSONG);
                break;
            case R.id.mode_gaoguai:
                mThreadHandler.sendEmptyMessage(EffectProcess.MODE_GAOGUAI);
                break;
            case R.id.mode_kongling:
                mThreadHandler.sendEmptyMessage(EffectProcess.MODE_KONGLING);
                break;
        }
    }

    public void handleState(boolean isStart) {
        if (isStart)
            animView.startAnmi();
        else
            animView.stopAnmi();
        playBtn.setSelected(!isStart);
    }
}
