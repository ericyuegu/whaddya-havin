package com.ericyuegu.whaddyahavin;

import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
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
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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
//                deleteBtn.setBackgroundColor(getResources().getColor(R.id./buttonstyle));
                deleteBtn.setBackground(getResources().getDrawable(R.drawable.buttonstyle));
                deleteBtn.setTextColor(getResources().getColor(R.color.white));
//                exitBtn.setBackgroundColor(getResources().getColor(R.color.red));
                exitBtn.setBackground(getResources().getDrawable(R.drawable.buttonstyle));
                exitBtn.setTextColor(getResources().getColor(R.color.white));

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
        createNotificationChannel();
        setNotification();

    }

    private void deleteMeals() {
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
//        deleteBtn.setBackgroundColor(getResources().getColor(R.color.platinum));
        deleteBtn.setBackground(getResources().getDrawable(R.drawable.buttonstyle4));
        deleteBtn.setTextColor(getResources().getColor(R.color.black));
//        exitBtn.setBackgroundColor(getResources().getColor(R.color.platinum));
        exitBtn.setBackground(getResources().getDrawable(R.drawable.buttonstyle4));
        exitBtn.setTextColor(getResources().getColor(R.color.black));
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
              .document(user.getUid())
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
        StorageReference mealRef = storageRef.child(user.getUid() + "-" + mealNum + ".jpeg");

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

        CollectionReference docRef = db.collection("users").document(user.getUid()).collection("meals");
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

    private void setNotification() {
        Calendar calendar = Calendar.getInstance();

        System.out.println("abc123");
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getApplicationContext(), Notification_reciever.class);
        intent.setAction("MY_NOTIFICATION_MESSAGE");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 6000L, pendingIntent);

    }

    private void createNotificationChannel() {
        String CHANNEL_ID = "123";
        String channel_name = "channel_name";
        String channel_description = "channel_description";
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channel_name, importance);
            channel.setDescription(channel_description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
