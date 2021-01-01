package com.animsh.trace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.animsh.trace.ui.LoginActivity;
import com.animsh.trace.ui.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        if (isDarkModeOn) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
        } else {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (restorePrefData()) {
                    Intent homePageActivity = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(homePageActivity);
                    finish();
                } else {
                    Intent loginActivity = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                }
            }
        }, 2000);


    }

    public boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        return pref.getBoolean("isUserLogin", false);
    }
}