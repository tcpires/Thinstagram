package com.example.thiago.testeparse.Activity;


import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class StarterApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("5BiBeoxCYIAmBZ57Kzzya4khTCB9lEYEN7KFAaRR")
                .clientKey("T6PQ37GcyBDjqndENPhpJa6JpIkEG5Yiq43EHjew")
                .server("https://parseapi.back4app.com/")
        .build()
        );

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);



    }
}
