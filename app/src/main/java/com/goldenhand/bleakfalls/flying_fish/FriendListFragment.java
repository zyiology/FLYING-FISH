package com.goldenhand.bleakfalls.flying_fish;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.util.List;


public class FriendListFragment extends Fragment {
    private final String ARG_SECTION_NUMBER = "friend list section number";

    static List<ParseObject> mFriendList;

    private static String mUserId;
    private static Boolean mIsRegistered;

    public FriendListFragment newInstance(int sectionNumber, String mUserId, boolean mIsRegistered) {
        FriendListFragment fragment = new FriendListFragment();
        Bundle args = new Bundle();
        this.mUserId = mUserId;
        this.mIsRegistered = mIsRegistered;
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

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("objectId",mUserId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
               if (e == null) {
                   if (list.size() != 0) {
                       mFriendList = (List<ParseObject>) list.get(0).get("friends");
                       FriendAdapter mFriendAdapter = new FriendAdapter(getActivity(), R.layout.fragment_friend_list_item, mFriendList);
                       ListView friendsLV = (ListView) rootView.findViewById(R.id.friends_list);
                       friendsLV.setAdapter(mFriendAdapter);
                   }
               } else {
                   System.out.println("FAILURE U IDIOT");
               }
            }
        });

        return rootView;
    }

    private class FriendAdapter extends ArrayAdapter<ParseObject> {
        List<ParseObject> mFriends;
        Context context;

        public FriendAdapter(Context context, int resource, List<ParseObject> mFriends) {
            super(context, resource, mFriends);
            this.mFriends = mFriends;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.ACTIVITY_SERVICE);
                convertView = mInflater.inflate(R.layout.fragment_friend_list_item, null);
            }

            final ParseObject currentFriend = mFriends.get(position);

            TextView friend = (TextView)convertView.findViewById(R.id.friend_name);
            friend.setText(currentFriend.get("username").toString());

            return convertView;
        }
    }
}
