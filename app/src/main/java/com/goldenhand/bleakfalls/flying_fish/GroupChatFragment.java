package com.goldenhand.bleakfalls.flying_fish;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class GroupChatFragment extends Fragment {
    private final String ARG_SECTION_NUMBER = "chat section number";

    private static String mUserId;
    private static boolean mIsRegistered;
    private static String mGroupId;

    private ListView chatLV;
    private ArrayList<ParseObject> messageArrayList;
    private ChatListAdapter chatListAdapter;
    private List<ParseObject> chatMessageArray;

    final Handler handler = new Handler();

    public GroupChatFragment newInstance(int sectionNumber, String userId, Boolean isRegistered, String groupId) {
        GroupChatFragment fragment = new GroupChatFragment();
        Bundle args =  new Bundle();
        mUserId = userId;
        mIsRegistered = isRegistered;
        mGroupId = groupId;
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public GroupChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        final EditText messageET = (EditText) rootView.findViewById(R.id.messageET);
        Button sendButton = (Button) rootView.findViewById(R.id.sendButton);
        chatLV = (ListView) rootView.findViewById(R.id.chatLV);

        messageArrayList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(getActivity(), mUserId, messageArrayList);
        chatLV.setAdapter(chatListAdapter);

        handler.postDelayed(runnable, 1000);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = messageET.getText().toString();
                final ParseObject newChatMessage = new ParseObject("ChatMessage");
                newChatMessage.put("sender",mUserId);
                newChatMessage.put("message",body);
                newChatMessage.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        ParseQuery<ParseObject> groupQuery = ParseQuery.getQuery("Group");
                        groupQuery.getInBackground(mGroupId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                List<ParseObject> chatMessageArray = parseObject.getList("groupMessageArray");
                                chatMessageArray.add(newChatMessage);
                                parseObject.put("groupMessageArray", chatMessageArray);
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
                messageET.setText("");
            }
        });

        return rootView;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            receiveMessage();
            handler.postDelayed(this, 1000);
        }
    };

    private void receiveMessage() {
        // Construct query to execute
        ParseQuery<ParseObject> refreshQuery = ParseQuery.getQuery("Group");

        refreshQuery.getInBackground(mGroupId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    chatMessageArray = parseObject.getList("groupMessageArray");

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

}
