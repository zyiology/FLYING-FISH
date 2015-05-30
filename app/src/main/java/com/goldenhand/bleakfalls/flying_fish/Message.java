package com.goldenhand.bleakfalls.flying_fish;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject{
    public String getUserId() {
        return getString(LoginActivity.ANON_USER_ID);
    }

    public String getBody() {
        return getString(ChatActivity.BODY);
    }

    public void setUserId(String userId) {
        put(LoginActivity.ANON_USER_ID, userId);
    }

    public void setBody(String body) {
        put(ChatActivity.BODY, body);
    }
}
