package com.scottquach.homeworkchatbotassistant.activities;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.scottquach.homeworkchatbotassistant.AlertDialogFragment;
import com.scottquach.homeworkchatbotassistant.R;
import com.scottquach.homeworkchatbotassistant.utils.JobSchedulerUtil;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button signOutButton = (Button) findViewById(R.id.button_sign_out);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingsActivity.this, SignInActivity.class));
            }
        });

        Button testButton = (Button) findViewById(R.id.button_test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                JobSchedulerUtil.INSTANCE.cancelAllJobs(SettingsActivity.this);
            }
        });
    }
}
