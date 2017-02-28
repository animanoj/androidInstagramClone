package com.example.ani.instagramclone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UserImageActivity extends AppCompatActivity {

    LinearLayout imageLayout;

    Boolean myFeed;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_image);

        myFeed = username.equals(ParseUser.getCurrentUser().getUsername());

        if (myFeed)
            setTitle("Your Feed");
        else
            setTitle(username + "'s Feed");

        imageLayout = (LinearLayout) findViewById(R.id.imageLayout);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("username", username);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        ParseFile file = objects.get(i).getParseFile("image");
                        final int finalI = i;
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    ImageView imageView = new ImageView(getApplicationContext());
                                    imageView.setImageBitmap(image);

                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));
                                    int padding_in_dp = 5;
                                    float scale = getResources().getDisplayMetrics().density;
                                    int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
                                    imageView.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);
                                    if (myFeed) {
                                        imageView.setOnLongClickListener(new View.OnLongClickListener() {
                                            @Override
                                            public boolean onLongClick(View v) {
                                                final ImageView imageView1 = (ImageView) v;

                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
                                                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                                                query.whereEqualTo("objectId", imageView1.getTag());
                                                query.setLimit(1);
                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> objects, ParseException e) {
                                                        if (e == null) {
                                                            if (objects.size() > 0) {
                                                                objects.get(0).deleteInBackground(new DeleteCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        if (e == null) {
                                                                            imageLayout.removeView(imageView1);
                                                                            Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                                                                        } else
                                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            } else
                                                                Toast.makeText(getApplicationContext(), "Delete failed - try again later", Toast.LENGTH_SHORT).show();
                                                        } else
                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                return false;
                                            }
                                        });
                                    }

                                    imageView.setTag(objects.get(finalI).getObjectId());
                                    imageLayout.addView(imageView);
                                } else
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (myFeed)
            Toast.makeText(getApplicationContext(), "Hold an image to remove from your feed", Toast.LENGTH_SHORT).show();
    }
}
