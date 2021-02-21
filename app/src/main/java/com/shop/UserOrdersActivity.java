package com.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import com.shop.models.AdminOrders;
import com.shop.models.UserHistoryOrders;
import com.shop.prevalent.Prevalent;

public class UserOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList, orderhistoryList;
    private TextView currentOrder, ordersHistory;
    private DatabaseReference ordersRef, historyRef;

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = findViewById(R.id.user_order_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

        historyRef= FirebaseDatabase.getInstance().getReference().child("Orders History").child(EncodeString(Prevalent.currentOnlineUser.getEmail()));
        orderhistoryList = findViewById(R.id.user_order_history_list);
        orderhistoryList.setLayoutManager(new LinearLayoutManager(this));

        currentOrder = findViewById(R.id.current_order);
        ordersHistory = findViewById(R.id.order_history);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ordersRef.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    currentOrder.setVisibility(View.GONE);
                }
                else {
                    currentOrder.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef.orderByChild("email").equalTo(Prevalent.currentOnlineUser.getEmail()), AdminOrders.class)
                        .build();

        FirebaseRecyclerAdapter<AdminOrders, UserOrdersActivity.UserOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, UserOrdersActivity.UserOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserOrdersActivity.UserOrdersViewHolder holder, int position, @NonNull AdminOrders model) {
                        holder.userName.setText("Name: " + model.getName());
                        holder.userPhone.setText("Phone Number: " + model.getPhone());
                        holder.userTotalPrice.setText("Total Price: " + model.getTotalAmount() + " lei");
                        holder.userDateTime.setText("Ordered at: " + model.getDate() + " " + model.getTime());
                        holder.userAddress.setText("Address: " + model.getAddress() + ", " + model.getCity());
                        holder.userEmail.setText("Email: " + model.getEmail());
                        holder.userState.setText("State: " + model.getState());

                        holder.showProductsBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uid = getRef(position).getKey();

                                Intent intent = new Intent(UserOrdersActivity.this, AdminOrderProductsActivity.class);
                                intent.putExtra("uid", uid);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public UserOrdersActivity.UserOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_orders_layout, parent, false);
                        return new UserOrdersActivity.UserOrdersViewHolder(view);
                    }

                };
        ordersList.setAdapter(adapter);
        adapter.startListening();


        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    ordersHistory.setVisibility(View.GONE);
                }
                else {
                    ordersHistory.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseRecyclerOptions<UserHistoryOrders> options2 =
                new FirebaseRecyclerOptions.Builder<UserHistoryOrders>()
                        .setQuery(historyRef.orderByChild("email"), UserHistoryOrders.class)
                        .build();

        FirebaseRecyclerAdapter<UserHistoryOrders, UserOrdersActivity.UserOrdersViewHolder> adapter2 =
                new FirebaseRecyclerAdapter<UserHistoryOrders, UserOrdersActivity.UserOrdersViewHolder>(options2) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserOrdersActivity.UserOrdersViewHolder holder, int position, @NonNull UserHistoryOrders model) {
                        holder.userName.setText("Name: " + model.getName());
                        holder.userPhone.setText("Phone Number: " + model.getPhone());
                        holder.userTotalPrice.setText("Total Price: " + model.getTotalAmount() + " lei");
                        holder.userDateTime.setText("Ordered at: " + model.getDate() + " " + model.getTime());
                        holder.userAddress.setText("Address: " + model.getAddress() + ", " + model.getCity());
                        holder.userEmail.setText("Email: " + model.getEmail());
                        holder.userState.setText("State: " + model.getState());

                        holder.showProductsBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uid = getRef(position).getKey();
                                Intent intent = new Intent(UserOrdersActivity.this, UserHistoryProductsActivity.class);
                                intent.putExtra("uid", uid);
                                intent.putExtra("products", model.getProducts());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public UserOrdersActivity.UserOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_orders_layout, parent, false);
                        return new UserOrdersActivity.UserOrdersViewHolder(view);
                    }
                };
        orderhistoryList.setAdapter(adapter2);
        adapter2.startListening();

    }

    public static class UserOrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userTotalPrice, userDateTime, userPhone, userAddress, userEmail, userState;
        private Button showProductsBtn;

        public UserOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_username);
            userPhone= itemView.findViewById(R.id.order_phone);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userAddress = itemView.findViewById(R.id.order_address);
            userEmail = itemView.findViewById(R.id.order_email);
            userState = itemView.findViewById(R.id.order_state);
            showProductsBtn = itemView.findViewById(R.id.admin_show_products_btn);
        }
    }

}