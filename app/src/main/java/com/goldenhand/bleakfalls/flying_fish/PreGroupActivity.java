package com.goldenhand.bleakfalls.flying_fish;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;


public class PreGroupActivity extends ActionBarActivity {

    private static boolean mIsRegistered;
    private static String mGroupId;
    private static String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_group);

        if (getIntent().getExtras().containsKey(LoginActivity.REGISTERED_USER_ID)) {
            mIsRegistered = true;
            mUserId = getIntent().getStringExtra(LoginActivity.REGISTERED_USER_ID);
        }
        mGroupId = getIntent().getStringExtra(GroupActivity.GROUP_ID);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        query.getInBackground(mGroupId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                ArrayList<String> mUserIdArrayList = (ArrayList<String>) parseObject.get("UserIds");
                if (mUserIdArrayList != null) {
                    if (mUserIdArrayList.contains(mUserId)) {
                        Intent i = new Intent(PreGroupActivity.this, GroupActivity.class);
                        i.putExtra(LoginActivity.REGISTERED_USER_ID, mUserId);
                        i.putExtra(GroupActivity.GROUP_ID, mGroupId);
                        startActivity(i);
                    }
                }
            }
        });


        Button mJoinGroupButton = (Button) findViewById(R.id.join_group);
            mJoinGroupButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
                query.getInBackground(mGroupId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            ArrayList<String> mUserIdArrayList = (ArrayList<String>) parseObject.get("UserIds");
                            if (mUserIdArrayList == null) {
                                mUserIdArrayList = new ArrayList<String>();
                            }
                            mUserIdArrayList.add(mUserId);
                            parseObject.put("UserIds", mUserIdArrayList);
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(PreGroupActivity.this, "ADDED!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(PreGroupActivity.this, FishActivity.class);
                                    if (mIsRegistered) {
                                        i.putExtra(LoginActivity.REGISTERED_USER_ID, mUserId);
                                    }
                                    startActivity(i);
                                }
                            });
                        } else {
                            //TODO ANYTHING??
                        }
                    }
                });
            }
            }
        );
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pre_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
