package com.ericyuegu.whaddyahavin;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Meal> myMeals = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    float x1, x2, y1, y2;

    protected void onCreate(Bundle savedInstanceState) {
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // specify an adapter (see also next example)
        mAdapter = new MainActivityAdapter(myMeals);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Meal meal = myMeals.get(position);
                // Toast.makeText(getApplicationContext(), meal.getMealName() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent viewMealIntent = new Intent(MainActivity.this, ViewMealActivity.class);
                viewMealIntent.putExtra("mealName", meal.getMealName());
                viewMealIntent.putExtra("timestamp", meal.getTimestamp());
                viewMealIntent.putExtra("photoUrl", meal.getPhotoUrl());
                viewMealIntent.putExtra("tags", meal.getTags());
                viewMealIntent.putExtra("description", meal.getDescription());

                startActivity(viewMealIntent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        displayMeals();
    }

    private void displayMeals() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        CollectionReference docRef = db.collection("users").document(user.getEmail()).collection("meals");
        Query query = docRef.orderBy("timestamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot query = task.getResult();

                    if (query != null) {
                        ArrayList<DocumentSnapshot> documents = new ArrayList<DocumentSnapshot>(query.getDocuments());

                        for (DocumentSnapshot doc : documents) {
                            Meal meal = doc.toObject(Meal.class);
                            myMeals.add(meal);
                        }

                        mAdapter.notifyDataSetChanged();

                    } else {
                        Log.d(TAG, "Invalid query");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

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
                    // set an enter/exit transition
                    getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
                    getWindow().setExitTransition(new Slide(Gravity.RIGHT));

                    // Check if we're running on Android 5.0 or higher
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Apply activity transition
                        startActivity(new Intent(MainActivity.this, TakePhotoActivity.class),
                                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    } else {
                        // Swap without transition
                        startActivity(new Intent(MainActivity.this, TakePhotoActivity.class));
                    }
                }
                if (x1 > x2) {
                    // set an enter/exit transition
                    getWindow().setEnterTransition(new Slide(Gravity.LEFT));
                    getWindow().setExitTransition(new Slide(Gravity.LEFT));

                    // Check if we're running on Android 5.0 or higher
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Apply activity transition
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class),
                                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    } else {
                        // Swap without transition
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    }
                }
                break;
        }
        return false;
    }
}
