package com.goldenhand.bleakfalls.flying_fish;

import android.content.Context;
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

import java.util.List;

/**
 * Created by Default on 24/5/2015.
 */

public class GroupFragment extends Fragment {
    private final String ARG_SECTION_NUMBER = "section_number";
    static List<ParseObject> mGroupList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public GroupFragment newInstance(int sectionNumber) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public GroupFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_fish_groups, container, false);
        System.out.println("CREATING FRAGMENT");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e==null) {
                    mGroupList = list;
                    System.out.println("DONNNNNNNNNNNNNNNNNNNNE");
                    GroupAdapter mGroupAdapter = new GroupAdapter(getActivity(), R.layout.fragment_fish_groups_item,mGroupList);
                    ListView lv = (ListView) rootView.findViewById(R.id.groups_list);
                    lv.setAdapter(mGroupAdapter);
                }
                else {
                    System.out.println("FAILURE U IDIOT");
                }
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

