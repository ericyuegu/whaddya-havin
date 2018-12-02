package com.ericyuegu.whaddyahavin.auth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ericyuegu.whaddyahavin.MainActivity;
import com.ericyuegu.whaddyahavin.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

/**
 * Created by ericgu on 9/5/18.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "FACEBOOK LOGIN";
    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private Button facebook_button;
    private FirebaseAuth auth;
    private CallbackManager mCallbackManager;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // set the view now

        auth = FirebaseAuth.getInstance(); // get Firebase auth instance

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        facebook_button = findViewById(R.id.facebook_button);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), R.string.prompt_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.prompt_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    final FirebaseUser user = auth.getCurrentUser();

                                    if (!user.isEmailVerified()) { // user not verified yet
                                        AlertDialog alert = new AlertDialog.Builder(LoginActivity.this).create();
                                        alert.setTitle("Verify Email");
                                        alert.setMessage("Must verify account first! Link sent to your email.");
                                        alert.setButton(Dialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Write your code here to execute after dialog closed
                                                user.sendEmailVerification().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(LoginActivity.this,
                                                                    "Verification email sent to " + user.getEmail(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.e("MainActivity", "sendEmailVerification", task.getException());
                                                            Toast.makeText(LoginActivity.this,
                                                                    "Failed to send verification email. Ensure email is valid.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                auth.signOut();
                                                finish();
                                            }
                                        });
                                        alert.show();
                                        alert.getButton(Dialog.BUTTON_POSITIVE).setTextColor(0xFF0097A7);
                                    } else { // user is verified -- allow login
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            }
                        });
            }
        });

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.facebook_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) { // user already logged in through Firebase
            user = currentUser; // current Firebase user

//            if (!user.isEmailVerified()) { // user not verified yet
//                AlertDialog alert = new AlertDialog.Builder(LoginActivity.this).create();
//                alert.setTitle("Verify Email");
//                alert.setMessage("Must verify account first! Link sent to your email.");
//                alert.setButton(Dialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // send verification email
//                        user.sendEmailVerification().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(LoginActivity.this,
//                                            "Verification email sent to " + user.getEmail(),
//                                            Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Log.e("MainActivity", "sendEmailVerification", task.getException());
//                                    Toast.makeText(LoginActivity.this,
//                                            "Failed to send verification email. Ensure email is valid.",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                        auth.signOut();
//                        finish();
//                    }
//                });
//                alert.show();
//                alert.getButton(Dialog.BUTTON_POSITIVE).setTextColor(0xFF0097A7);
//            } else { // user is verified -- allow login
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                Toast.makeText(LoginActivity.this,"Welcome!", Toast.LENGTH_SHORT).show();
//                finish();
//            }
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            Toast.makeText(LoginActivity.this,"Welcome!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}