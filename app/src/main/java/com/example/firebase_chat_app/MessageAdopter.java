package com.example.firebase_chat_app;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdopter extends RecyclerView.Adapter<MessageAdopter.UserViewHolder> {

    private final String uid;
    private List<Message> mMessageList;
    FirebaseAuth mAuth;
    DatabaseReference mRootRef;
    private android.content.Context context;
    private String receiverimage;

    public MessageAdopter(Context context,List<Message> mMessageList,String uid) {
        this.mMessageList = mMessageList;
        this.context=context;
        this.uid=uid;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.send_revive_message_list,parent,false);
        return new MessageAdopter.UserViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        Message message1 = mMessageList.get(position);
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();

        String messageTpye = message1.getType();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    receiverimage=snapshot.child(uid).child("image").getValue().toString();
                    Picasso.get().load(receiverimage).placeholder(R.drawable.is).into(holder.img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        String from_user = message1.getFrom();
        if (messageTpye.equals("text")){
        if (from_user.equals(current_user_id)){
            holder.messageText.setVisibility(View.INVISIBLE);
            holder.img.setVisibility(View.INVISIBLE);
            holder.messageText1.setText(message1.getMessage());
            holder.messageText1.setBackgroundResource(R.drawable.text_message_background_white);
        }else {
            holder.messageText1.setVisibility(View.INVISIBLE);
            holder.messageText.setText(message1.getMessage());
            holder.messageText.setBackgroundResource(R.drawable.text_message_background);
            holder.messageText.setTextColor(Color.WHITE);
        }

        }
        else if (messageTpye.equals("image")){
            if (from_user.equals(current_user_id)){
                holder.messageText.setVisibility(View.INVISIBLE);
                holder.messageText1.setVisibility(View.INVISIBLE);
                holder.img.setVisibility(View.INVISIBLE);
                Picasso.get().load(message1.getMessage()).into(holder.senderImg);
            }else {
                holder.messageText.setVisibility(View.INVISIBLE);
                holder.messageText1.setVisibility(View.INVISIBLE);
                holder.img.setVisibility(View.VISIBLE);
                Picasso.get().load(message1.getMessage()).into(holder.reciverimg);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        TextView messageText,messageText1;
        ImageView img,senderImg,reciverimg;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_view);
            messageText1 = itemView.findViewById(R.id.message_view1);
            img = itemView.findViewById(R.id.reciver_img);
            senderImg = itemView.findViewById(R.id.message_image_sender);
            reciverimg = itemView.findViewById(R.id.message_image_reciver);
        }
    }
}

