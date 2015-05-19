package com.goldenhand.bleakfalls.flying_fish;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class CatApplication extends Application {
    public static final String APPLICATION_ID = "a2jh49fEnFAsCzC9DouV1nBtDwWJWZtLDIYFehfZ";
    public static final String CLIENT_KEY = "HvkqXU3W7nUj1e1KlMSDcqVNa4S4sssLRgV5aoFc";

    @Override
    public void onCreate() {
        super.onCreate();


        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }
}