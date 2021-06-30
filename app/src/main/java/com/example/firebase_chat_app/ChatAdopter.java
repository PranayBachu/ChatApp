package com.example.firebase_chat_app;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ChatAdopter extends RecyclerView.Adapter<ChatAdopter.UserViewHolder> {


    private String user_id;
    private android.content.Context context;
    ArrayList<User> userArrayList;
    private String lastmessage;


    public ChatAdopter(android.content.Context applicationContext, ArrayList<User> userArray) {

        this.context=applicationContext;
        this.userArrayList=userArray;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_user,parent,false);
        return new ChatAdopter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {



        User user = userArrayList.get(position);
        holder.name.setText(user.getName());


        Picasso.get().load(user.getImage()).placeholder(R.drawable.is).into(holder.image);

        FirebaseDatabase.getInstance().getReference().child("messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                lastmessage=snapshot.child("message").getValue().toString();
                String messageType = snapshot.child("type").getValue().toString();
                if (messageType.equals("text")){
                    holder.status.setText(lastmessage);
                }else {
                    holder.status.setText("Image Send");
                }

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user=userArrayList.get(position);
                Intent intent = new Intent(context,ChartActivity.class);
                intent.putExtra("user_id",user.getUid());
                intent.putExtra("user_name", user.getName());
                intent.putExtra("imgUrl", user.getImage());
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        TextView name,status;
        ImageView image;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            image= itemView.findViewById(R.id.list_image);
            status=itemView.findViewById(R.id.list_status);
            name= itemView.findViewById(R.id.list_display_name);
        }
    }
}

