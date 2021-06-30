package com.example.firebase_chat_app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdopter extends RecyclerView.Adapter<UserAdopter.UserViewHolder> {


    private String user_id;
    private android.content.Context context;
    ArrayList<User> userArrayList;

    public UserAdopter(android.content.Context applicationContext, ArrayList<User> userArray) {

        this.context=applicationContext;
        this.userArrayList=userArray;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.single_user,parent,false);
        return new UserAdopter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User user=userArrayList.get(position);
        holder.name.setText(user.getName());
        holder.status.setText(user.getStatus());
        Picasso.get().load(user.getImage()).placeholder(R.drawable.is).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user=userArrayList.get(position);

                Intent intent = new Intent(context,ProfileActivity.class);
                intent.putExtra("user_id",user.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
