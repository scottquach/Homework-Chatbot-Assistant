package com.scottquach.homeworkchatbotassistant;

import android.app.Application;
import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;
import com.scottquach.homeworkchatbotassistant.database.Database;
import com.scottquach.homeworkchatbotassistant.utils.InstrumentationUtils;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by Scott Quach on 9/10/2017.
 */

public class BaseApplication extends Application {
    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    public Database database;
    public InstrumentationUtils instrumentation;

    public BaseApplication() {}

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        Timber.plant(new MyDebugTree());

        instrumentation = new InstrumentationUtils(getApplicationContext());
        database = new Database();
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
