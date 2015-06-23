package com.goldenhand.bleakfalls.flying_fish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GroupUploadFragment extends Fragment {
    private final String ARG_SECTION_NUMBER = "section_number";

    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;

    static List<ParseObject> mGroupList;

    private static String mUserId;
    private static boolean mIsRegistered;
    private static String mGroupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public GroupUploadFragment newInstance(int sectionNumber, String userId, boolean isRegistered, String groupId) {
        GroupUploadFragment fragment = new GroupUploadFragment();
        Bundle args = new Bundle();
        mUserId = userId;
        mIsRegistered = isRegistered;
        mGroupId = groupId;
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public GroupUploadFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_fish_group_upload, container, false);

        final ListView lv = (ListView) rootView.findViewById(R.id.user_list);
        final GridView gridView = (GridView) rootView.findViewById(R.id.gridView);

        ParseQuery query = ParseQuery.getQuery("Group");
        query.getInBackground(mGroupId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                ArrayList<String> mImageText = (ArrayList<String>) parseObject.get("Images");
                ArrayList<Bitmap> images = new ArrayList<Bitmap>();
                if (mImageText != null) {
                    for (int i=0;i<mImageText.size();i++) {
                        images.add(stringToBitmap(mImageText.get(i)));
                    }
                    GridViewAdapter gridAdapter = new GridViewAdapter(getActivity(), R.layout.fragment_fish_group_upload_item, images);
                    gridView.setAdapter(gridAdapter);
                } else {
                    //GridViewAdapter gridAdapter = new GridViewAdapter(getActivity(), R.layout.fragment_fish_group_upload_item, getData());
                    //gridView.setAdapter(gridAdapter);

                    /*Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);*/

                }
            }
        });

        Button mAddImageButton = (Button) rootView.findViewById(R.id.add_image);
        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });
        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    final String newImage = bitmapToString(bitmap);
                    ParseQuery query = ParseQuery.getQuery("Group");
                    query.getInBackground(mGroupId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            ArrayList<String> images = (ArrayList<String>) parseObject.get("Images");
                            if (images==null) {
                                images = new ArrayList<String>();
                            }
                            images.add(newImage);
                            parseObject.put("Images", images);
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(getActivity(),"SAVED",Toast.LENGTH_SHORT).show();
                                    
                                }
                            });
                        }
                    });
                }
                catch (java.io.FileNotFoundException e) {
                    Toast.makeText(getActivity(),"IMAGE NOT FOUND", Toast.LENGTH_LONG).show();
                }
                catch (java.io.IOException e) {
                    Toast.makeText(getActivity(), "INPUT NOT RECEIVED", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public final static String bitmapToString(Bitmap in){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        in.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        return Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
    }
    public final static Bitmap stringToBitmap(String in){
        byte[] bytes = Base64.decode(in, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /*private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(bitmap);
        }
        imgs.recycle();
        return imageItems;
    }*/

    public class GridViewAdapter extends ArrayAdapter {
        private Context context;
        private int layoutResourceId;
        private ArrayList data = new ArrayList();

        public GridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) row.findViewById(R.id.image);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            Bitmap item = (Bitmap) data.get(position);
            holder.image.setImageBitmap(item);
            return row;
        }

        class ViewHolder {
            TextView imageTitle;
            ImageView image;
        }
    }
}

