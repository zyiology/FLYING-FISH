package com.goldenhand.bleakfalls.flying_fish;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.ParseException;
import java.util.List;

public class ChatListAdapter extends ArrayAdapter<ParseObject> {
    private String mUserId;
    private String mUsername;
    private List<ParseObject> chatMessages;
    private Boolean isMe = false;

    public ChatListAdapter(Context context, String userId, List<ParseObject> chatMessages) {
        super(context, 0, chatMessages);
        this.mUserId = userId;
        this.chatMessages = chatMessages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.chat_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.imageLeft = (ImageView)convertView.findViewById(R.id.ivProfileLeft);
            holder.nameLeft = (TextView) convertView.findViewById(R.id.tvProfileLeft);
            holder.imageRight = (ImageView)convertView.findViewById(R.id.ivProfileRight);
            holder.nameRight = (TextView) convertView.findViewById(R.id.tvProfileRight);
            holder.body = (TextView)convertView.findViewById(R.id.tvBody);
            convertView.setTag(holder);
        }

        final ParseObject chatMessage = chatMessages.get(position);
        final ViewHolder holder = (ViewHolder)convertView.getTag();
        try {
            mUsername = chatMessage.fetchIfNeeded().getString("username");
            isMe = chatMessage.fetchIfNeeded().getString("sender").equals(mUserId);
            holder.body.setText(chatMessage.fetchIfNeeded().getString("message"));
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }



        // Show-hide image based on the logged-in user.
        // Display the profile image to the right for our user, left for other users.
        if (isMe) {
            holder.imageRight.setVisibility(View.VISIBLE);
            holder.imageLeft.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            holder.nameRight.setText(mUsername);
            holder.nameLeft.setText("");
        } else {
            holder.imageLeft.setVisibility(View.VISIBLE);
            holder.imageRight.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            holder.nameLeft.setText(mUsername);
            holder.nameRight.setText("");
        }


        final ImageView profileView = isMe ? holder.imageRight : holder.imageLeft;
        try {
            Picasso.with(getContext()).load(getProfileUrl(chatMessage.fetchIfNeeded().getString("sender"))).into(profileView);
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }



        return convertView;
    }

    // Create a gravatar image based on the hash value obtained from userId
    private static String getProfileUrl(final String userId) {
        String hex = "";
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] hash = digest.digest(userId.getBytes());
            final BigInteger bigInt = new BigInteger(hash);
            hex = bigInt.abs().toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "http://www.gravatar.com/avatar/" + hex + "?d=identicon";
    }

    final class ViewHolder {
        private ImageView imageLeft;
        private TextView nameLeft;
        private ImageView imageRight;
        private TextView nameRight;
        private TextView body;
    }

}
