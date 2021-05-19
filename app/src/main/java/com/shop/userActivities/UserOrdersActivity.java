package com.shop.userActivities;

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
import com.shop.shopActivities.HomeActivity;
import com.shop.R;
import com.shop.adminActivities.AdminOrderProductsActivity;
import com.shop.models.AdminOrders;
import com.shop.models.UserHistoryOrders;
import com.shop.helpers.Prevalent;
import com.shop.viewholders.UserOrdersViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList, orderhistoryList;
    private TextView currentOrder, ordersHistory, backBtn, noOrdersTxt;
    private DatabaseReference ordersRef, historyRef;
    private String uid = "";

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
        noOrdersTxt = findViewById(R.id.no_user_orders_txt);
        backBtn = findViewById(R.id.back_to_home_txt);

        uid = EncodeString(Prevalent.currentOnlineUser.getEmail());

        // display a message when there are no orders from the user
        ordersRef.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                noOrdersTxt.setVisibility(View.VISIBLE);
                            }
                            else{
                                noOrdersTxt.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                else{
                    noOrdersTxt.setVisibility(View.GONE);
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
                Intent intent = new Intent(UserOrdersActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserOrdersActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // title to display when user has an order in progress
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

        // RecyclerView created to store and display current orders for the user
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef.orderByChild("email").equalTo(Prevalent.currentOnlineUser.getEmail()), AdminOrders.class)
                        .build();

        FirebaseRecyclerAdapter<AdminOrders, UserOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, UserOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserOrdersViewHolder holder, int position, @NonNull AdminOrders model) {
                        holder.userName.setText("Name: " + model.getName());
                        holder.userPhone.setText("Phone Number: " + model.getPhone());
                        holder.userTotalPrice.setText("Total Price: " + model.getTotalAmount() + " lei");
                        try {
                            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(model.getDate());
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            String DateToStr = format.format(date);

                            Date time= new SimpleDateFormat("HH:mm:ss a").parse(model.getTime());
                            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                            String TimeToStr = format2.format(time);

                            holder.userDateTime.setText("Ordered at: " + TimeToStr + ", " + DateToStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        holder.userAddress.setText("Address: " + model.getAddress() + ", " + model.getCity());
                        holder.userEmail.setText("Email: " + model.getEmail());
                        holder.userState.setText("State: " + model.getState().toUpperCase());
                        holder.userPayment.setText("Payment method: " + model.getPayment());

                        holder.showProductsBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uorderid = getRef(position).getKey();
                                Intent intent = new Intent(UserOrdersActivity.this, AdminOrderProductsActivity.class);
                                intent.putExtra("uorderid", uorderid);
                                intent.putExtra("uid", uid);
                                intent.putExtra("type","user");
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public UserOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_orders_layout, parent, false);
                        return new UserOrdersViewHolder(view);
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();

        // title to display when user has past orders
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

        // RecyclerView created to store and display orders history for a user
        FirebaseRecyclerOptions<UserHistoryOrders> options2 =
                new FirebaseRecyclerOptions.Builder<UserHistoryOrders>()
                        .setQuery(historyRef.orderByChild("email"), UserHistoryOrders.class)
                        .build();

        FirebaseRecyclerAdapter<UserHistoryOrders, UserOrdersViewHolder> adapter2 =
                new FirebaseRecyclerAdapter<UserHistoryOrders, UserOrdersViewHolder>(options2) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserOrdersViewHolder holder, int position, @NonNull UserHistoryOrders model) {
                        holder.userName.setText("Name: " + model.getName());
                        holder.userPhone.setText("Phone Number: " + model.getPhone());
                        holder.userTotalPrice.setText("Total Price: " + model.getTotalAmount() + " lei");
                        try {
                            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(model.getDate());
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            String DateToStr = format.format(date);

                            Date time= new SimpleDateFormat("HH:mm:ss a").parse(model.getTime());
                            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                            String TimeToStr = format2.format(time);

                            holder.userDateTime.setText("Ordered at: "  + TimeToStr + ", " + DateToStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        holder.userAddress.setText("Address: " + model.getAddress() + ", " + model.getCity());
                        holder.userEmail.setText("Email: " + model.getEmail());
                        holder.userState.setText("State: " + model.getState().toUpperCase());
                        holder.userPayment.setText("Payment method: " + model.getPayment());

                        holder.showProductsBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uorderid = getRef(position).getKey();
                                Intent intent = new Intent(UserOrdersActivity.this, UserHistoryProductsActivity.class);
                                intent.putExtra("uorderid", uorderid);
                                intent.putExtra("uid", uid);
                                intent.putExtra("products", model.getProducts());
                                intent.putExtra("type","user");
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public UserOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_orders_layout, parent, false);
                        return new UserOrdersViewHolder(view);
                    }
                };
        orderhistoryList.setAdapter(adapter2);
        adapter2.startListening();
    }

}