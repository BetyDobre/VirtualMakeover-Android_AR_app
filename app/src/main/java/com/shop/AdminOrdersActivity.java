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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shop.models.AdminOrders;
import com.shop.prevalent.Prevalent;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

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
                                                            PlaceInHistory(uid);
//                                                            ordersRef.child(uid).removeValue();
                                                            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View");
//                                                            productsRef.child(uid).removeValue();
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

    private void PlaceInHistory(String uid) {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("Orders History");
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(uid);
        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> ordersMap = new HashMap<>();
                ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String date = "", time= "";
                            for (DataSnapshot orderSnapshot : snapshot.getChildren()){
                                String data = orderSnapshot.getValue().toString();
                                if(orderSnapshot.getKey().equals("totalAmount")){
                                    ordersMap.put("totalAmount", data);
                                }
                                else if(orderSnapshot.getKey().equals("name")){
                                    ordersMap.put("name", data);
                                }
                                else if(orderSnapshot.getKey().equals("phone")){
                                    ordersMap.put("phone", data);
                                }
                                else if(orderSnapshot.getKey().equals("address")){
                                    ordersMap.put("address", data);
                                }
                                else if(orderSnapshot.getKey().equals("city")){
                                    ordersMap.put("city", data);
                                }
                                else if(orderSnapshot.getKey().equals("date")){
                                    ordersMap.put("date", data);
                                    date = data;
                                }
                                else if(orderSnapshot.getKey().equals("time")){
                                    ordersMap.put("time", data);
                                    time = data;
                                }
                                else if(orderSnapshot.getKey().equals("email")){
                                    ordersMap.put("email", data);
                                }
                                ordersMap.put("state", "delivered");
                            }

                            historyRef.child(uid).child(date+time).updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AdminOrdersActivity.this, "Order placed in history", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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