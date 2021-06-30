package com.example.firebase_chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class
ProfileActivity extends AppCompatActivity {

    TextView profile_display_name, profile_total_fiends,profile_status;
    ImageView imageView;
    Button mProfileSendReqBtn, mDeclineReqBtn;
    String mCurrentState = "";

    DatabaseReference mUsersDatabase;
    DatabaseReference mFriendRequestDatabase;
    DatabaseReference mFriendRequest;
    DatabaseReference mNotificationDatabase;
    DatabaseReference mRootDatabase;
    FirebaseUser mCurrent_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String user_id = getIntent().getStringExtra("user_id");
        Toast.makeText(this, user_id, Toast.LENGTH_SHORT).show();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        profile_display_name = findViewById(R.id.profile_display_name);
        profile_total_fiends = findViewById(R.id.profile_total_Firends);
        profile_status = findViewById(R.id.profile_status);
        mProfileSendReqBtn = findViewById(R.id.send_friend_request);
        mDeclineReqBtn = findViewById(R.id.declineFriendReq);
        imageView = findViewById(R.id.imageView);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendRequest = FirebaseDatabase.getInstance().getReference().child("Contacts");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notification");
        mRootDatabase = FirebaseDatabase.getInstance().getReference();

        mCurrent_User = FirebaseAuth.getInstance().getCurrentUser();

        mCurrentState = "not_friends";

        mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String display_name = snapshot.child("name").getValue().toString();
                String display_status = snapshot.child("status").getValue().toString();
                String image = snapshot.child("image").getValue().toString();

                profile_display_name.setText(display_name);
                profile_status.setText(display_status);
                Picasso.get().load(image).into(imageView);

                // ------- Friend list / Request Feature --------
                mFriendRequestDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(user_id)) {
                            //Toast.makeText(ProfileActivity.this, "hello", Toast.LENGTH_SHORT).show();
                            String req_type = "";
                            req_type = snapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {
                                mCurrentState = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");
                                mDeclineReqBtn.setVisibility(View.VISIBLE);
                                mDeclineReqBtn.setEnabled(true);
                            } else if (req_type.equals("sent")) {
                                mCurrentState = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                                mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mDeclineReqBtn.setEnabled(false);
                            }
                        } else {
                            mFriendRequest.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(user_id)){
                                        mCurrentState = "friends";
                                        mProfileSendReqBtn.setText("Unfriend this Person");
                                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                        mDeclineReqBtn.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileSendReqBtn.setEnabled(false);
                if (mCurrentState.equals("not_friends")){

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrent_User.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + mCurrent_User.getUid() + "/request_type", "received");
                    mRootDatabase.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null){
                                Toast.makeText(ProfileActivity.this, "their was some error ", Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                            mCurrentState = "req_sent";
                            mProfileSendReqBtn.setText("cancel Friend request");
                        }
                    });

                }

                if (mCurrentState.equals("req_sent")){
                    mFriendRequestDatabase.child(mCurrent_User.getUid()).child(user_id)
                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendRequestDatabase.child(user_id).child(mCurrent_User.getUid())
                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrentState = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend request");
                                    mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                    mDeclineReqBtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                if (mCurrentState.equals("req_received")){
                    final String currentDate = DateFormat.getDateInstance().format(new Date());
                    Map friendsMap = new HashMap();
                    friendsMap.put("Contacts/" + mCurrent_User.getUid() + "/" + user_id + "/date", currentDate);
                    friendsMap.put("Contacts/" + user_id + "/" + mCurrent_User.getUid() + "/date", currentDate);

                    friendsMap.put("Friend_req/" + mCurrent_User.getUid() + "/" + user_id, null);
                    friendsMap.put("Friend_req/" + user_id + "/" + mCurrent_User.getUid(), null);

                    mRootDatabase.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null){
                                mProfileSendReqBtn.setEnabled(true);
                                mCurrentState = "friends";
                                mProfileSendReqBtn.setText("Unfriend this Person");
                                mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mDeclineReqBtn.setEnabled(false);
                            }
                        }
                    });
                }

                if (mCurrentState.equals("friends")){

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Contacts/" + mCurrent_User.getUid() + "/" + user_id, null);
                    unfriendMap.put("Contacts/" + user_id + "/" + mCurrent_User.getUid(), null );
                    mRootDatabase.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null){
                                mCurrentState = "not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");
                                mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mDeclineReqBtn.setEnabled(false);
                            }
                            else {
                                String error1 = error.getMessage();
                                Toast.makeText(ProfileActivity.this, error1, Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });

                }


            }
        });

        mDeclineReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentState.equals("req_received")){

                    Map declineRequest = new HashMap();
                    declineRequest.put("Friend_req/" + user_id + "/" + mCurrent_User.getUid(), null);
                    declineRequest.put("Friend_req/" + mCurrent_User.getUid() + "/" + user_id , null);


                    mRootDatabase.updateChildren(declineRequest, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null){
                                mCurrentState = "not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");
                                mDeclineReqBtn.setEnabled(false);
                            }
                        }
                    });

                }
            }
        });
    }
}