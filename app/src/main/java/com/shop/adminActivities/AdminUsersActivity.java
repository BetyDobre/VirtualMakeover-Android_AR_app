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
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shop.R;
import com.shop.models.Users;
import com.shop.viewholders.UserViewHolder;
import com.squareup.picasso.Picasso;


public class AdminUsersActivity extends AppCompatActivity {

    private RecyclerView usersList, googleUsersList;
    private DatabaseReference usersRef, googleUsersRef;
    private TextView backBtn, googleUsersTxt, noUsersTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        backBtn = findViewById(R.id.back_to_current_orders_txt);
        googleUsersTxt = findViewById(R.id.google_users_txt);
        noUsersTxt = findViewById(R.id.no_users_txt);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersList = findViewById(R.id.users_list);
        usersList.setLayoutManager(new LinearLayoutManager(this));

        googleUsersRef = FirebaseDatabase.getInstance().getReference().child("Google Users");
        googleUsersList = findViewById(R.id.google_users_list);
        googleUsersList.setLayoutManager(new LinearLayoutManager(this));

        // title for Google logged in users
        googleUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    googleUsersTxt.setVisibility(View.GONE);
                }
                else {
                    googleUsersTxt.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // display a message in case app doesn't have any user
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    googleUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                noUsersTxt.setVisibility(View.VISIBLE);
                            }
                            else{
                                noUsersTxt.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                else{
                    noUsersTxt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // click listener to go to the previous activity
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

        // RecyclerView created to store and display app users with an account
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(usersRef, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Users model) {
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
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false);
                        return new UserViewHolder(view);
                    }

                };
        usersList.setAdapter(adapter);
        adapter.startListening();

        // RecyclerView created to store and display Google logged in users
        FirebaseRecyclerOptions<Users> options2 =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(googleUsersRef, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, UserViewHolder> adapter2 =
                new FirebaseRecyclerAdapter<Users, UserViewHolder>(options2) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Users model) {
                        holder.userName.setText("Name: " + model.getName());
                        holder.userEmail.setText("Email: " + model.getEmail());
                        if(!(model.getAddress() == null)) {
                            holder.userAddress.setText("Address: " + model.getAddress());
                        }
                        else{
                            holder.userAddress.setText("");
                        }
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
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false);
                        return new UserViewHolder(view);
                    }

                };
        googleUsersList.setAdapter(adapter2);
        adapter2.startListening();
    }
}