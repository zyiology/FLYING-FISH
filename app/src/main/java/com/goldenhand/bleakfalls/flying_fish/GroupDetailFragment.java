package com.goldenhand.bleakfalls.flying_fish;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Default on 24/5/2015.
 */



public class GroupDetailFragment extends Fragment {
    private final String ARG_SECTION_NUMBER = "section_number";
    static List<ParseObject> mGroupList;

    private static String mUserId;
    private static boolean mIsRegistered;
    private static String mGroupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public GroupDetailFragment newInstance(int sectionNumber, String userId, boolean isRegistered, String groupId) {
        GroupDetailFragment fragment = new GroupDetailFragment();
        Bundle args = new Bundle();
        mUserId = userId;
        mIsRegistered = isRegistered;
        mGroupId = groupId;
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public GroupDetailFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_fish_group_detail, container, false);

        final ListView lv = (ListView) rootView.findViewById(R.id.user_list);

        Button mJoinGroupButton = (Button) rootView.findViewById(R.id.join_group);
        mJoinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
                query.getInBackground(mGroupId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            ArrayList<String> mUserIdArrayList = (ArrayList<String>) parseObject.get("UserIds");
                            if (mUserIdArrayList == null) {
                                mUserIdArrayList = new ArrayList<String>();
                            }
                            mUserIdArrayList.add(mUserId);
                            parseObject.put("UserIds", mUserIdArrayList);
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(getActivity(), "ADDED!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getActivity(),FishActivity.class);
                                    if (mIsRegistered) {
                                        i.putExtra(LoginActivity.REGISTERED_USER_ID, mUserId);
                                    }
                                    else {
                                        i.putExtra(LoginActivity.ANON_USER_ID, mUserId);
                                    }
                                    startActivity(i);
                                }
                            });
                        } else {
                            //TODO ANYTHING??
                        }
                    }
                });
            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        query.getInBackground(mGroupId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                final ArrayList<String> mUserIdArrayList = (ArrayList<String>) parseObject.get("UserIds");
                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        ArrayList<ParseUser> mUserArrayList = new ArrayList<ParseUser>();
                        if (mUserIdArrayList!=null) {
                            for (int i = 0; i < list.size(); i++) {
                                if (mUserIdArrayList.contains(list.get(i).getObjectId())) {
                                    mUserArrayList.add(list.get(i));
                                }
                            }
                            GroupAdapter mGroupAdapter = new GroupAdapter(getActivity(), R.layout.fragment_fish_group_detail, mUserArrayList);
                            lv.setAdapter(mGroupAdapter);
                        }
                    }
                });
            }
        });

        return rootView;
    }

    private class GroupAdapter extends ArrayAdapter<ParseUser> {
        List<ParseUser> mUsers;
        Context mContext;
        public GroupAdapter(Context context, int resource, List<ParseUser> users) {
            super(context, resource, users);
            mUsers = users;
            mContext = context;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = new ViewHolder();

            if (row==null) {
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.fragment_fish_group_detail_item, parent, false);
                holder.userNameTextView = (TextView) row.findViewById(R.id.user_name);
                row.setTag(holder);
            }
            else {
                holder = (ViewHolder) row.getTag();
            }
            final ParseObject currentUser = mUsers.get(position);
            holder.userNameTextView.setText(currentUser.get("username").toString());
            return row;
        }

        class ViewHolder{
            TextView userNameTextView;
        }
    }
}

