package com.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shop.models.Products;
import com.shop.models.Users;
import com.shop.prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberBtn;
    private TextView productPrice, productDescription, productName;
    private String productID = "", state = "normal";
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        addToCartBtn = findViewById(R.id.details_add_product_to_cart);
        productImage = findViewById(R.id.product_image_details);
        numberBtn = findViewById(R.id.number_btn);
        productPrice = findViewById(R.id.product_price_details);
        productDescription = findViewById(R.id.product_description_details);
        productName = findViewById(R.id.product_name_details);

        productID = getIntent().getStringExtra("pid");
        getProductDetails(productID);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state.equals("Order Placed") || state.equals("Order Shipped")){
                    Toast.makeText(ProductDetailsActivity.this, "You can purchase more products once your current order will be delivered", Toast.LENGTH_LONG).show();
                }
                else {
                    addingToCart();
                }
            }
        });
    }

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    private void addingToCart() {

        Calendar calForDate = Calendar.getInstance();
        String saveCurrentTime, saveCurrentDate;

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        String sub = (productPrice.getText().toString()).substring(0, (productPrice.getText().toString()).length() - 4);
        cartMap.put("price", sub);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberBtn.getNumber());
        cartMap.put("discount", "");
        cartMap.put("image", image);

        cartListRef.child("User View").child(EncodeString(Prevalent.currentOnlineUser.getEmail()))
                .child("Products")
                .child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            cartListRef.child("Admin View").child(EncodeString(Prevalent.currentOnlineUser.getEmail()))
                                    .child("Products")
                                    .child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ProductDetailsActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void getProductDetails(String productID) {
        DatabaseReference ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        ProductsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Products product = dataSnapshot.getValue(Products.class);
                    productName.setText(product.getPname());
                    productDescription.setText(product.getDescription());
                    productPrice.setText(product.getPrice()+ " lei");
                    Picasso.get().load(product.getImage()).into(productImage);
                    image = product.getImage();
                    if(getIntent().getStringExtra("quantity") != null){
                        numberBtn.setNumber(getIntent().getStringExtra("quantity"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();
    }

    private void CheckOrderState(){
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(EncodeString(Prevalent.currentOnlineUser.getEmail()));

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shippingState = snapshot.child("state").getValue().toString();

                    if (shippingState.equals("shipped")){
                        state = "Order Shipped";
                    }
                    else if (shippingState.equals("not shipped")) {
                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}