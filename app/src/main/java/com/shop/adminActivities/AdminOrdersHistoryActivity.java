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

import org.w3c.dom.Text;

import java.util.ArrayList;



public class AdminOrdersHistoryActivity extends AppCompatActivity {

    private RecyclerView orderhistoryList;
    DatabaseReference historyRef;
    private String uid;
    private TextView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders_history);

        uid = getIntent().getStringExtra("uid");
        historyRef = FirebaseDatabase.getInstance().getReference().child("Orders History").child(uid);
        orderhistoryList = findViewById(R.id.admin_orders_history_list);
        orderhistoryList.setLayoutManager(new LinearLayoutManager(this));
        backBtn = findViewById(R.id.back_to_users_txt);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminOrdersHistoryActivity.this, AdminUsersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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

        FirebaseRecyclerOptions<UserHistoryOrders> options =
                new FirebaseRecyclerOptions.Builder<UserHistoryOrders>()
                        .setQuery(historyRef, UserHistoryOrders.class)
                        .build();

        FirebaseRecyclerAdapter<UserHistoryOrders, AdminOrdersHistoryActivity.AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<UserHistoryOrders, AdminOrdersHistoryActivity.AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersHistoryActivity.AdminOrdersViewHolder holder, int position, @NonNull UserHistoryOrders model) {
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
                    public AdminOrdersHistoryActivity.AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_orders_layout, parent, false);
                        return new AdminOrdersHistoryActivity.AdminOrdersViewHolder(view);
                    }
                };
        orderhistoryList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userTotalPrice, userDateTime, userPhone, userAddress, userEmail, userState, userPayment;
        private Button showOrdersBtn;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_username);
            userPhone= itemView.findViewById(R.id.order_phone);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userAddress = itemView.findViewById(R.id.order_address);
            userEmail = itemView.findViewById(R.id.order_email);
            userState = itemView.findViewById(R.id.order_state);
            userPayment = itemView.findViewById(R.id.order_payment_method);
            showOrdersBtn = itemView.findViewById(R.id.admin_show_products_btn);
        }
    }
}