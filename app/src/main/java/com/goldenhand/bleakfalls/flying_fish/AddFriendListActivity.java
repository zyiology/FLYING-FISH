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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_list);

        mUserId = getIntent().getStringExtra(USER_ID);

        ParseQuery<ParseUser> selfQuery = ParseUser.getQuery();
        selfQuery.getInBackground(mUserId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                mFriends = parseUser.getList("friends");
                currentUser = parseUser;
            }
        });

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    mUserList = list;
                    UserAdapter mUserAdapter = new UserAdapter(AddFriendListActivity.this, R.layout.activity_add_friend_list_item, mUserList);
                    usernameLV = (ListView) findViewById(R.id.username_list);
                    usernameLV.setAdapter(mUserAdapter);
                    setUpOnClick();
                }
            }
        });
    }

    private void setUpOnClick() {
        usernameLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseUser selectedUser = (ParseUser) usernameLV.getItemAtPosition(position);

                if (!mFriends.contains(selectedUser)) {

                    mFriends.add(selectedUser);
                    currentUser.put("friends",mFriends);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            NavUtils.navigateUpTo(AddFriendListActivity.this, new Intent(AddFriendListActivity.this, FishActivity.class));
                            Intent navigateUpIntent = new Intent();
                            navigateUpIntent.putExtra(LoginActivity.REGISTERED_USER_ID, currentUser.getObjectId());
                            setResult(RESULT_OK, navigateUpIntent);
                            System.out.println("NAVIGATING BACK");
                            finish();
                        }
                    });
                } else {

                }

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
