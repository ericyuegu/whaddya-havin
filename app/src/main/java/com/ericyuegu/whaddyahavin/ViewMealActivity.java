package com.ericyuegu.whaddyahavin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ViewMealActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meal);

//        String newString;
//        if (savedInstanceState == null) {
//            Bundle extras = getIntent().getExtras();
//            if(extras == null) {
//                newString= null;
//            } else {
//                newString= extras.get("photo_uri");
//            }
//        } else {
//            newString= (String) savedInstanceState.getSerializable("photo_uri");
//        }

        // System.out.println("newString: " + newString);

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("photo_uri");

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView image = findViewById(R.id.meal_image);

        image.setImageBitmap(bmp);

//        String photoUri = getIntent().getExtras().getString("photo_uri");
//        System.out.println();
//        if (photoUri != null)
//        {
//            Uri path = Uri.parse(photoUri);
//            ImageView img = (ImageView) findViewById(R.id.meal_image);
//            img.setImageURI(path);
//        }

    }
}
