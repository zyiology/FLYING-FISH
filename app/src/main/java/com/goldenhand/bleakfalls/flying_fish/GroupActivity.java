package com.goldenhand.bleakfalls.flying_fish;

import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class GroupActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static String GROUP_ID = "GROUP TO DISPLAY";
    private static boolean mIsRegistered;
    private static String mGroupId;
    private static String mUserId;

    private ParseObject group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        if (getIntent().getExtras().containsKey(LoginActivity.REGISTERED_USER_ID)) {
            mIsRegistered = true;
            mUserId = getIntent().getStringExtra(LoginActivity.REGISTERED_USER_ID);
        }
        mGroupId = getIntent().getStringExtra(GROUP_ID);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            editGroupName();
        }

        return super.onOptionsItemSelected(item);


    }

    private void editGroupName() {
        ParseQuery<ParseObject> groupQuery = ParseQuery.getQuery("Group");
        groupQuery.getInBackground(mGroupId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                group = parseObject;
                if (mUserId.equals(parseObject.getString("admin"))) {
                    Intent i = new Intent(GroupActivity.this, EditGroupActivity.class);
                    i.putExtra(EditGroupActivity.GROUP_OBJECT_ID, group.getObjectId());
                    if (mIsRegistered) {
                        i.putExtra(LoginActivity.REGISTERED_USER_ID, mUserId);
                    }
                    startActivity(i);
                }
            }
        });
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    GroupDetailFragment gf = new GroupDetailFragment();
                    gf.newInstance(position + 1, mUserId, mIsRegistered, mGroupId);
                    return gf;

                case 1:
                    GroupChatFragment gcf = new GroupChatFragment();
                    gcf.newInstance(position + 1, mUserId, mIsRegistered, mGroupId);
                    return gcf;

                case 2:
                    GroupUploadFragment guf = new GroupUploadFragment();
                    guf.newInstance(position+1, mUserId, mIsRegistered, mGroupId);
                    return guf;
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section4).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section5).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section6).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_group, container, false);
            return rootView;
        }
    }

}
