package com.ericyuegu.whaddyahavin;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Meal> myMeals = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private Button deleteBtn, exitBtn;
    private FirebaseStorage storage;
    float x1, x2, y1, y2;

    private boolean deleting = false;

    protected void onCreate(Bundle savedInstanceState) {
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        mLayoutManager = new LinearLayoutManager(getApplicationContext()); // use a linear layout manager
        mAdapter = new MainActivityAdapter(myMeals); // specify an adapter

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Meal meal = myMeals.get(position);

                if (!deleting) { // not deleting meals

                    Intent viewMealIntent = new Intent(MainActivity.this, ViewMealActivity.class);
                    viewMealIntent.putExtra("mealName", meal.getMealName());
                    viewMealIntent.putExtra("timestamp", meal.getTimestamp());
                    viewMealIntent.putExtra("photoUrl", meal.getPhotoUrl());
                    viewMealIntent.putExtra("tags", meal.getTags());
                    viewMealIntent.putExtra("description", meal.getDescription());

                    startActivity(viewMealIntent);
                } else {
                    meal.setIsSelected(!meal.getIsSelected());
                    view.setBackgroundColor(meal.getIsSelected() ? getResources().getColor(R.color.colorPrimaryMedium) : getResources().getColor(R.color.colorPrimary));
                }
                return;

            }

            @Override
            public void onLongClick(View view, int position) {
                Meal meal = myMeals.get(position);
                meal.setIsSelected(true);
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryMedium));

                deleting = true;
                deleteBtn = (Button) findViewById(R.id.delete_meals);
                exitBtn = findViewById(R.id.exit_delete);
                deleteBtn.setEnabled(true);
                exitBtn.setEnabled(true);
                deleteBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                exitBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMeals();
                    }
                });

                exitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exitDelete();
                    }
                });

                return;
            }
        }));

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        displayMeals();

    }

    private void deleteMeals() {
        System.out.println("deleting meals");
        showAlert();

    }

    private void exitDelete() {
        int mealNum = myMeals.size() - 1;
        for (Meal meal : myMeals) {
            meal.setIsSelected(false);
            RecyclerView.ViewHolder v = mRecyclerView.findViewHolderForLayoutPosition(mealNum);
            View view = v.itemView;
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mealNum--;
        }

        deleteBtn.setEnabled(false);
        exitBtn.setEnabled(false);
        deleteBtn.setBackgroundColor(getResources().getColor(R.color.platinum));
        exitBtn.setBackgroundColor(getResources().getColor(R.color.platinum));
        deleting = false;
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure you want to delete the selected meals?");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        int mealNum = myMeals.size() - 1;
                        HashMap<Integer, Meal> allMeals = new HashMap<>();
                        HashMap<Integer, Meal> selected = new HashMap<>();
                        ArrayList<Meal> unselected = new ArrayList<>();

                        for (Meal meal : myMeals) {
                            if (!meal.getIsSelected()) { unselected.add(meal); }
                            else { selected.put(mealNum, meal); }

                            allMeals.put(mealNum, meal);
                            mealNum--;
                        }

                        removeData(unselected, selected);

                        deleteBtn.setEnabled(false);
                        exitBtn.setEnabled(false);
                        deleteBtn.setBackgroundColor(getResources().getColor(R.color.platinum));
                        exitBtn.setBackgroundColor(getResources().getColor(R.color.platinum));
                        deleting = false;
                    }
                });

        alertDialog.show();

    }


    private void removeData(ArrayList<Meal> unselected, HashMap<Integer, Meal> selected) {

        final HashMap<Integer, Meal> selectMap = selected;

        for (Integer key : selected.keySet()) { // delete selected meals from database
            String strKey = key.toString();
            final int intKey = key.intValue();

            db.collection("users")
              .document(user.getEmail())
              .collection("meals")
              .document(strKey)
              .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("success");

                    removeFromStorage(intKey); // remove image from storage
//                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("failure");
                }
            });
        }

        startActivity(new Intent(MainActivity.this, MainActivity.class));

    }



    public void removeFromStorage(int mealNum) {
        StorageReference storageRef = storage.getReference();
        StorageReference mealRef = storageRef.child(user.getEmail() + "-" + mealNum + ".jpeg");

        mealRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("success deleting picture");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("failure deleting picture");
            }
        });

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
