package com.ericyuegu.whaddyahavin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewMealActivity extends AppCompatActivity {
    private FirebaseStorage storage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meal);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            TextView mealName = this.findViewById(R.id.name);
            mealName.setText(bundle.getString("mealName"));

            TextView timestamp = this.findViewById(R.id.timestamp);
            timestamp.setText(bundle.getString("timestamp"));

            String tagStr = "";

            for (String key : bundle.keySet()) {

                if (key.contains("tag")) {
                    String tag = bundle.getString(key);
                    tagStr += tag + ", ";
                }
            }

            TextView tags = this.findViewById(R.id.tags);
            tags.setText(tagStr.substring(0, tagStr.length() - 2));

            final ImageView mealImg = this.findViewById(R.id.meal_image);

            storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference pathRef = storageRef.child(bundle.getString("photoUrl"));

            final long ONE_MEGABYTE = 1024 * 1024;
            pathRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap scaled = Bitmap.createScaledBitmap(bmp, 150, 150, false);
                    mealImg.setImageBitmap(scaled);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Unable to load image");
                    System.out.println(e.getMessage());
                }
            });
        }
    }
}
