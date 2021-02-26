package com.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shop.adminActivities.AdminEditProductsActivity;
import com.shop.models.Comments;
import com.shop.models.Products;
import com.shop.models.Users;
import com.shop.prevalent.Prevalent;
import com.shop.viewholders.CommentsViewHolder;
import com.shop.viewholders.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCartBtn, addCommentBtn;
    private ImageView productImage, userImage;
    private ElegantNumberButton numberBtn;
    private TextView productPrice, productDescription, productName, backBtn;
    private EditText commentContentEditTxt;
    private String productID = "", state = "normal";
    private String image;

    RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

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
        backBtn = findViewById(R.id.back_home_txt);
        commentContentEditTxt = findViewById(R.id.post_detail_comment);
        addCommentBtn = findViewById(R.id.post_detail_add_comment_btn);
        userImage = findViewById(R.id.post_detail_currentuser_img);
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).into(userImage);

        recyclerView = findViewById(R.id.comment_list);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        productID = getIntent().getStringExtra("pid");
        getProductDetails(productID);

        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddComment();
            }
        });

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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        getComments(productID);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();
        getComments(productID);
    }

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    private void addingToCart() {

        Calendar calendar = Calendar.getInstance();
        String saveCurrentTime, saveCurrentDate;

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-mmm-yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

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

    private void getComments(String productID){
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(productID);
        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(commentsRef, Comments.class)
                        .build();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comments model) {
                        Picasso.get().load(model.getUserImg()).into(holder.userImage);
                        holder.commentDate.setText(model.getTime() + " " + model.getDate());
                        holder.commentContent.setText(model.getContent());
                        holder.userName.setText(model.getUserName());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(Prevalent.currentOnlineUser.getEmail().equals(model.getUserEmail())) {
                                    CharSequence options[] = new CharSequence[]{
                                            "Delete comment"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
                                    builder.setTitle("Comment options:");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                                commentsRef.child(model.getDate() + model.getTime())
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(ProductDetailsActivity.this, "Comment deleted!", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(ProductDetailsActivity.this, ProductDetailsActivity.class);
                                                                    intent.putExtra("pid", productID);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
                        CommentsViewHolder holder = new CommentsViewHolder(view);
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

    private void AddComment() {
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(productID);

        addCommentBtn.setVisibility(View.GONE);
        String commentContent = commentContentEditTxt.getText().toString();
        String userName = Prevalent.currentOnlineUser.getName();
        String userImg = Prevalent.currentOnlineUser.getImage();
        String userEmail = Prevalent.currentOnlineUser.getEmail();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-mmm-yyyy");
        String date = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        String time = currentTime.format(calendar.getTime());

        HashMap<String, Object> commentMap = new HashMap<>();
        commentMap.put("content", commentContent);
        commentMap.put("userName", userName);
        commentMap.put("userImg", userImg);
        commentMap.put("userEmail", userEmail);
        commentMap.put("date", date);
        commentMap.put("time", time);

        commentRef.child(date+time).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    addCommentBtn.setVisibility(View.VISIBLE);
                    commentContentEditTxt.setText("");
                    Toast.makeText(ProductDetailsActivity.this, "Comment added!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ProductDetailsActivity.this, "Fail to add comment!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}