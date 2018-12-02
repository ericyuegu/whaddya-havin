package com.ericyuegu.whaddyahavin;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.ericyuegu.whaddyahavin.auth.HomescreenActivity;
import com.ericyuegu.whaddyahavin.auth.LoginActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ericgu on 9/5/18.
 */
public class ProfileActivity extends AppCompatActivity {

    private Button btnChangeEmail, btnChangePassword, btnSendResetEmail, btnRemoveUser, btnGoHome,
            changeEmail, changePassword, sendEmail, remove, signOut, btnChangeDiet, btnMealRec;

    private EditText oldEmail, newEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;

    float x1, x2, y1, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile Settings");
        setSupportActionBar(toolbar);

        // set an enter/exit transition
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.RIGHT));

        storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();

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
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnChangeEmail = findViewById(R.id.change_email_btn);
        btnChangePassword = findViewById(R.id.change_pass_btn);
//        btnSendResetEmail = (Button) findViewById(R.id.sending_pass_reset_btn);
        btnRemoveUser = findViewById(R.id.remove_user_btn);
        btnChangeDiet = findViewById(R.id.change_diet_btn);
        btnMealRec = findViewById(R.id.diet_rec_btn);
//        changeEmail = (Button) findViewById(R.id.changeEmail);
//        changePassword = (Button) findViewById(R.id.changePass);
//        sendEmail = (Button) findViewById(R.id.send);
//        remove = (Button) findViewById(R.id.remove);
        signOut = findViewById(R.id.sign_out_btn);

//        oldEmail = (EditText) findViewById(R.id.old_email);
//        newEmail = (EditText) findViewById(R.id.new_email);
//        password = (EditText) findViewById(R.id.password);
//        newPassword = (EditText) findViewById(R.id.newPassword);

//        oldEmail.setVisibility(View.GONE);
//        newEmail.setVisibility(View.GONE);
//        password.setVisibility(View.GONE);
//        newPassword.setVisibility(View.GONE);
//        changeEmail.setVisibility(View.GONE);
//        changePassword.setVisibility(View.GONE);
//        sendEmail.setVisibility(View.GONE);
//        remove.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (!user.isEmailVerified()) {
            AlertDialog alert = new AlertDialog.Builder(ProfileActivity.this).create();
            alert.setTitle("Verify Email");
            alert.setMessage("Please verify your account! A link has been sent to your email.");
            alert.setButton(Dialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    user.sendEmailVerification().addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("ProfileActivity", "sendEmailVerification", task.getException());
                                Toast.makeText(ProfileActivity.this,
                                        "Failed to send verification email. Ensure email is valid.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            alert.show();
            alert.getButton(Dialog.BUTTON_POSITIVE).setTextColor(0xFF0097A7);
        }

//        if (progressBar != null) {
//            progressBar.setVisibility(View.GONE);
//        }

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                oldEmail.setVisibility(View.GONE);
//                newEmail.setVisibility(View.VISIBLE);
//                password.setVisibility(View.GONE);
//                newPassword.setVisibility(View.GONE);
//                changeEmail.setVisibility(View.VISIBLE);
//                changePassword.setVisibility(View.GONE);
//                sendEmail.setVisibility(View.GONE);
//                remove.setVisibility(View.GONE);
                startActivity(new Intent(ProfileActivity.this, ChangeEmailActivity.class));
            }
        });

//        changeEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//                if (user != null && !newEmail.getText().toString().trim().equals("")) {
//                    user.updateEmail(newEmail.getText().toString().trim())
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(ProfileActivity.this, "Email address has been updated. Please sign in with your new email!", Toast.LENGTH_LONG).show();
//                                        signOut();
//                                        progressBar.setVisibility(View.GONE);
//                                    } else {
//                                        Toast.makeText(ProfileActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
//                                        progressBar.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
//                } else if (newEmail.getText().toString().trim().equals("")) {
//                    newEmail.setError("Enter email");
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                oldEmail.setVisibility(View.GONE);
//                newEmail.setVisibility(View.GONE);
//                password.setVisibility(View.GONE);
//                newPassword.setVisibility(View.VISIBLE);
//                changeEmail.setVisibility(View.GONE);
//                changePassword.setVisibility(View.VISIBLE);
//                sendEmail.setVisibility(View.GONE);
//                remove.setVisibility(View.GONE);
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            }
        });

//        changePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//                if (user != null && !newPassword.getText().toString().trim().equals("")) {
//                    if (newPassword.getText().toString().trim().length() < 6) {
//                        newPassword.setError("Password too short, enter minimum 6 characters");
//                        progressBar.setVisibility(View.GONE);
//                    } else {
//                        user.updatePassword(newPassword.getText().toString().trim())
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(ProfileActivity.this, "Password has been updated, please sign in with your new password!", Toast.LENGTH_SHORT).show();
//                                            signOut();
//                                            progressBar.setVisibility(View.GONE);
//                                        } else {
//                                            Toast.makeText(ProfileActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
//                                            progressBar.setVisibility(View.GONE);
//                                        }
//                                    }
//                                });
//                    }
//                } else if (newPassword.getText().toString().trim().equals("")) {
//                    newPassword.setError("Enter password");
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });

//        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                oldEmail.setVisibility(View.VISIBLE);
//                newEmail.setVisibility(View.GONE);
//                password.setVisibility(View.GONE);
//                newPassword.setVisibility(View.GONE);
//                changeEmail.setVisibility(View.GONE);
//                changePassword.setVisibility(View.GONE);
//                sendEmail.setVisibility(View.VISIBLE);
//                remove.setVisibility(View.GONE);
//                System.out.println("something");
//            }
//        });

//        sendEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//                if (!oldEmail.getText().toString().trim().equals("")) {
//                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(ProfileActivity.this, "Reset password email has been sent!", Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.GONE);
//                                    } else {
//                                        Toast.makeText(ProfileActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
//                } else {
//                    oldEmail.setError("Enter email");
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });

        btnChangeDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                // Inflate the custom layout/view
                final View customView = inflater.inflate(R.layout.activity_change_diet, null);

                final PopupWindow mPopupWindow = new PopupWindow(
                        customView,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                );

                // Set an elevation value for popup window
                // Call requires API level 21
                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }

                Button confirm = customView.findViewById(R.id.confirm);
                Button cancel = customView.findViewById(R.id.cancel);

                // Get a reference for the custom view close button
                final Spinner spinner = customView.findViewById(R.id.diet_spinner);
                // Create an ArrayAdapter using the string array and a default spinner layout
                final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.diets_array, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);

                DocumentReference docRef = db.collection("users").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            System.out.println(task.getResult().get("diet"));
                            String diet = task.getResult().get("diet").toString();
                            setSpinnerValue(diet, spinner, adapter);
                        } else {
                            setSpinnerValue("No Diet", spinner, adapter); // default to no diet if didn't pull correctly
                            Log.d("Failed", "get failed with ", task.getException());
                        }
                    }
                });


                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, String> diet = new HashMap<>();
                        diet.put("diet", spinner.getSelectedItem().toString());

                        db.collection("users")
                                .document(user.getUid())
                                .set(diet)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void documentReference) {
                                    mPopupWindow.dismiss();
                                    System.out.println("User was successfully added.");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("Error adding user.");
                                    }
                                });

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });

                mPopupWindow.showAtLocation(findViewById(R.id.profileLayout), Gravity.CENTER,0,0);
            }
        });

        btnMealRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                oldEmail.setVisibility(View.GONE);
//                newEmail.setVisibility(View.GONE);
//                password.setVisibility(View.GONE);
//                newPassword.setVisibility(View.VISIBLE);
//                changeEmail.setVisibility(View.GONE);
//                changePassword.setVisibility(View.VISIBLE);
//                sendEmail.setVisibility(View.GONE);
//                remove.setVisibility(View.GONE);
                startActivity(new Intent(ProfileActivity.this, MealRecActivity.class));
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.activity_profile_delete_account, null);

                /*
                    public PopupWindow (View contentView, int width, int height)
                        Create a new non focusable popup window which can display the contentView.
                        The dimension of the window must be passed to this constructor.

                        The popup does not provide any background. This should be handled by
                        the content view.

                    Parameters
                        contentView : the popup's content
                        width : the popup's width
                        height : the popup's height
                */
                // Initialize a new instance of popup window
                final PopupWindow mPopupWindow = new PopupWindow(
                        customView,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                );

                // Set an elevation value for popup window
                // Call requires API level 21
                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }

                // Get a reference for the custom view close button
                Button closeButton = customView.findViewById(R.id.cancel);
                Button deleteButton = customView.findViewById(R.id.delete);

                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (user != null) {

                            // delete firebase user
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                System.out.println("task doing something");
                                                Toast.makeText(ProfileActivity.this, "Your profile has been deleted :( Create a account now!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ProfileActivity.this, HomescreenActivity.class));
                                                finish();
        //                                        progressBar.setVisibility(View.GONE);
                                            } else {
                                                System.out.println("exception");
                                                System.out.println(task.getException());
                                                try {
                                                    throw new FirebaseAuthRecentLoginRequiredException("","");
                                                }
                                                catch (Exception e) {
                                                    Toast.makeText(ProfileActivity.this, "Failed to delete account. Please refresh by logging back in!", Toast.LENGTH_LONG).show();
                                                    System.out.println(task.getException().getMessage());
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });

                /*
                    public void showAtLocation (View parent, int gravity, int x, int y)
                        Display the content view in a popup window at the specified location. If the
                        popup window cannot fit on screen, it will be clipped.
                        Learn WindowManager.LayoutParams for more information on how gravity and the x
                        and y parameters are related. Specifying a gravity of NO_GRAVITY is similar
                        to specifying Gravity.LEFT | Gravity.TOP.

                    Parameters
                        parent : a parent view to get the getWindowToken() token from
                        gravity : the gravity which controls the placement of the popup window
                        x : the popup's x location offset
                        y : the popup's y location offset
                */
                // Finally, show the popup window at the center location of root relative layout
                mPopupWindow.showAtLocation(findViewById(R.id.profileLayout), Gravity.CENTER,0,0);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    public void setSpinnerValue(String diet, Spinner spinner, ArrayAdapter<CharSequence> adapter) {

        int spinnerPosition = adapter.getPosition(diet); // set default value of spinner
        spinner.setSelection(spinnerPosition);

        return;
    }

    //sign out method
    public void signOut() {
        auth.signOut();
        Toast.makeText(ProfileActivity.this, "You are logged out", Toast.LENGTH_SHORT).show();
        LoginManager.getInstance().logOut();
        startActivity(new Intent(ProfileActivity.this, HomescreenActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
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
                    // Check if we're running on Android 5.0 or higher
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Apply activity transition
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class),
                                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    } else {
                        // Swap without transition
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    }
                }
                break;
        }
        return false;
    }
}