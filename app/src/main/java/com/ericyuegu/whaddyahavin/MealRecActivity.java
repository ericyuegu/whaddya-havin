package com.ericyuegu.whaddyahavin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;

import com.ericyuegu.whaddyahavin.auth.HomescreenActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MealRecActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_rec);

        // set an enter/exit transition
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.RIGHT));

        //get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MealRecActivity.this, HomescreenActivity.class)); // log back in
                    finish();
                }
            }
        };

        ImageView firstImage = findViewById(R.id.meal_first_image);
        ImageView secondImage = findViewById(R.id.meal_second_image);
        ImageView thirdImage = findViewById(R.id.meal_third_image);
        final List<ImageView> imageList = new ArrayList<>(Arrays.asList(firstImage, secondImage, thirdImage));




        DocumentReference documentReference = db.collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String res = document.getData().toString();
                        String type = res.substring(6, res.length()-1);

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReference();
                        for (int i = 0; i < imageList.size(); i++) {
                            StorageReference imagesRef = storageReference.child(type + "-" + i +".jpg");
                            GlideApp.with(getApplicationContext()).load(imagesRef).centerCrop().into(imageList.get(i));
                        }

                        Log.d("HI", "DocumentSnapshot data: " + type);
                    } else {
                        Log.d("HI", "No such document");
                    }
                } else {
                    Log.d("HI", "get failed with ", task.getException());
                }
            }
        });



    }
}
