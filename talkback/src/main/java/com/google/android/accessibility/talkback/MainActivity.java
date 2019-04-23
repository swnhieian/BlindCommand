package com.google.android.accessibility.talkback;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import blindcommand.Parser;
import blindcommand.SoundPlayer;
import blindcommand.Utility;
import blindcommand.Log;

public class MainActivity extends AppCompatActivity {
    Button openSettingsButton;
    Switch parserSwitcher;
    Button speechTest;
    Button speechConfirm;
    EditText speechSpeed;
    EditText speechPitch;
    EditText speechVolume;
    Context activity = this;
    static final String LOGTAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        openSettingsButton = findViewById(R.id.button);
        openSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
        parserSwitcher = findViewById(R.id.switcher);
        parserSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                System.out.println(isChecked);
                Utility.parserType = isChecked ? Parser.ParserType.SPEECH : Parser.ParserType.DEFAULT;
                Log.i(LOGTAG, "parserSwitcher: " + (isChecked ? "speech" : "default"));
            }
        });
        Switch naviSwitcher = findViewById(R.id.navigation_switcher);
        naviSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utility.useDiffNav = isChecked;
                Log.i(LOGTAG, "useDiffNav: " + isChecked);
            }
        });

        speechTest = findViewById(R.id.test_button);
        speechConfirm = findViewById(R.id.confirm_button);
        speechSpeed = findViewById(R.id.speechSpeed);
        speechPitch = findViewById(R.id.speechPitch);
        speechVolume = findViewById(R.id.speechVolume);
        speechTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Utility.speechPitch = speechPitch.getText().toString();
                Utility.speechSpeed = speechSpeed.getText().toString();
                Utility.speechVolume = speechVolume.getText().toString();
                SoundPlayer.setParam();
                SoundPlayer.tts("微信朋友圈，当前第1项，共1项");
            }
        });
        speechConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Utility.speechPitch = speechPitch.getText().toString();
                Utility.speechSpeed = speechSpeed.getText().toString();
                Utility.speechVolume = speechVolume.getText().toString();
                SoundPlayer.setParam();
                Toast.makeText(activity, "设置完成",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void requestPermissions(){
//        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.LOCATION_HARDWARE,Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_SETTINGS,Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS,
                            Manifest.permission.CAMERA},0x0010);
                }
            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
