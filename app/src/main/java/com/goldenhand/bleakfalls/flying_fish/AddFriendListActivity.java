package com.goldenhand.bleakfalls.flying_fish;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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


public class AddFriendListActivity extends ActionBarActivity {
    public static final String USER_ID = "user id";
    private static String mUserId;
    private ParseUser currentUser;
    private List<ParseUser> mFriends;

    private ListView usernameLV;

    static List<ParseUser> mUserList;

    private Toast toast;
    Boolean notifExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_list);

        getSupportActionBar().hide();

        mUserId = getIntent().getStringExtra(USER_ID);

        toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

        ParseQuery<ParseUser> selfQuery = ParseUser.getQuery();
        selfQuery.getInBackground(mUserId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                mFriends = (List<ParseUser>) parseUser.get("friends");
                currentUser = parseUser;



                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        if (e == null) {
                            mUserList = list;
                            mUserList.remove(currentUser);
                            if (mFriends != null) {
                                for (ParseUser friend : mFriends) {
                                    mUserList.remove(friend);
                                }
                            }
                            UserAdapter mUserAdapter = new UserAdapter(AddFriendListActivity.this, R.layout.activity_add_friend_list_item, mUserList);
                            usernameLV = (ListView) findViewById(R.id.username_list);
                            usernameLV.setAdapter(mUserAdapter);
                            setUpOnClick();
                        }
                    }
                });
            }
        });
    }

    private void setUpOnClick() {
        usernameLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final ParseUser selectedUser = (ParseUser) usernameLV.getItemAtPosition(position);

                ParseQuery<ParseObject> notifQuery = ParseQuery.getQuery("Notification");
                notifQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for (ParseObject notification: list) {
                            if (notification.getBoolean("isFriendNotif") && notification.getString("to").equals(selectedUser.getObjectId()) && notification.getString("from").equals(mUserId)) {
                                notifExists = true;
                            }
                        }

                        if (!mFriends.contains(selectedUser) && selectedUser != currentUser && !notifExists) {
                            ParseObject friendNotif = new ParseObject("Notification");
                            friendNotif.put("isFriendNotif",true);
                            friendNotif.put("from", mUserId);
                            friendNotif.put("fromName", currentUser.getUsername());
                            friendNotif.put("to", selectedUser.getObjectId());
                            friendNotif.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    toast.setText(R.string.toast_request_success);
                                    toast.show();
                                }
                            });
                        } else {
                            toast.setText(R.string.toast_request_spam);
                            toast.show();
                        }
                    }
                });



            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_friend_list, menu);
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
