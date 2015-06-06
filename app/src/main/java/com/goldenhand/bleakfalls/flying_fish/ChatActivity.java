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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends ActionBarActivity {

    private String userId;
    private Handler handler;

    private EditText messageET;
    private Button sendButton;

    private ListView chatLV;
    private ArrayList<Message> messageArrayList;
    private ChatListAdapter chatListAdapter;

    public static final String BODY = "body";

    private static final Integer MAX_MESSAGES = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userId = getIntent().getStringExtra(LoginActivity.ANON_USER_ID);


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
                handler.postDelayed(this, 100);
            }
        };

        handler.postDelayed(runnable, 100);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = messageET.getText().toString();
                Message message = new Message();
                message.setUserId(userId);
                message.setBody(body);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        receiveMessage();
                    }
                });
                messageET.setText("");
            }
        });


    }

    // Query messages from Parse so we can load them into the chat adapter
    private void receiveMessage() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(MAX_MESSAGES);
        query.orderByAscending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    messageArrayList.clear();
                    messageArrayList.addAll(messages);
                    chatListAdapter.notifyDataSetChanged(); // update adapter
                    chatLV.invalidate(); // redraw listview
                } else {
                    Log.d("message", "Error: " + e.getMessage());
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
