package com.ericyuegu.whaddyahavin;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

public class ViewMealActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meal);

        String photoUri = getIntent().getExtras().getString("photo_uri");
        if (photoUri != null)
        {
            Uri path = Uri.parse(photoUri);
            ImageView img = (ImageView) findViewById(R.id.meal_image);
            img.setImageURI(path);
        }

    }
}
