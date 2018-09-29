package com.ericyuegu.whaddyahavin;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder> {
    private List<Meal> mDataset;
    private FirebaseUser user;
    private FirebaseStorage storage;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MainActivityViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name, timestamp, tags, description;
        public ImageView meal_image;
        public MainActivityViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            timestamp = (TextView) v.findViewById(R.id.timestamp);
            meal_image = (ImageView) v.findViewById(R.id.meal_image);
            tags = (TextView) v.findViewById(R.id.tags);
            description = (TextView) v.findViewById(R.id.description);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainActivityAdapter(List<Meal> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainActivityAdapter.MainActivityViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_meal_row, parent, false);

        MainActivityViewHolder vh = new MainActivityViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MainActivityViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final MainActivityViewHolder viewHolder = holder;
        Meal meal = mDataset.get(position);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathRef = storageRef.child(meal.getPhotoUrl());

        final long ONE_MEGABYTE = 1024 * 1024;
        pathRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap scaled = Bitmap.createScaledBitmap(bmp, 300, 300, false);
                viewHolder.meal_image.setImageBitmap(scaled);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Unable to load image");
                System.out.println(e.getMessage());
            }
        });

//        viewHolder.tags.setText(meal.getTags());
        viewHolder.name.setText(meal.getMealName());
//        viewHolder.description.setText(meal.getDescription());
        viewHolder.timestamp.setText(meal.getTimestamp());

    }

    private void addToRow(MainActivityViewHolder holder, Meal meal, Bitmap bmp) {




    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}


