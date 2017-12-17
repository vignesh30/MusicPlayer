package com.example.olaplaymp3player;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {
    SharedPreferences shared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        shared=getSharedPreferences("OlaPlay", MODE_PRIVATE);
        //shared.getBoolean("isFirstTime", true)
        if(true){
            shared.edit().putBoolean("isFirstTime", false).commit();
            Intent i=new Intent(LauncherActivity.this, WelcomePage.class);
            finish();
            startActivity(i);

        }else{
            Intent i=new Intent(LauncherActivity.this, MainActivity.class);
            finish();
            startActivity(i);
        }
    }
}
