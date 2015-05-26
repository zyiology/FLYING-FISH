package com.goldenhand.bleakfalls.flying_fish;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;


public class ChatActivity extends ActionBarActivity {

    private EditText messageET;
    private Button sendButton;

    private ListView chatLV;
    private ArrayList<Message> messageArrayList;
    private ChatListAdapter chatListAdapter;

    public static final String BODY = "body";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageET = (EditText) findViewById(R.id.messageET);
        sendButton = (Button) findViewById(R.id.sendButton);
        chatLV = (ListView) findViewById(R.id.chatLV);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = messageET.getText().toString();
                ParseObject message = new ParseObject("Message");
                message.put(LoginActivity.ANON_USER_ID, getIntent().getStringExtra(LoginActivity.ANON_USER_ID));
                message.put(BODY,data);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(ChatActivity.this, "MESSAGE IS SEND", Toast.LENGTH_SHORT).show();
                    }
                });
                messageET.setText("");
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
