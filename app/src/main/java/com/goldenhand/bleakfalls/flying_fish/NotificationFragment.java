package com.goldenhand.bleakfalls.flying_fish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private static String mUserId;
    private static Boolean mIsRegistered;
    private final String ARG_SECTION_NUMBER = "notification section number";

    private ListView notificationLV;
    private NotificationAdapter notificationAdapter;

    private List<ParseObject> myNotifications;
    private List<ParseObject> myFriends;
    private List<ParseObject> theirFriends;

    private List<String> groupMembers;

    private Toast toast;

    public NotificationFragment newInstance(int sectionNumber, String userId, boolean isRegistered) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        mUserId = userId;
        mIsRegistered = isRegistered;
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        toast = Toast.makeText(getActivity(),"",Toast.LENGTH_SHORT);

        ParseQuery<ParseObject> notifQuery = ParseQuery.getQuery("Notification");
        notifQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                myNotifications = new ArrayList<>();
                for (ParseObject notification : list) {
                    if (notification.getString("to").equals(mUserId)) {
                        System.out.println(mUserId);
                        myNotifications.add(notification);
                    }
                }
                notificationAdapter = new NotificationAdapter(getActivity(), R.layout.fragment_notification_item, myNotifications);
                notificationLV = (ListView) rootView.findViewById(R.id.notification_list);
                notificationLV.setAdapter(notificationAdapter);
                setUpNotificationOnClick();
            }
        });

        return rootView;
    }

    private void setUpNotificationOnClick() {
        notificationLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ParseObject selectedNotification = (ParseObject) notificationLV.getItemAtPosition(position);
                if (selectedNotification.getBoolean("isFriendNotif")) {
                    ParseQuery<ParseUser> selfQuery = ParseUser.getQuery();
                    selfQuery.getInBackground(mUserId, new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e == null) {
                                final ParseUser me = parseUser;
                                ParseQuery<ParseUser> newFriendQuery = ParseUser.getQuery();
                                newFriendQuery.getInBackground(selectedNotification.getString("from"), new GetCallback<ParseUser>() {
                                    @Override
                                    public void done(ParseUser parseUser, ParseException e) {
                                        if (e == null) {
                                            final ParseUser them = parseUser;
                                            myFriends = me.getList("friends");
                                            myFriends.add(parseUser);
                                            theirFriends = parseUser.getList("friends");
                                            theirFriends.add(me);

                                            me.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    them.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            myNotifications.remove(selectedNotification);
                                                            selectedNotification.deleteInBackground(new DeleteCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    notificationAdapter.notifyDataSetChanged();

                                                                }
                                                            });


                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }
                    });
                } else {
                    ParseQuery<ParseObject> groupQuery = ParseQuery.getQuery("Group");
                    groupQuery.getInBackground(selectedNotification.getString("groupId"), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            groupMembers = parseObject.getList("UserIds");
                            groupMembers.add(selectedNotification.getString("from"));
                            parseObject.put("UserIds", groupMembers);
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    myNotifications.remove(selectedNotification);
                                    selectedNotification.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            notificationAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

            }
        });
    }

    private class NotificationAdapter extends ArrayAdapter<ParseObject> {
        List<ParseObject> notifications;
        Context context;

        public NotificationAdapter(Context context, int resource, List<ParseObject> notifications) {
            super(context, resource, notifications);
            this.notifications = notifications;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.fragment_notification_item, null);
            }

            holder.notificationTextView = (TextView) convertView.findViewById(R.id.notification_title);
            convertView.setTag(holder);

            ParseObject notification = notifications.get(position);

            if (notification.getBoolean("isFriendNotif")) {
                holder.notificationTextView.setText(notification.getString("fromName")+" wants to be your friend!");
            } else {
                holder.notificationTextView.setText(notification.getString("fromName")+" wants to join group "+
                                                    notification.getString("groupName")+"!");
            }

            return convertView;
        }

        class ViewHolder{
            TextView notificationTextView;
        }
    }

}
