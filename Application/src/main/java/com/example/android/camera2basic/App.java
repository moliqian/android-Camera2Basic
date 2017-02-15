package com.example.android.camera2basic;

import android.app.Application;
import android.content.Context;

/**
 * Created by android on 17-2-15.
 */

public class App extends Application {
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }
}
