package com.example.gift4you.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.gift4you.R;

public class Splash_Activity extends AppCompatActivity {

    private final int DURACION_SPLASH = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {

            Intent intent = new Intent(Splash_Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, DURACION_SPLASH);
    }
}
