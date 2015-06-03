package com.goldenhand.bleakfalls.flying_fish;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Default on 24/5/2015.
 */

//TODO IMPORTANT A NEWLY REGISTERED USER DOESN'T REGISTER JOIN GROUP PROPERLY
//TODO IMPORTANT A NEWLY REGISTERED USER DOESN'T REGISTER JOIN GROUP PROPERLY
//TODO IMPORTANT A NEWLY REGISTERED USER DOESN'T REGISTER JOIN GROUP PROPERLY
//TODO IMPORTANT A NEWLY REGISTERED USER DOESN'T REGISTER JOIN GROUP PROPERLY
//TODO IMPORTANT A NEWLY REGISTERED USER DOESN'T REGISTER JOIN GROUP PROPERLY
//TODO IMPORTANT A NEWLY REGISTERED USER DOESN'T REGISTER JOIN GROUP PROPERLY


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

        Button mJoinGroupButton = (Button) rootView.findViewById(R.id.join_group);
        mJoinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
                query.getInBackground(mGroupId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e==null) {
                            ArrayList<String> mUserIdArrayList = (ArrayList<String>) parseObject.get("UserIds");
                            if (mUserIdArrayList==null) {
                                mUserIdArrayList = new ArrayList<String>();
                            }
                            mUserIdArrayList.add(mUserId);
                            parseObject.put("UserIds", mUserIdArrayList);
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(getActivity(),"ADDED!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            //TODO ANYTHING??
                        }
                    }
                });
            }
        });

        return rootView;
    }

    /*private class GroupAdapter extends ArrayAdapter<ParseObject> {
        List<ParseObject> mGroups;
        Context mContext;
        public GroupAdapter(Context context, int resource, List<ParseObject> groups) {
            super(context, resource, groups);
            mGroups = groups;
            mContext = context;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = new ViewHolder();

            if (row==null) {
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.fragment_fish_groups_item, parent, false);
                holder.groupNameTextView = (TextView) row.findViewById(R.id.group_name);
                row.setTag(holder);
            }
            else {
                holder = (ViewHolder) row.getTag();
            }
            final ParseObject currentGroup = mGroups.get(position);
            holder.groupNameTextView.setText(currentGroup.get("Name").toString());
            return row;
        }

        class ViewHolder{
            TextView groupNameTextView;
        }
    }*/
}

