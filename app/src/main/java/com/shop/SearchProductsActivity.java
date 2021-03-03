package com.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.ArrayList;

public class SearchProductsActivity extends AppCompatActivity {

    private Button searchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String searchInput;
    private ArrayList<Products> products = new ArrayList<>();
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        inputText = findViewById(R.id.search_product);
        searchBtn = findViewById(R.id.search_btn);
        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchProductsActivity.this));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStart();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SearchProductsActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchInput = inputText.getText().toString();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products");

        products.clear();
        searchList.removeAllViews();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot product : snapshot.getChildren()){
                    String pid = product.getKey();
                    String pname = product.child("pname").getValue(String.class);
                    String cateogry = product.child("category").getValue(String.class);
                    String date = product.child("date").getValue(String.class);
                    String description = product.child("description").getValue(String.class);
                    String image = product.child("image").getValue(String.class);
                    int price = product.child("price").getValue(Integer.class);
                    String time = product.child("time").getValue(String.class);
                    Products p = new Products(pname,description,image,cateogry,pid,date,time,price);

                    if(pname.toLowerCase().contains(searchInput.toLowerCase())){
                        products.add(p);
                    }

                }

                searchAdapter = new SearchAdapter(products);
                searchList.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class SearchAdapter extends RecyclerView.Adapter<ProductViewHolder>{
        ArrayList<Products> products;

        public SearchAdapter(ArrayList<Products> products) {
            this.products = products;
        }


        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
            ProductViewHolder holder = new ProductViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            holder.txtProductName.setText(products.get(position).getPname());
            holder.txtProductDescription.setText(products.get(position).getDescription());
            holder.txtProductPrice.setText("Price: " +products.get(position).getPrice()+"lei");
            Picasso.get().load(products.get(position).getImage()).into(holder.imageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchProductsActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("pid", products.get(position).getPid());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }
    }
}