package com.example.firebase_chat_app;


import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class FriendsAdopter extends RecyclerView.Adapter<FriendsAdopter.UserViewHolder> {


    private String user_id;
    private android.content.Context context;
    ArrayList<User> userArrayList;



    public FriendsAdopter(android.content.Context applicationContext, ArrayList<User> userArray) {

        this.context=applicationContext;
        this.userArrayList=userArray;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.single_user,parent,false);
        return new FriendsAdopter.UserViewHolder(view);
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

               // User user=userArrayList.get(position);

                CharSequence option[] = new CharSequence[]{"Open Profile", "Send Message"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Selection Option");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i==0){
                            User user=userArrayList.get(position);
                            Intent intent = new Intent(context,ProfileActivity.class);
                            intent.putExtra("user_id",user.getUid());
                          //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }else if (i==1){
                            User user=userArrayList.get(position);
                            Intent intent = new Intent(context,ChartActivity.class);
                            intent.putExtra("user_id",user.getUid());
                            intent.putExtra("user_name", user.getName());
                            intent.putExtra("imgUrl", user.getImage());
                           // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    }
                });
                builder.show();


                /*Intent intent = new Intent(context,ProfileActivity.class);
                intent.putExtra("user_id",user.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);*/
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
