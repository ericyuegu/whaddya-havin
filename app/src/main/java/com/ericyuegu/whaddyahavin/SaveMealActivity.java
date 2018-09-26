package com.ericyuegu.whaddyahavin;

import android.*;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.ericyuegu.whaddyahavin.model.Meal;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;

public class SaveMealActivity extends AppCompatActivity {

    private static final String TAG = "SaveMealActivity";

    private EditText mealTitle, mealdescrip;
    private Button chooseBtn, saveBtn;
    private Meal m;
    private ImageView mealimage;
    private ProgressBar progressBar;

    private Uri mImageUri;
    private final int PICK_IMAGE_REQUEST = 1;

//    private ArrayList<String> pathArray;
//    private int array_position;

    private StorageReference mStorageref;
    private DatabaseReference mDatabaseref;
//    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_meal);
        mealTitle = (EditText) findViewById(R.id.nameEditText);
        mealdescrip = (EditText) findViewById(R.id.mealdescripEditText);
        saveBtn = (Button) findViewById(R.id.savebtn);
        mealimage = (ImageView) findViewById(R.id.meal_image);
        chooseBtn = (Button) findViewById(R.id.choosebtn);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mStorageref = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseref = FirebaseDatabase.getInstance().getReference("uploads");


        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

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
            mealimage.setImageURI(mImageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    //saving the file on firebase
    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageref.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 5000);
                            Toast.makeText(SaveMealActivity.this, "Upload successfull", Toast.LENGTH_LONG).show();
                            Meal m = new Meal(mealTitle.getText().toString().trim(), mealdescrip.getText().toString(),
                                    taskSnapshot.getUploadSessionUri().toString());
                            String uploadId = mDatabaseref.push().getKey();
                            mDatabaseref.child(uploadId).setValue(m);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SaveMealActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int)progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
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
