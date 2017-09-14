package com.scottquach.homeworkchatbotassistant;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;

/**
 * Created by Scott Quach on 9/10/2017.
 */

public class BaseApplication extends Application {
    private static final BaseApplication instance = new BaseApplication();

    static BaseApplication getInstance() {
        return instance;
    }

    public BaseApplication() {}

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new MyDebugTree());
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
