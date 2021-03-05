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
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shop.R;
import com.shop.models.UserHistoryOrders;
import com.shop.userActivities.UserHistoryProductsActivity;
import com.shop.viewholders.AdminOrdersViewHolder;

public class AdminOrdersHistoryActivity extends AppCompatActivity {

    private RecyclerView orderhistoryList;
    private DatabaseReference historyRef;
    private String uid;
    private TextView backBtn, noOrdersHistoryTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders_history);

        uid = getIntent().getStringExtra("uid");

        historyRef = FirebaseDatabase.getInstance().getReference().child("Orders History").child(uid);

        orderhistoryList = findViewById(R.id.admin_orders_history_list);
        orderhistoryList.setLayoutManager(new LinearLayoutManager(this));

        backBtn = findViewById(R.id.back_to_users_txt);
        noOrdersHistoryTxt = findViewById(R.id.no_orders_history_txt);

        // click listener to go to the previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminOrdersHistoryActivity.this, AdminUsersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // display a message there aren't any previous orders
        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    noOrdersHistoryTxt.setVisibility(View.VISIBLE);
                }
                else{
                    noOrdersHistoryTxt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminOrdersHistoryActivity.this, AdminUsersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // RecyclerView created to store and display orders history for a user
        FirebaseRecyclerOptions<UserHistoryOrders> options =
                new FirebaseRecyclerOptions.Builder<UserHistoryOrders>()
                        .setQuery(historyRef, UserHistoryOrders.class)
                        .build();

        FirebaseRecyclerAdapter<UserHistoryOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<UserHistoryOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, int position, @NonNull UserHistoryOrders model) {
                        holder.userName.setText("Name: " + model.getName());
                        holder.userPhone.setText("Phone Number: " + model.getPhone());
                        holder.userTotalPrice.setText("Total Price: " + model.getTotalAmount() + " lei");
                        holder.userDateTime.setText("Ordered at: " + model.getDate() + " " + model.getTime());
                        holder.userAddress.setText("Address: " + model.getAddress()+ ", " + model.getCity());
                        holder.userEmail.setText("Email: " + model.getEmail());
                        holder.userState.setText("State: " + model.getState());
                        holder.userPayment.setText("Payment method: " + model.getPayment());

                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uorderid = getRef(position).getKey();
                                Intent intent = new Intent(AdminOrdersHistoryActivity.this, UserHistoryProductsActivity.class);
                                intent.putExtra("uorderid", uorderid);
                                intent.putExtra("uid", uid);
                                intent.putExtra("type", "admin");
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_orders_layout, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };
        orderhistoryList.setAdapter(adapter);
        adapter.startListening();
    }
}