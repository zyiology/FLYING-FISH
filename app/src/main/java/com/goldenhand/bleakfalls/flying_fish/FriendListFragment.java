package com.goldenhand.bleakfalls.flying_fish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;


public class FriendListFragment extends Fragment {
    private final String ARG_SECTION_NUMBER = "friend list section number";
    public static final int ADD_FRIEND_REQUEST = 1;

    private List<ParseUser> mFriendList;

    private static String mUserId;
    private static String mSelectedUserId;
    private static String mConvoId;
    private static Boolean mIsRegistered;
    private List<String> convoUsersArray;
    private ListView friendsLV;
    private FriendAdapter mFriendAdapter;

    private AlertDialog alertDialog;

    public static final String SELECTED_USER_ID = "selected user id";
    public static final String CONVO_ID = "convo id";

    public FriendListFragment newInstance(int sectionNumber, String userId, boolean isRegistered) {
        FriendListFragment fragment = new FriendListFragment();
        Bundle args = new Bundle();
        mUserId = userId;
        mIsRegistered = isRegistered;
        args.putInt(ARG_SECTION_NUMBER,sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_friend_list, container, false);

        ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
        friendQuery.getInBackground(mUserId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                mFriendList = parseUser.getList("friends");
                mFriendAdapter = new FriendAdapter(getActivity(), R.layout.fragment_friend_list_item, mFriendList);
                friendsLV = (ListView) rootView.findViewById(R.id.friends_list);
                friendsLV.setAdapter(mFriendAdapter);
                setUpFriendsOnClick();
            }
        });

        Button mAddFriendButton = (Button) rootView.findViewById(R.id.add_friend);
        mAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addFriendIntent = new Intent(getActivity(), AddFriendListActivity.class);
                addFriendIntent.putExtra(AddFriendListActivity.USER_ID, mUserId);
                startActivityForResult(addFriendIntent, ADD_FRIEND_REQUEST);
            }
        });


        return rootView;
    }

    private void startChatIntent() {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(LoginActivity.REGISTERED_USER_ID, mUserId);
        chatIntent.putExtra(SELECTED_USER_ID, mSelectedUserId);
        chatIntent.putExtra(CONVO_ID, mConvoId);
        startActivity(chatIntent);
    }

    private void setUpFriendsOnClick() {
        friendsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (alertDialog == null || !alertDialog.isShowing()) {

                    ParseUser selectedUser = (ParseUser) friendsLV.getItemAtPosition(position);
                    mSelectedUserId = selectedUser.getObjectId();

                    convoUsersArray = new ArrayList<>();

                    convoUsersArray.add(mUserId);
                    convoUsersArray.add(mSelectedUserId);

                    Collections.sort(convoUsersArray, new Comparator<String>() {
                        @Override
                        public int compare(String lhs, String rhs) {
                            return lhs.compareToIgnoreCase(rhs);
                        }
                    });

                    ParseQuery<ParseObject> convoQuery = ParseQuery.getQuery("Conversation");
                    convoQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {
                                Boolean foundExistingConvo = false;
                                for (ParseObject convo : list) {

                                    if (convo.getList("users").equals(convoUsersArray)) {
                                        mConvoId = convo.getObjectId();
                                        startChatIntent();
                                        foundExistingConvo = true;
                                    }
                                }

                                if (!foundExistingConvo) {
                                    final ParseObject newConvo = new ParseObject("Conversation");
                                    newConvo.put("users", convoUsersArray);
                                    newConvo.put("chatMessageArray", new ArrayList<ParseObject>());
                                    newConvo.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                mConvoId = newConvo.getObjectId();
                                                startChatIntent();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }

            }
        });

        friendsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ParseUser selectedUser = (ParseUser) friendsLV.getItemAtPosition(position);

                alertDialog = confirmFriendDeleteDialog(selectedUser);
                alertDialog.show();
                return false;
            }
        });
    }

    public void updateAdapter() {
        mFriendAdapter.notifyDataSetChanged();
    }

    private AlertDialog confirmFriendDeleteDialog(final ParseUser friend) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getText(R.string.delete_warning))
                .setMessage(getText(R.string.delete_friend)+" "+friend.getUsername()+"?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ParseQuery<ParseUser> deleteQuery = ParseUser.getQuery();
                        deleteQuery.getInBackground(mUserId, new GetCallback<ParseUser>() {
                            @Override
                            public void done(final ParseUser parseUser, ParseException e) {
                                if (e == null) {
                                    List<ParseUser> mFriends = parseUser.getList("friends");
                                    mFriends.remove(friend);
                                    parseUser.put("friends", mFriends);

                                    parseUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                            mFriendList.remove(friend);
                                            updateAdapter();

                                            List<ParseUser> otherFriends = friend.getList("friends");
                                            otherFriends.remove(parseUser);
                                            friend.put("friends", otherFriends);

                                            friend.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {

                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();
    }

    private class FriendAdapter extends ArrayAdapter<ParseUser> {
        List<ParseUser> mFriends;
        Context context;

        public FriendAdapter(Context context, int resource, List<ParseUser> mFriends) {
            super(context, resource, mFriends);
            this.mFriends = mFriends;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.fragment_friend_list_item, null);
            }

            holder.friendName = (TextView)convertView.findViewById(R.id.friend_name);
            convertView.setTag(holder);

            final ParseUser currentFriend = mFriends.get(position);

            holder.friendName.setText(currentFriend.getUsername());

            return convertView;
        }

        final class ViewHolder {
            private TextView friendName;
        }
    }
}
