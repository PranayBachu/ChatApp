package com.example.firebase_chat_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class chatFragment extends Fragment {

    RecyclerView mFriendList;
    DatabaseReference mFriendDatabase;
    FirebaseAuth mAuth;
    String mCurrent_user_id;
    View mMainView;
    Toolbar toolbar;
    DatabaseReference databaseReference;
    DatabaseReference mFriendListRef;
    DatabaseReference mFriendlistKeys;
    DatabaseReference lastMessageRef;
    ArrayList<String> friendKeys;
    ChatAdopter adopter;
    private ArrayList<User> userArray;

    public chatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView= inflater.inflate(R.layout.fragment_chat, container, false);
        toolbar = mMainView.findViewById(R.id.users_app_bar);
        // mCurrent_user_id = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendlistKeys = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        lastMessageRef = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        friendKeys = new ArrayList<>();
        mFriendListRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendList = mMainView.findViewById(R.id.friend_list);
        userArray=new ArrayList<>();
        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    if(!user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        userArray.add(user);
                        adopter = new ChatAdopter(getContext(),userArray);
                        mFriendList.setAdapter(adopter);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return mMainView;
    }


}

