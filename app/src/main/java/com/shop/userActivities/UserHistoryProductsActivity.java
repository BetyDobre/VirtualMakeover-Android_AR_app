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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shop.R;
import com.shop.adminActivities.AdminOrderProductsActivity;
import com.shop.adminActivities.AdminOrdersActivity;
import com.shop.adminActivities.AdminOrdersHistoryActivity;
import com.shop.models.Cart;
import com.shop.models.UserHistoryOrders;
import com.shop.prevalent.Prevalent;
import com.shop.viewholders.CartViewHolder;
import com.squareup.picasso.Picasso;

public class UserHistoryProductsActivity extends AppCompatActivity {

    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference productsRef;
    private String orderId = "", uid = "", type = "";
    private TextView backBtn;

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_products);

        productsList = findViewById(R.id.admin_products_list);
        productsList.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);
        orderId = getIntent().getStringExtra("uorderid");
        uid = getIntent().getStringExtra("uid");
        type = getIntent().getStringExtra("type");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Orders History").child(uid).child(orderId).child("Products");
        backBtn = findViewById(R.id.back_to_order_txt);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("admin")){
                    Intent intent = new Intent(UserHistoryProductsActivity.this, AdminOrdersHistoryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }
                else if(type.equals("user")){
                    Intent intent = new Intent(UserHistoryProductsActivity.this, UserOrdersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (type.equals("admin")){
            Intent intent = new Intent(UserHistoryProductsActivity.this, AdminOrdersHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("uid", uid);
            startActivity(intent);
        }
        else if(type.equals("user")){
            Intent intent = new Intent(UserHistoryProductsActivity.this, UserOrdersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("uid", uid);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(productsRef, Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                holder.productQuantityTxt.setText("Quantity: " + model.getQuantity());
                holder.productNameTxt.setText(model.getPname());
                holder.productPriceTxt.setText("Price: " + model.getQuantity() + "x" + model.getPrice() + " lei");
                Picasso.get().load(model.getImage()).into(holder.productImage);
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}