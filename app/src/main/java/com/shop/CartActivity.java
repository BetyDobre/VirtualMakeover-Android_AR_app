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
import android.widget.ImageView;
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
import com.shop.models.Cart;
import com.shop.prevalent.Prevalent;
import com.shop.viewholders.CartViewHolder;
import com.squareup.picasso.Picasso;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextBtn;
    private TextView totalPriceTxt, txtMsg1;
    private ImageView loadingImg, deliveryImg;
    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextBtn = findViewById(R.id.next_btn);
        totalPriceTxt = findViewById(R.id.total_price);

        txtMsg1 = findViewById(R.id.msg1);
        loadingImg = findViewById(R.id.loading_image);
        deliveryImg = findViewById(R.id.delivery_image);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                totalPriceTxt.setText("Total price: " + String.valueOf(totalPrice) + " lei");
                Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                intent.putExtra("Total price", String.valueOf(totalPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View").child(EncodeString(Prevalent.currentOnlineUser.getEmail())).child("Products"), Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                            holder.productQuantityTxt.setText("Quantity: " + model.getQuantity());
                            holder.productNameTxt.setText(model.getPname());
                            holder.productPriceTxt.setText("Price: " + model.getQuantity() + "x" +model.getPrice() + " lei");
                            Picasso.get().load(model.getImage()).into(holder.productImage);

                            int oneProductTotalPrice = (Integer.valueOf(model.getPrice())) * Integer.valueOf(model.getQuantity());
                            totalPrice = totalPrice + oneProductTotalPrice;
                            totalPriceTxt.setText("Total price: " + String.valueOf(totalPrice) + " lei");

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CharSequence options[] = new CharSequence[]{
                                        "Edit",
                                        "Remove from cart"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                    builder.setTitle("Cart options:");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //edit
                                            if (i == 0){
                                                Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                                intent.putExtra("pid", model.getPid());
                                                intent.putExtra("quantity", model.getQuantity());
                                                startActivity(intent);
                                            }
                                            //delete
                                            if(i == 1){
                                                    cartListRef.child("User View").child(EncodeString(Prevalent.currentOnlineUser.getEmail()))
                                                            .child("Products")
                                                            .child(model.getPid())
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        Toast.makeText(CartActivity.this, "Item removed from the cart!", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(CartActivity.this, CartActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            });
                                                    cartListRef.child("Admin View").child(EncodeString(Prevalent.currentOnlineUser.getEmail()))
                                                            .child("Products")
                                                            .child(model.getPid())
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        Intent intent = new Intent(CartActivity.this, CartActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            });
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckOrderState(){
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(EncodeString(Prevalent.currentOnlineUser.getEmail()));

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shippingState = snapshot.child("state").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped")){
                        totalPriceTxt.setText("Dear " + userName + ",\n Order has been shipped successfully");
                        recyclerView.setVisibility(View.GONE);

                        deliveryImg.setVisibility(View.VISIBLE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.GONE);
                    }
                    else if (shippingState.equals("not shipped")) {
                        totalPriceTxt.setText("Dear " + userName + ",\n Your order is not shipped yet.");
                        recyclerView.setVisibility(View.GONE);

                        loadingImg.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulations, your order has been placed successfully. It will be verified and shipped soon.");
                        txtMsg1.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}