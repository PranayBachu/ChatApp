package com.example.firebase_chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChartActivity extends AppCompatActivity {

    String mChartUser;
    Toolbar mChatToolbar;
    TextView displayName;
    CircleImageView circleImageView,reciverImg;
    DatabaseReference mRootRef;
    FirebaseAuth mAuth;
    String mCurrentUserId;
    EditText mEnterMessage;
    ImageView mAddImg, mSendmessage;
    RecyclerView mMessageList;
    private StorageReference mImageStorage;

    private static final int GALLERY_PICK = 1;
    List<Message> messageList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;
    MessageAdopter mAdopter;
    DatabaseReference recImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        recImg = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mEnterMessage = findViewById(R.id.enter_message);
        mAddImg = findViewById(R.id.add_img);
        mSendmessage = findViewById(R.id.send_message);



        mChatToolbar = findViewById(R.id.chatAppBar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mChartUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");
        String imageURL = getIntent().getStringExtra("imgUrl");
        getSupportActionBar().setTitle(mChartUser);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.custom_action_bar, null);
        actionBar.setCustomView(action_bar_view);

        displayName = (TextView) findViewById(R.id.custom_actionBar_name);
        circleImageView = findViewById(R.id.custom_actionBar_img);
        reciverImg = findViewById(R.id.reciver_img);
        mMessageList = findViewById(R.id.message_list);
        mAdopter = new MessageAdopter(getApplicationContext(), messageList,mChartUser);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLayoutManager);

        loadMessages();



        displayName.setText(userName);
        Picasso.get().load(imageURL).placeholder(R.drawable.is).into(circleImageView);
       // Picasso.get().load(imageURL).placeholder(R.drawable.is).into(reciverImg);

        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(mChartUser)){
                    String s = "abc";
                    Map chatUerMap = new HashMap();
                    chatUerMap.put("Chat/" + mCurrentUserId + "/" + mChartUser, s );
                    chatUerMap.put("Chat/" + mChartUser + "/" + mCurrentUserId, s);

                    mRootRef.updateChildren(chatUerMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null){
                                Log.d("chat_tag", error.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //------- Send Message ------------
        mSendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        mAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallaryIntent = new Intent();
                gallaryIntent.setType("image/*");
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallaryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChartUser;
            String chat_user_ref = "messages/" + mChartUser + "/" + mCurrentUserId;
            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChartUser).push();
            final String push_id = user_message_push.getKey();
            StorageReference filepath = mImageStorage.child("message_image").child(push_id + ".jpg");
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task){
                    if(task.isSuccessful()){

                        Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String download_url = uri.toString();


                                Map messageMap = new HashMap();
                                messageMap.put("message", download_url);
                                messageMap.put("type", "image");
                                messageMap.put("from", mCurrentUserId);

                                Map messageUserMap = new HashMap();
                                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        if(databaseError != null){

                                            Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                        }

                                    }
                                });
                            }
                        });

                    }

                }
            });
        }
    }

    private void loadMessages() {

        mRootRef.child("messages").child(mCurrentUserId).child(mChartUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                messageList.add(message);
                mMessageList.setAdapter(mAdopter);
                mMessageList.scrollToPosition(messageList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void sendMessage() {
        String message = mEnterMessage.getText().toString();
        if (!TextUtils.isEmpty(message)){
            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChartUser;
            String chat_user_ref = "messages/" + mChartUser + "/" + mCurrentUserId;
            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChartUser).push();

            String push_id = user_message_push.getKey();
            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("type", "text");
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    mEnterMessage.setText("");
                    if (error != null){
                        Log.d("chat_tag", error.getMessage().toString());
                    }
                }
            });
        }
    }

}