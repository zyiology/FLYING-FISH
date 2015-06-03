package com.goldenhand.bleakfalls.flying_fish;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


public class EditGroupActivity extends ActionBarActivity {


    public static String GROUP_OBJECT_ID = "object id of group to be edited";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        //TextView test = (TextView) findViewById(R.id.test);
        //test.setText(getIntent().getStringExtra(GROUP_OBJECT_ID));
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        query.getInBackground(getIntent().getStringExtra(GROUP_OBJECT_ID), new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject parseObject, ParseException e) {
                if (e==null) {
                    final EditText mGroupNameEditText = (EditText) findViewById(R.id.group_name);
                    mGroupNameEditText.setText((String) parseObject.get("Name"));
                    //DO ANY OTHER EDITTEXTS HERE

                    Button mSaveChangesButton = (Button) findViewById(R.id.save_changes);
                    mSaveChangesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            parseObject.put("Name", mGroupNameEditText.getText().toString());
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(),FishActivity.class);
                                    if (getIntent().getExtras().containsKey(LoginActivity.REGISTERED_USER_ID)) {
                                        i.putExtra(LoginActivity.REGISTERED_USER_ID, getIntent().getStringExtra(LoginActivity.REGISTERED_USER_ID));
                                    }
                                    else {
                                        i.putExtra(LoginActivity.ANON_USER_ID, getIntent().getStringExtra(LoginActivity.ANON_USER_ID));
                                    }
                                    startActivity(i);
                                }
                            });
                        }
                    });
                }
                else {
                    //TODO ANYTHING??
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_group, menu);
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
