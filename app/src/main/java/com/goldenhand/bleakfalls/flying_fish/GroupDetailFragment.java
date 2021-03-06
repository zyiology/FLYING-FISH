package com.goldenhand.bleakfalls.flying_fish;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class GroupDetailFragment extends Fragment {
    private final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList<ParseUser> mUserArrayList;
    private GroupAdapter mGroupAdapter;

    private ListView lv;

    private static String mUserId;
    private static boolean mIsRegistered;
    private static String mGroupId;

    private AlertDialog alertDialog;
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

        lv = (ListView) rootView.findViewById(R.id.user_list);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        query.getInBackground(mGroupId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                final ArrayList<String> mUserIdArrayList = (ArrayList<String>) parseObject.get("UserIds");
                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        mUserArrayList = new ArrayList<ParseUser>();
                        if (mUserIdArrayList!=null) {
                            for (int i = 0; i < list.size(); i++) {
                                if (mUserIdArrayList.contains(list.get(i).getObjectId())) {
                                    mUserArrayList.add(list.get(i));
                                }
                            }
                            mGroupAdapter = new GroupAdapter(getActivity(), R.layout.fragment_fish_group_detail, mUserArrayList);
                            lv.setAdapter(mGroupAdapter);
                            setUpGroupDetailOnClick();
                        }
                    }
                });
            }
        });

        return rootView;
    }

    private void setUpGroupDetailOnClick() {
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ParseUser selectedUser = (ParseUser) lv.getItemAtPosition(position);

                ParseQuery<ParseObject> groupQuery = ParseQuery.getQuery("Group");
                groupQuery.getInBackground(mGroupId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject.getString("admin").equals(mUserId) && !selectedUser.getObjectId().equals(mUserId)) {
                            alertDialog = confirmUserKickDialog(selectedUser, parseObject);
                            alertDialog.show();
                        }
                    }
                });
                return false;
            }
        });
    }

    private AlertDialog confirmUserKickDialog(final ParseUser user, final ParseObject group) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_warning)
                .setMessage(getText(R.string.kick_user)+" "+user.getUsername()+" from group?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> userIds = group.getList("UserIds");
                        userIds.remove(user.getObjectId());
                        group.put("UserIds", userIds);
                        group.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                mUserArrayList.remove(user);
                                mGroupAdapter.notifyDataSetChanged();
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

