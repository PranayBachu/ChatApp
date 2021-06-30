package com.example.firebase_chat_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class registerationActivity extends AppCompatActivity {
    Toolbar mToolbar;
    FirebaseAuth mAuth;
    String currentUserId;
    EditText reg_name,reg_email,reg_pass;
    Button reg_btn;
    DatabaseReference mDatabase;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
       // currentUserId = mAuth.getCurrentUser().getUid();
        mToolbar = findViewById(R.id.reg_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        reg_name = findViewById(R.id.reg_name);
        reg_email = findViewById(R.id.reg_email);
        reg_pass = findViewById(R.id.reg_pass);
        reg_btn = findViewById(R.id.reg_button);


        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = reg_name.getText().toString();
                String email = reg_email.getText().toString();
                String pass = reg_pass.getText().toString();

                if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)){


                    progressDialog.setTitle("Regestering User");
                    progressDialog.setMessage("please wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    register_user(name, email, pass);
                }

            }
        });
    }

    public void register_user(String name, String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser current_User = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_User.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("status", "i am using chat app");
                    userMap.put("image", "default");
                    userMap.put("uid", uid);
                    mDatabase.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Intent intent = new Intent(registerationActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });
                }else {
                    progressDialog.hide();
                    Toast.makeText(registerationActivity.this, "Not Register"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}