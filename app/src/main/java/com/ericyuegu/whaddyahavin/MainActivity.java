package com.ericyuegu.whaddyahavin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Meal> myMeals = new ArrayList<>();
    float x1, x2, y1, y2;

    protected void onCreate(Bundle savedInstanceState) {
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
                Toast.makeText(getApplicationContext(), meal.getName() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent viewMealIntent = new Intent(MainActivity.this, ViewMealActivity.class);
                viewMealIntent.putExtra("mealName", meal.getName());
                viewMealIntent.putExtra("timestamp", meal.getTimestamp());
                viewMealIntent.putExtra("photoUrl", meal.getUrl());

                for (int i = 0; i < meal.getTags().size(); i++) {
                    String tagName = "tag" + i;
                    viewMealIntent.putExtra(tagName, meal.getTags().get(i));
                }

                startActivity(viewMealIntent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        displayMeals();
        System.out.println(myMeals);
    }

    private void displayMeals() {
        for (int i = 0; i < 10; i++) {
            ArrayList<String> tags = new ArrayList<String>(Arrays.asList("Food", "Sugar", "Items"));
            Meal meal = new Meal("Pancakes", "2018-09-25", tags, "download.jpeg");
            myMeals.add(meal);
        }

        mAdapter.notifyDataSetChanged();
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
