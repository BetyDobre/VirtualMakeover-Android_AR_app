package com.shop.adminActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shop.R;
import com.shop.models.Users;
import com.squareup.picasso.Picasso;


public class AdminUsersActivity extends AppCompatActivity {

    private RecyclerView usersList;
    private DatabaseReference usersRef;
    private TextView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        backBtn = findViewById(R.id.back_to_current_orders_txt);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersList = findViewById(R.id.users_list);
        usersList.setLayoutManager(new LinearLayoutManager(this));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminUsersActivity.this, AdminHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminUsersActivity.this, AdminHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(usersRef, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, AdminUsersActivity.UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Users, AdminUsersActivity.UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminUsersActivity.UserViewHolder holder, int position, @NonNull Users model) {
                        holder.userName.setText("Name: " + model.getName());
                        holder.userAddress.setText("Address: " + model.getAddress());
                        holder.userEmail.setText("Email: " + model.getEmail());
                        if (! (model.getImage() == null)) {
                            Picasso.get().load(model.getImage()).into(holder.userImage);
                        }


                        holder.showOrdersHistory.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uid = getRef(position).getKey();
                                Intent intent = new Intent(AdminUsersActivity.this, AdminOrdersHistoryActivity.class);
                                intent.putExtra("uid", uid);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminUsersActivity.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false);
                        return new AdminUsersActivity.UserViewHolder(view);
                    }

                };
        usersList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, userAddress, userEmail;
        public ImageView userImage;
        public Button showOrdersHistory;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            userAddress = itemView.findViewById(R.id.user_address);
            userEmail = itemView.findViewById(R.id.user_email);
            userImage = itemView.findViewById(R.id.user_image);
            showOrdersHistory = itemView.findViewById(R.id.show_orders_history__btn);
        }
    }
}