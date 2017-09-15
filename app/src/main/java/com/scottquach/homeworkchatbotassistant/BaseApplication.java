package com.scottquach.homeworkchatbotassistant;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;

/**
 * Created by Scott Quach on 9/10/2017.
 */

public class BaseApplication extends Application {
    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    public BaseApplication() {}

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Timber.plant(new MyDebugTree());
    }

    public SharedPreferences getSharePref() {
        return this.getApplicationContext().getSharedPreferences("app", MODE_PRIVATE);
    }

    public boolean isFirstOpen() {
        return getSharePref().getBoolean("first_open", true);
    }

    /**
     * https://stackoverflow.com/questions/38689399/log-method-name-and-line-number-in-timber
     */
    public class MyDebugTree extends Timber.DebugTree {
        @Override
        protected String createStackElementTag(StackTraceElement element) {
            return String.format("[L:%s] [M:%s] [C:%s]",
                    element.getLineNumber(),
                    element.getMethodName(),
                    super.createStackElementTag(element));
        }
    }
}
