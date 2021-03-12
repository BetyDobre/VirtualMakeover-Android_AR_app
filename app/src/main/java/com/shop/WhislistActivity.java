package com.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
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
import com.shop.adminActivities.AdminEditProductsActivity;
import com.shop.models.Products;
import com.shop.prevalent.Prevalent;
import com.shop.viewholders.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class WhislistActivity extends AppCompatActivity {

    private TextView bacBtn, noWhislistTxt;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whislist);

        recyclerView = findViewById(R.id.whislist);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        noWhislistTxt = findViewById(R.id.no_whislist_txt);
        bacBtn = findViewById(R.id.back_to_home_from_whislist_txt);
        bacBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WhislistActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Whislist").child(EncodeString(Prevalent.currentOnlineUser.getEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        noWhislistTxt.setVisibility(View.VISIBLE);
                    }
                    else {
                        noWhislistTxt.setVisibility(View.GONE);
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
        Intent intent = new Intent(WhislistActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference ProductsRef = FirebaseDatabase.getInstance().getReference().child("Whislist").child(EncodeString(Prevalent.currentOnlineUser.getEmail()));

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef, Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                        FirebaseDatabase.getInstance().getReference().child("Products").child(model.getPid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    holder.txtProductName.setText(snapshot.child("pname").getValue().toString());
                                    holder.txtProductDescription.setText(snapshot.child("description").getValue().toString());
                                    if(Integer.parseInt(snapshot.child("discount").getValue().toString()) == 0){
                                        holder.txtProductPrice.setText("Price: " + snapshot.child("price").getValue().toString() + " lei");
                                    }
                                    else{
                                        String txt = "Price: " + snapshot.child("price").getValue().toString() + " lei " + snapshot.child("discountPrice").getValue().toString() + " lei";
                                        holder.txtProductPrice.setText(txt, TextView.BufferType.SPANNABLE);
                                        Spannable spannable = (Spannable) holder.txtProductPrice.getText();
                                        spannable.setSpan(new StrikethroughSpan(), 7, txt.length() - (snapshot.child("discountPrice").getValue().toString() + " lei").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#e71826")), 7, txt.length() - (snapshot.child("discountPrice").getValue().toString()  + " lei").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }

                                    Picasso.get().load(snapshot.child("image").getValue().toString()).into(holder.imageView);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                    Intent intent = new Intent(WhislistActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}