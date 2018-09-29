package com.ericyuegu.whaddyahavin;

import android.*;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ericyuegu.whaddyahavin.auth.LoginActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import io.grpc.Context;

public class SaveMealActivity extends AppCompatActivity {

    private static final String TAG = "SaveMealActivity";

    private EditText mealName, mealDesc, mealTags;
    private Button chooseBtn, saveBtn;
    private Meal m;
    private ImageView mealImg;
    private ProgressBar progressBar;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Uri mImageUri;
    private final int PICK_IMAGE_REQUEST = 1;

//    private ArrayList<String> pathArray;
//    private int array_position;

    private StorageReference mStorageref;
    private DatabaseReference mDatabaseref;
    private FirebaseStorage storage;
    private FirebaseUser user;
//    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_meal);
        mealName= (EditText) findViewById(R.id.nameEditText);
        mealDesc = (EditText) findViewById(R.id.mealdescripEditText);
        saveBtn = (Button) findViewById(R.id.savebtn);
        mealImg = (ImageView) findViewById(R.id.meal_image);
        mealTags = (EditText) findViewById(R.id.tags);
//        chooseBtn = (Button) findViewById(R.id.choosebtn);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mStorageref = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseref = FirebaseDatabase.getInstance().getReference("uploads");

        Bundle bundle = getIntent().getExtras();

        user = FirebaseAuth.getInstance().getCurrentUser();

        // display passed in picture that was taken
        if (bundle != null) {
            storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            byte[] bytes = (byte[]) bundle.get("photo_uri");

            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap scaled = Bitmap.createScaledBitmap(bmp, 50, 50, false);
            mealImg.setImageBitmap(scaled);
        }

//
//        chooseBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openFileChooser();
//            }
//        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            //Picasso.with(this).load(mImageUri).into(mealimage;
            mealImg.setImageURI(mImageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    // saving the file on firebase - database and storage
    private void uploadFile() {

        CollectionReference docRef = db.collection("users").document(user.getEmail()).collection("meals");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot query = task.getResult();
                    System.out.println(query);

                    if (query != null) {
                        final int mealNum = query.getDocuments().size();

                        HashMap<String, String> uploadMeal = new HashMap<>();
                        uploadMeal.put("mealName", mealName.getText().toString());
                        uploadMeal.put("timestamp", Calendar.getInstance().getTime().toString());
                        uploadMeal.put("tags", mealTags.getText().toString());
                        uploadMeal.put("photoUrl", user.getEmail() + "-" + mealNum + ".jpeg");
                        uploadMeal.put("description", mealDesc.getText().toString());

                        db.collection("users")
                                .document(user.getEmail())
                                .collection("meals")
                                .document(Integer.toString(mealNum))
                                .set(uploadMeal)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void documentReference) {
                                        System.out.println("Meal was successfully added.");
                                        uploadToStorage(mealNum);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("Error adding user.");
                                    }
                                });
                    } else {
                        Log.d(TAG, "Invalid query");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void uploadToStorage(int mealNum) {
        StorageReference storageRef = storage.getReference();
        StorageReference mealRef = storageRef.child(user.getEmail() + "-" + mealNum + ".jpeg");

        Bundle bundle = getIntent().getExtras();
        byte[] bytes = (byte[]) bundle.get("photo_uri");

        UploadTask uploadTask = mealRef.putBytes(bytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Failed to upload image");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                System.out.println("Success: " + taskSnapshot.getMetadata());
                startActivity(new Intent(SaveMealActivity.this, MainActivity.class));
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressBar.setProgress((int)progress);
            }
        });


//        StorageReference fileReference = mStorageref.child(System.currentTimeMillis()
//                + "." + getFileExtension(mImageUri));
//        fileReference.putFile(mImageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                progressBar.setProgress(0);
//                            }
//                        }, 5000);
//                        Toast.makeText(SaveMealActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
//                        ArrayList<String> tags = new ArrayList<>(Arrays.asList("Carbs", "Sugar"));
//                        Meal m = new Meal(mealName.getText().toString().trim(), "", tags,
//                                taskSnapshot.getUploadSessionUri().toString(), mealDesc.getText().toString());
//                        String uploadId = mDatabaseref.push().getKey();
//                        mDatabaseref.child(uploadId).setValue(m);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(SaveMealActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                        progressBar.setProgress((int)progress);
//                    }
//                });
    }





    //    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_save_meal);
//
//        //INITIALIZE
//        mealTitle = (EditText) findViewById(R.id.nameEditText);
//        mealdescrip = (EditText) findViewById(R.id.mealdescripEditText);
//        saveBtn = (Button) findViewById(R.id.savebtn);
//        mealimage = (ImageView) findViewById(R.id.meal_image);
//        pathArray = new ArrayList<>();
//        progressBar = new ProgressBar(SaveMealActivity.this);
//        auth = FirebaseAuth.getInstance();
//        mStorageref = FirebaseStorage.getInstance().getReference();
//
//        checkFilePermissions();
//
//
//        saveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Meal m = new Meal();
//                m.setTitle(mealTitle.getText().toString());
//                m.setDesc(mealdescrip.getText().toString());
//                Log.d(TAG, "onclick: Uploading image");
//
//                FirebaseUser user = auth.getCurrentUser();
//                String userid = user.getUid();
//
//                mealTitle.setText("");
//                mealdescrip.setText("");
//            }
//        });
//    }
//
//    private void addFilePaths() {
//        Log.d(TAG, "addFilePaths: Adding file paths");
//        String path = System.getenv("EXTERNAL_STORAGE");
//        pathArray.add(path+"/Pictures/Portal/image1.jpg");
//        pathArray.add(path+"/Pictures/Portal/image2.jpg");
//        pathArray.add(path+"/Pictures/Portal/image3.jpg");
//        loadImageFromStorage();
//    }
//
//    private  void loadImageFromStorage() {
//        try {
//            String path = pathArray.get(array_position);
//            File f = new File(path, "");
//            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            mealimage.setImageBitmap(b);
//        } catch (FileNotFoundException e) {
//            log.e(TAG, "loadImageFrom Storage: FileNotFoundException" + e.getMessage());
//        }
//    }
//
//    private void checkFilePermissions() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            int permissionCheck = SaveMealActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
//            permissionCheck += SaveMealActivity.this.checkSelfPermission("Manifest.permissions.WRITE_EXTERNAL_STORAGE");
//            if (permissionCheck != 0) {
//                this.requestPermissions(new String[] Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
//            }
//        } else {
//            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK Version < LOLLIPOP");
//        }
//    }


}
