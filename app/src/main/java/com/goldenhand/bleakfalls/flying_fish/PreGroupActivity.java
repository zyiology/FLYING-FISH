package com.goldenhand.bleakfalls.flying_fish;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;


public class PreGroupActivity extends ActionBarActivity {

    private static boolean mIsRegistered;
    private static String mGroupId;
    private static String mUserId;

    private ParseUser currentUser;
    private ParseObject currentGroup;

    private String groupName;
    Boolean notifExists = false;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_group);

        toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

        if (getIntent().getExtras().containsKey(LoginActivity.REGISTERED_USER_ID)) {
            mIsRegistered = true;
            mUserId = getIntent().getStringExtra(LoginActivity.REGISTERED_USER_ID);
        }
        mGroupId = getIntent().getStringExtra(GroupActivity.GROUP_ID);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        query.getInBackground(mGroupId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                groupName = parseObject.getString("Name");
                TextView mGroupNameTV = (TextView) findViewById(R.id.group_name);
                mGroupNameTV.setText(groupName);
                ArrayList<String> mUserIdArrayList = (ArrayList<String>) parseObject.get("UserIds");
                if (mUserIdArrayList != null) {
                    if (mUserIdArrayList.contains(mUserId)) {
                        Intent i = new Intent(PreGroupActivity.this, GroupActivity.class);
                        i.putExtra(LoginActivity.REGISTERED_USER_ID, mUserId);
                        i.putExtra(GroupActivity.GROUP_ID, mGroupId);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });



        Button mJoinGroupButton = (Button) findViewById(R.id.join_group);
            mJoinGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    ParseQuery<ParseObject> notifQuery = ParseQuery.getQuery("Notification");
                    notifQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {

                            for (ParseObject notification: list) {
                                if (!notification.getBoolean("isFriendNotif") && notification.getString("from").equals(mUserId) && notification.getString("groupId").equals(mGroupId)) {
                                    notifExists = true;
                                }
                            }

                            if (!notifExists) {
                                ParseQuery<ParseUser> selfQuery = ParseUser.getQuery();
                                selfQuery.getInBackground(mUserId, new GetCallback<ParseUser>() {
                                    @Override
                                    public void done(ParseUser parseUser, ParseException e) {
                                        currentUser = parseUser;

                                        final ParseObject groupNotif = new ParseObject("Notification");
                                        groupNotif.put("isFriendNotif", false);
                                        groupNotif.put("from", mUserId);
                                        groupNotif.put("fromName", currentUser.getUsername());
                                        groupNotif.put("groupId", mGroupId);

                                        ParseQuery<ParseObject> groupQuery = ParseQuery.getQuery("Group");
                                        groupQuery.getInBackground(mGroupId, new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject parseObject, ParseException e) {
                                                if (e == null) {
                                                    currentGroup = parseObject;
                                                    groupNotif.put("to", currentGroup.getString("admin"));
                                                    groupNotif.put("groupName", currentGroup.getString("Name"));
                                                    groupNotif.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            toast.setText(R.string.toast_group_success);
                                                            toast.show();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                });

                            } else {
                                toast.setText(R.string.toast_group_spam);
                                toast.show();
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
