package com.goldenhand.bleakfalls.flying_fish;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by S9925872A on 5/12/2015.
 */
public class CatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "a2jh49fEnFAsCzC9DouV1nBtDwWJWZtLDIYFehfZ", "HvkqXU3W7nUj1e1KlMSDcqVNa4S4sssLRgV5aoFc");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }
}