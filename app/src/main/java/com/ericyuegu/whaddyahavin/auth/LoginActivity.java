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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ericyuegu.whaddyahavin.MainActivity;
import com.ericyuegu.whaddyahavin.ProfileActivity;
import com.ericyuegu.whaddyahavin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by ericgu on 9/5/18.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnLogin, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance(); // Get Firebase auth instance

        if (auth.getCurrentUser() != null) {
            final FirebaseUser user = auth.getCurrentUser(); // current Firebase user

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

        // set the view now
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
//        btnReset = (Button) findViewById(R.id.btn_reset_password);

//        btnSignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//            }
//        });

//        btnReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
//            }
//        });

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

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
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
//                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                    startActivity(intent);
//                                    finish();
                                }
                            }
                        });
            }
        });
    }
}