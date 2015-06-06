package com.goldenhand.bleakfalls.flying_fish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.w3c.dom.Text;

import java.util.List;
import java.util.ArrayList;


public class FriendListFragment extends Fragment {
    private final String ARG_SECTION_NUMBER = "friend list section number";
    public static final int ADD_FRIEND_REQUEST = 1;

    static List<ParseUser> mFriendList;

    private static String mUserId;
    private static Boolean mIsRegistered;
    private FriendAdapter mFriendAdapter;

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
                System.out.println("USER ID IN FLF: "+mUserId);
                mFriendList = parseUser.getList("friends");
                mFriendAdapter = new FriendAdapter(getActivity(), R.layout.fragment_friend_list_item, mFriendList);
                ListView friendsLV = (ListView) rootView.findViewById(R.id.friends_list);
                friendsLV.setAdapter(mFriendAdapter);
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

    public void updateAdapter() {
        mFriendAdapter.notifyDataSetChanged();
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
