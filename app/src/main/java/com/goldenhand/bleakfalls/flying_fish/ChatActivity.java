package com.goldenhand.bleakfalls.flying_fish;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class ChatActivity extends ActionBarActivity {

    private String userId;
    private String friendId;
    private String convoId;
    private Handler handler;

    private EditText messageET;
    private Button sendButton;

    private ListView chatLV;
    private ArrayList<ParseObject> messageArrayList;
    private List<ParseObject> chatMessageArray;
    private ChatListAdapter chatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userId = getIntent().getStringExtra(LoginActivity.REGISTERED_USER_ID);
        friendId = getIntent().getStringExtra(FriendListFragment.SELECTED_USER_ID);
        convoId = getIntent().getStringExtra(FriendListFragment.CONVO_ID);

        messageET = (EditText) findViewById(R.id.messageET);
        sendButton = (Button) findViewById(R.id.sendButton);
        chatLV = (ListView) findViewById(R.id.chatLV);

        messageArrayList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(ChatActivity.this, userId, messageArrayList);
        chatLV.setAdapter(chatListAdapter);

        handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                receiveMessage();
                handler.postDelayed(this, 1000);
            }
        };


        handler.postDelayed(runnable, 1000);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = messageET.getText().toString();

                final ParseObject newChatMessage = new ParseObject("ChatMessage");
                newChatMessage.put("sender",userId);
                newChatMessage.put("message", body);

                ParseQuery<ParseUser> selfQuery = ParseUser.getQuery();
                selfQuery.getInBackground(userId, new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            newChatMessage.put("username", parseUser.getUsername());
                            newChatMessage.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    ParseQuery<ParseObject> convoQuery = ParseQuery.getQuery("Conversation");
                                    convoQuery.getInBackground(convoId, new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject parseObject, ParseException e) {
                                            List<ParseObject> chatMessageArray = parseObject.getList("chatMessageArray");
                                            chatMessageArray.add(newChatMessage);
                                            parseObject.put("chatMessageArray", chatMessageArray);
                                            parseObject.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    receiveMessage();
                                                }
                                            });
                                        }
                                    });


                                }
                            });
                        }
                    }
                });

                messageET.setText("");
            }
        });


    }



    // Query messages from Parse so we can load them into the chat adapter
    private void receiveMessage() {
        // Construct query to execute
        ParseQuery<ParseObject> refreshQuery = ParseQuery.getQuery("Conversation");

        refreshQuery.getInBackground(convoId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    chatMessageArray = parseObject.getList("chatMessageArray");

                    messageArrayList.clear();
                    messageArrayList.addAll(chatMessageArray);
                    chatListAdapter.notifyDataSetChanged();
                    chatLV.invalidate();
                } else {
                        System.out.println(e.getMessage());
                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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
