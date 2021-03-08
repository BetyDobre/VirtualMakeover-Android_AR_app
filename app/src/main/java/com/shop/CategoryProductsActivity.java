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
import com.shop.models.Products;
import com.shop.viewholders.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class CategoryProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView categoryTxt, backBtn, noCategoryProducts;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference ProductsRef;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_products);

        categoryTxt = findViewById(R.id.category_name_txt);
        noCategoryProducts = findViewById(R.id.no_category_products_txt);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = findViewById(R.id.recycler_category_products);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        category = getIntent().getStringExtra("category");
        categoryTxt.setText(Character.toString(category.charAt(0)).toUpperCase()+category.substring(1));

        backBtn = findViewById(R.id.back_to_categories_txt);

        // click listener to go to the previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryProductsActivity.this, CategoriesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // display a message in case there isn't any product in this category
        ProductsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                for (DataSnapshot prod : snapshot.getChildren()){
                    if(prod.child("category").getValue().equals(category)){
                        exists = true;
                    }
                }
                if(exists == false){
                    noCategoryProducts.setVisibility(View.VISIBLE);
                }
                else {
                    noCategoryProducts.setVisibility(View.GONE);
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
        Intent intent = new Intent(CategoryProductsActivity.this, CategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // RecyclerView created to store and display category products
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef.orderByChild("category").equalTo(category), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        if(model.getDiscount() == 0){
                            holder.txtProductPrice.setText("Price: " + model.getPrice() + " lei");
                        }
                        else{
                            String txt = "Price: " + model.getPrice() + " lei " + model.getDiscountPrice() + " lei";
                            holder.txtProductPrice.setText(txt, TextView.BufferType.SPANNABLE);
                            Spannable spannable = (Spannable) holder.txtProductPrice.getText();
                            spannable.setSpan(new StrikethroughSpan(), 7, txt.length() - (model.getDiscountPrice() + " lei").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#e71826")), 7, txt.length() - (model.getDiscountPrice() + " lei").length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                    Intent intent = new Intent(CategoryProductsActivity.this, ProductDetailsActivity.class);
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