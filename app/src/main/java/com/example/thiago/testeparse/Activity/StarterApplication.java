package com.example.thiago.testeparse.Activity;


import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

import static com.example.thiago.testeparse.BuildConfig.BACK4APP_APP_ID;
import static com.example.thiago.testeparse.BuildConfig.BACK4APP_CLIENT_KEY;
import static com.example.thiago.testeparse.BuildConfig.BACK4APP_SERVER;

public class StarterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(BACK4APP_APP_ID)
                .clientKey(BACK4APP_CLIENT_KEY)
                .server(BACK4APP_SERVER)
                .build()
        );

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);


    }
}
