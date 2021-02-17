package com.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
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
                        holder.userState.setText("State: " + model.getState());

                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uid = getRef(position).getKey();

                                Intent intent = new Intent(AdminOrdersActivity.this, AdminOrderProductsActivity.class);
                                intent.putExtra("uid", uid);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options1[] = new CharSequence[]{
                                        "Marked as delivered",
                                        "Back"
                                };
                                CharSequence options2[] = new CharSequence[]{
                                        "Marked as shipped",
                                        "Back"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminOrdersActivity.this);
                                builder.setTitle("Order options");
                                String uid = getRef(position).getKey();
                                DatabaseReference specOrder = ordersRef.child(uid);

                                specOrder.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String shippingState = snapshot.child("state").getValue().toString();

                                            if (shippingState.equals("shipped")) {
                                                builder.setItems(options1, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (i == 0) {
                                                            ordersRef.child(uid).removeValue();
                                                        } else {
                                                            finish();
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                            else {
                                                builder.setItems(options2, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (i == 0) {
                                                            ordersRef.child(uid).child("state").setValue("shipped");
                                                        } else {
                                                            finish();
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
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

        public TextView userName, userTotalPrice, userDateTime, userPhone, userAddress, userEmail, userState;
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
            showOrdersBtn = itemView.findViewById(R.id.admin_show_products_btn);
        }
    }
}