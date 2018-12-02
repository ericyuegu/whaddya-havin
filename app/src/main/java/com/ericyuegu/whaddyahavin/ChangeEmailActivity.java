package com.ericyuegu.whaddyahavin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ericyuegu.whaddyahavin.auth.HomescreenActivity;
import com.ericyuegu.whaddyahavin.auth.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailActivity extends AppCompatActivity {

    private Button changeEmail, sendConfirmation;
    private EditText oldEmail, newEmail;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_email);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Profile Settings");
//        setSupportActionBar(toolbar);

        // set an enter/exit transition
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.RIGHT));

        //get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ChangeEmailActivity.this, HomescreenActivity.class)); // log back in
                    finish();
                }
            }
        };

        oldEmail = findViewById(R.id.email);
        newEmail = findViewById(R.id.newemail);
        changeEmail = findViewById(R.id.change_email_btn);
        sendConfirmation = findViewById(R.id.send_confirmation);

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(newEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangeEmailActivity.this, "Email address has been updated. Please sign in with your new email!", Toast.LENGTH_LONG).show();
                                        signOut();
//                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        try { }
                                        catch (Exception e) {
                                            Toast.makeText(ChangeEmailActivity.this, "Failed to update email. Please refresh by logging back in!", Toast.LENGTH_LONG).show();
//                                          progressBar.setVisibility(View.GONE);
                                            System.out.println(task.getException().getMessage());
                                        }

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("in here");
                                    Toast.makeText(ChangeEmailActivity.this, "Failed to update. Please refresh by logging back in!", Toast.LENGTH_LONG).show();
                                }
                            });
                } else if (newEmail.getText().toString().trim().equals("")) {
                    newEmail.setError("Enter email");
//                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }

    //sign out method
    public void signOut() {
        auth.signOut();
        startActivity(new Intent(ChangeEmailActivity.this, HomescreenActivity.class)); // log back in
    }
}
