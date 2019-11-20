package com.jianjin33.soundeffect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import org.fmod.FMOD;

import java.io.File;

public class CustomActivity extends AppCompatActivity implements View.OnClickListener {

    private EffectProcess mEffectProcess;
    private String mPath;

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

        View playBtn = findViewById(R.id.play);
        playBtn.setOnClickListener(this);
        findViewById(R.id.mode_normal).setOnClickListener(this);
        findViewById(R.id.mode_luoli).setOnClickListener(this);
        findViewById(R.id.mode_dashu).setOnClickListener(this);
        findViewById(R.id.mode_jingsong).setOnClickListener(this);
        playBtn.setSelected(false);
        File media = new File(Environment.getExternalStorageDirectory(),"test.mp3");
        mPath = media.getPath();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.play:
            case R.id.mode_normal:
                mEffectProcess.native_effect(mPath, EffectProcess.MODE_NORMAL);
                break;
            case R.id.mode_luoli:
                mEffectProcess.native_effect(mPath, EffectProcess.MODE_LUOLI);
                break;
            case R.id.mode_dashu:
                mEffectProcess.native_effect(mPath, EffectProcess.MODE_DASHU);
                break;
            case R.id.mode_jingsong:
                mEffectProcess.native_effect(mPath, EffectProcess.MODE_JINGSONG);
                break;
        }
    }
}
