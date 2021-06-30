package com.example.firebase_chat_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class RequistAdopter extends RecyclerView.Adapter<RequistAdopter.UserViewHolder> {

    private android.content.Context context;
    ArrayList<User> userArrayList;


    public RequistAdopter(android.content.Context applicationContext, ArrayList<User> userArray) {

        this.context=applicationContext;
        this.userArrayList=userArray;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.firend_request_list,parent,false);
        return new RequistAdopter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User user=userArrayList.get(position);
        holder.name.setText(user.getName());
        Picasso.get().load(user.getImage()).placeholder(R.drawable.is).into(holder.image);

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
            image= itemView.findViewById(R.id.request_list_image);
            name= itemView.findViewById(R.id.request_list_name);
        }
    }
}


