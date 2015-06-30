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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Default on 24/5/2015.
 */

public class GroupListFragment extends Fragment {
    private final String ARG_SECTION_NUMBER = "section_number";
    static List<ParseObject> mGroupList;

    private static String mUserId;
    private static Boolean mIsRegistered;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public GroupListFragment newInstance(int sectionNumber, String userID, boolean isRegistered) {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        mUserId = userID;
        mIsRegistered = isRegistered;
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public GroupListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_fish_groups, container, false);
        System.out.println("CREATING FRAGMENT");

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    mGroupList = list;
                    GroupAdapter mGroupAdapter = new GroupAdapter(getActivity(), R.layout.fragment_fish_groups_item, mGroupList);
                    System.out.println("DONNNNNNNNNNNNNNNNNNNNE");
                    ListView lv = (ListView) rootView.findViewById(R.id.groups_list);
                    lv.setAdapter(mGroupAdapter);
                } else {
                    System.out.println("FAILURE U IDIOT");
                }
            }
        });


        final ListView lv = (ListView) rootView.findViewById(R.id.groups_list);
        Button mAddGroupButton = (Button) rootView.findViewById(R.id.add_group);
        mAddGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject newGroup = new ParseObject("Group");
                newGroup.put("Name", "New Group");
                ArrayList<String> userList = new ArrayList<>();
                userList.add(mUserId);
                newGroup.put("UserIds", userList);
                newGroup.put("groupMessageArray", new ArrayList<>());
                newGroup.put("admin", mUserId);
                newGroup.put("ImagesLikes", new ArrayList<ArrayList<String>>());
                newGroup.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    mGroupList = list;
                                    GroupAdapter mGroupAdapter = new GroupAdapter(getActivity(), R.layout.fragment_fish_groups_item, mGroupList);
                                    System.out.println("DONNNNNNNNNNNNNNNNNNNNE");
                                    lv.setAdapter(mGroupAdapter);
                                } else {
                                    System.out.println("FAILURE U IDIOT");
                                }
                            }
                        });
                    }
                });
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject group = (ParseObject) lv.getItemAtPosition(position);
                Intent i = new Intent(getActivity(), PreGroupActivity.class);
                i.putExtra(GroupActivity.GROUP_ID,group.getObjectId());
                if (mIsRegistered) {
                    i.putExtra(LoginActivity.REGISTERED_USER_ID, mUserId);
                }
                startActivity(i);
            }
        });


        return rootView;
    }

    private class GroupAdapter extends ArrayAdapter<ParseObject> {
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
    }
}

