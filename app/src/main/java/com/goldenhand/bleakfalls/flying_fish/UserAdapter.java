package com.goldenhand.bleakfalls.flying_fish;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class UserAdapter extends ArrayAdapter<ParseUser> {
    List<ParseUser> mUsers;
    Context context;
    int resource;

    public UserAdapter(Context context, int resource, List<ParseUser> mUsers) {
        super(context, resource, mUsers);
        this.mUsers = mUsers;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }
        holder.username = (TextView) convertView.findViewById(R.id.add_friend_username);
        convertView.setTag(holder);

        final ParseUser currentUser = mUsers.get(position);
        holder.username.setText(currentUser.getUsername());

        return convertView;
    }

    final class ViewHolder {
        private TextView username;
    }
}
