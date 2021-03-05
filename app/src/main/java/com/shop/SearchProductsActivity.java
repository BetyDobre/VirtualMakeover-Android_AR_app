package com.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shop.adminActivities.AdminEditProductsActivity;
import com.shop.models.Products;
import com.shop.viewholders.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchProductsActivity extends AppCompatActivity {

    private ImageButton searchBtn;
    private ImageView backBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String searchInput, type = "";
    private ArrayList<Products> products = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private TextView noProductsFoundTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        inputText = findViewById(R.id.search_product);
        searchBtn = findViewById(R.id.search_btn);
        backBtn = findViewById(R.id.back_to_home_from_search_img);
        noProductsFoundTxt = findViewById(R.id.no_searched_products_txt);

        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchProductsActivity.this));

        type = getIntent().getStringExtra("type");

        // search products button
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStart();
            }
        });

        // click listener to go to the previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchProductsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                if(type.equals("admin")){
                    intent.putExtra("Admin", "Admin");
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SearchProductsActivity.this, HomeActivity.class);
        if(type.equals("admin")){
            intent.putExtra("Admin", "Admin");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products");

        searchInput = inputText.getText().toString();

        products.clear();
        searchList.removeAllViews();

        // search the specific product by name
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

                if(products.size() == 0){
                    noProductsFoundTxt.setVisibility(View.VISIBLE);
                }
                else {
                    noProductsFoundTxt.setVisibility(View.GONE);
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
                    if (type.equals("user")) {
                        Intent intent = new Intent(SearchProductsActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("pid", products.get(position).getPid());
                        startActivity(intent);
                    }
                    else if(type.equals("admin")){
                        Intent intent = new Intent(SearchProductsActivity.this, AdminEditProductsActivity.class);
                        intent.putExtra("pid", products.get(position).getPid());
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }
    }
}