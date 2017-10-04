package com.scottquach.homeworkchatbotassistant.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.scottquach.homeworkchatbotassistant.BaseApplication;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BaseApplication.getInstance().isFirstOpen()) {
            startActivity(new Intent(this, IntroActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }

    }
}
