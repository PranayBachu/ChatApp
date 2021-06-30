package com.example.firebase_chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    DatabaseReference mUserDatabase;
    FirebaseUser mCurrentUser;
    ProgressDialog progressDialog;

    TextView profile_name, profile_status;
    CircleImageView circleImageView;
    Button change_status, change_image;
    private static final int GALLARY_PICK = 1;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        profile_name = findViewById(R.id.profile_name);
        profile_status = findViewById(R.id.profile_status);
        circleImageView = findViewById(R.id.profile_image);

        change_status = findViewById(R.id.change_status);
        change_image = findViewById(R.id.change_image);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_Uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_Uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String status = snapshot.child("status").getValue().toString();

                profile_name.setText(name);
                profile_status.setText(status);


                Picasso.get().load(image).placeholder(R.drawable.is).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    change_status.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String status_value = profile_status.getText().toString();
            Intent intent = new Intent(SettingsActivity.this, StatusActivity.class);
            intent.putExtra("status_value", status_value);
            startActivity(intent);
        }
    });

    change_image.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Image pick"), GALLARY_PICK);
        }
    });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(SettingsActivity.this);
                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("please wait while image is uploading");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String current_userId = mCurrentUser.getUid();

              //  File thumb_file = new File(current_userId.getUid());

              //  Bitmap compressToBitmap = new Compressor(this).compressToBitmap(actualImageFile);

                Uri resultUri = result.getUri();
                StorageReference filepath = mStorageRef.child("profile_images").child(current_userId + ".jpg");
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> result=taskSnapshot.getMetadata().getReference().getDownloadUrl();

                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String photolink = uri.toString();

                                mUserDatabase.child("image").setValue(photolink).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SettingsActivity.this, "uploaded", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }

                                    }
                                });
                            }
                        });
                    }
                });/*.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){

                          //  String download_url = task.getResult().getStorage().getDownloadUrl().toString();
                            String download_url = resultUri.toString();
                            mUserDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "upload", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}