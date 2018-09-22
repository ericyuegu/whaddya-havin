package com.ericyuegu.whaddyahavin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ericyuegu.whaddyahavin.model.Meal;
import com.ericyuegu.whaddyahavin.model.MealViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    float x1, x2, y1, y2;
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Meal, MealViewHolder> firebaseRecyclerAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("oncreate");
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.gallery_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onstart");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("data");
        mRef.keepSynced(true);

        Query mealQuery = mRef.orderByKey();

        FirebaseRecyclerOptions mealOptions = new FirebaseRecyclerOptions.Builder<Meal>().setQuery(mealQuery, Meal.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Meal, MealViewHolder>(
                mealOptions) {
            @Override
            protected void onBindViewHolder(@NonNull MealViewHolder holder, int position, @NonNull Meal model) {
                System.out.println("Title: " + model.getTitle());
                holder.setMeal(getApplicationContext(), model.getTitle(), model.getDesc(), model.getImage());
            }

            @NonNull
            @Override
            public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.meal_row, parent, false);
                return new MealViewHolder(view);
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onresume");
        firebaseRecyclerAdapter.startListening();
    }

    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    startActivity(new Intent(MainActivity.this, TakePhotoActivity.class));
                }
                if (x1 > x2) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }
                break;
        }
        return false;
    }
}
