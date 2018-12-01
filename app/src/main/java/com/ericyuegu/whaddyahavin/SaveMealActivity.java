package com.ericyuegu.whaddyahavin;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
//        getActionBar().setDisplayHomeAsUpEnabled(true);
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
            Bitmap scaled = Bitmap.createScaledBitmap(bmp, 300, 300, false);
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
                
            final String mealTitle = mealName.getText().toString().trim();

            if (TextUtils.isEmpty(mealTitle)) {
                Toast.makeText(getApplicationContext(), "Enter a meal title", Toast.LENGTH_SHORT).show();
                return;
            } else {
                uploadFile();
            }
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

        CollectionReference docRef = db.collection("users").document(user.getUid()).collection("meals");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot query = task.getResult();
                    System.out.println(query);

                    if (query != null) {
                        int numMeals = query.getDocuments().size();
                        int maxMealNum = 0;

                        if (numMeals > 0) { // meals have been added (and possibly deleted)
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (Integer.parseInt(document.get("mealNum").toString()) > maxMealNum) {
                                    maxMealNum = Integer.parseInt(document.get("mealNum").toString());
                                }
                            }
                            maxMealNum++; // increment to be next meal key
                        }

                        final String strNext = Integer.toString(maxMealNum);
                        System.out.println(strNext);

                        HashMap<String, String> uploadMeal = new HashMap<>();
                        uploadMeal.put("mealName", mealName.getText().toString());
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d yyyy HH:mm:ss", Locale.US);
                        String currentDateandTime = sdf.format(new Date());
                        uploadMeal.put("timestamp", currentDateandTime);
                        uploadMeal.put("tags", mealTags.getText().toString());
                        uploadMeal.put("photoUrl", user.getUid() + "-" + strNext + ".jpeg");
                        uploadMeal.put("description", mealDesc.getText().toString());
                        uploadMeal.put("mealNum", strNext);

                        db.collection("users")
                                .document(user.getUid())
                                .collection("meals")
                                .document(strNext)
                                .set(uploadMeal)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void documentReference) {
                                        System.out.println("Meal was successfully added.");
                                        uploadToStorage(strNext);
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

    public void uploadToStorage(String mealNum) {
        StorageReference storageRef = storage.getReference();
        StorageReference mealRef = storageRef.child(user.getUid() + "-" + mealNum + ".jpeg");

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
    }
}
