package com.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shop.models.AdminOrders;

import org.w3c.dom.Text;

public class AdminOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = findViewById(R.id.admin_orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef, AdminOrders.class)
                .build();
        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, int position, @NonNull AdminOrders model) {
                        holder.userName.setText("Name: " + model.getName());
                        holder.userPhone.setText("Phone Number: " + model.getPhone());
                        holder.userTotalPrice.setText("Total Price: " + model.getTotalAmount() + " lei");
                        holder.userDateTime.setText("Ordered at: " + model.getDate() + " " + model.getTime());
                        holder.userAddress.setText("Address: " + model.getAddress()+ ", " + model.getCity());
                        holder.userEmail.setText("Email: " + model.getEmail());

                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uid = getRef(position).getKey();

                                Intent intent = new Intent(AdminOrdersActivity.this, AdminOrderProductsActivity.class);
                                intent.putExtra("uid", uid);
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
            ordersList.setAdapter(adapter);
            adapter.startListening();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userTotalPrice, userDateTime, userPhone, userAddress, userEmail;
        private Button showOrdersBtn;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_username);
            userPhone= itemView.findViewById(R.id.order_phone);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userAddress = itemView.findViewById(R.id.order_address);
            userEmail = itemView.findViewById(R.id.order_email);
            showOrdersBtn = itemView.findViewById(R.id.admin_show_products_btn);
        }
    }
}