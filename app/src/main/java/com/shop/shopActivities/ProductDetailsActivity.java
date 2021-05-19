package com.shop.shopActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.shop.ARactivities.MakeUpActivity;
import com.shop.ARactivities.TryOnActivity;
import com.shop.R;
import com.shop.models.Comments;
import com.shop.models.Products;
import com.shop.helpers.Prevalent;
import com.shop.viewholders.CommentsViewHolder;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addCommentBtn;
    private FloatingActionButton addToCartBtn, addToWhislistBtn, removeFromWhislistBtn, tryItOnBtn;
    private ImageView productImage, userImage;
    private ElegantNumberButton numberBtn;
    private TextView productPrice, productDescription, productName, backBtn;
    private EditText commentContentEditTxt;
    private String productID = "", state = "normal";
    private String image;
    private LinearLayoutManager layoutManager;
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
        addToWhislistBtn = findViewById(R.id.details_add_product_to_wishlist);
        removeFromWhislistBtn = findViewById(R.id.details_remove_from_whislist);
        tryItOnBtn = findViewById(R.id.ar_btn);
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).into(userImage);

        recyclerView = findViewById(R.id.comment_list);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        productID = getIntent().getStringExtra("pid");
        getProductDetails(productID);

        // add a commment button
        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddComment();
            }
        });

        // add the product to cart button
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToCart();
            }
        });

        addToWhislistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToWhislist();
            }
        });

        removeFromWhislistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Whislist").child(EncodeString(Prevalent.currentOnlineUser.getEmail())).child(productID).removeValue();
                Toast.makeText(ProductDetailsActivity.this, "Removed from whislist!", Toast.LENGTH_SHORT).show();
                removeFromWhislistBtn.setVisibility(View.GONE);
                addToWhislistBtn.setVisibility(View.VISIBLE);
            }
        });

        // click listener to go to the previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Whislist").child(EncodeString(Prevalent.currentOnlineUser.getEmail())).child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    removeFromWhislistBtn.setVisibility(View.VISIBLE);
                    addToWhislistBtn.setVisibility(View.GONE);
                }
                else{
                    removeFromWhislistBtn.setVisibility(View.GONE);
                    addToWhislistBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        getComments(productID);

        tryItOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Products").child(productID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Products product = snapshot.getValue(Products.class);
                            if (product.getCategory().equals("decorations")){
                                Intent intent = new Intent(ProductDetailsActivity.this, TryOnActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("pid", productID);
                                startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(ProductDetailsActivity.this, MakeUpActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("pid", productID);
                                startActivity(intent);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void addingToWhislist() {
        FirebaseDatabase.getInstance().getReference().child("Products").child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    HashMap<String, Object> whislistMap = new HashMap<>();
                    whislistMap.put("pid", snapshot.child("pid").getValue().toString());

                    FirebaseDatabase.getInstance().getReference().child("Whislist").child(EncodeString(Prevalent.currentOnlineUser.getEmail())).child(productID)
                            .updateChildren(whislistMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ProductDetailsActivity.this, "Added to whislist!", Toast.LENGTH_SHORT).show();
                                        addToWhislistBtn.setVisibility(View.GONE);
                                        removeFromWhislistBtn.setVisibility(View.VISIBLE);
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
    protected void onStart() {
        super.onStart();

        CheckOrderState();

        getComments(productID);
    }

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    // add product to cart
    private void addingToCart() {
        Calendar calendar = Calendar.getInstance();
        String saveCurrentTime, saveCurrentDate;

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-mmm-yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss:SSS a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        String pricetxt = productPrice.getText().toString();
        double sub;
        if(pricetxt.substring(pricetxt.indexOf("lei") + 3, pricetxt.length()).isEmpty()){
            sub = Double.parseDouble((pricetxt).substring(0, (pricetxt).length() - 4));
        }
        else{
            sub = Double.parseDouble(pricetxt.substring(pricetxt.indexOf("lei") + 3, pricetxt.length() - 4));
        }
        cartMap.put("price", sub);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberBtn.getNumber());
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
                                                Intent intent = new Intent(ProductDetailsActivity.this, CartActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    // display the product information from the database
    private void getProductDetails(String productID) {
        DatabaseReference ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        ProductsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Products product = dataSnapshot.getValue(Products.class);
                    productName.setText(product.getPname());
                    productDescription.setText(product.getDescription());
                    if(product.getDiscount() == 0){
                        productPrice.setText(product.getPrice()+ " lei");
                    }
                    else{
                        String txt = product.getPrice() + " lei " + product.getDiscountPrice() + " lei";
                        productPrice.setText(txt, TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) productPrice.getText();
                        spannable.setSpan(new StrikethroughSpan(), 0, txt.length() - (product.getDiscountPrice() + " lei").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#e71826")), 0, txt.length() - (product.getDiscountPrice() + " lei").length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
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

    // display the comments at this product from the database
    private void getComments(String productID){
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(productID);

        // RecyclerView created to store and display product comments
        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(commentsRef, Comments.class)
                        .build();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comments model) {
                        try {
                            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(model.getDate());
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            String DateToStr = format.format(date);

                            Date time= new SimpleDateFormat("HH:mm:ss a").parse(model.getTime());
                            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                            String TimeToStr = format2.format(time);

                            holder.commentDate.setText(TimeToStr + ", " + DateToStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        holder.commentContent.setText(model.getContent());

                        FirebaseDatabase.getInstance().getReference().child("Users").child(EncodeString(model.getUserEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    holder.userName.setText(snapshot.child("name").getValue().toString());
                                    Picasso.get().load(snapshot.child("image").getValue().toString()).into(holder.userImage);
                                }
                                else {
                                    FirebaseDatabase.getInstance().getReference().child("Google Users").child(EncodeString(model.getUserEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                holder.userName.setText(snapshot.child("name").getValue().toString());
                                                Picasso.get().load(snapshot.child("image").getValue().toString()).into(holder.userImage);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

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

    // verify the order state
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

    // add a comment in the database for this specific product
    private void AddComment() {
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(productID);

        addCommentBtn.setVisibility(View.GONE);
        String commentContent = commentContentEditTxt.getText().toString();
        String userEmail = Prevalent.currentOnlineUser.getEmail();

        if(!TextUtils.isEmpty(commentContent)) {

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            String date = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss:SSS a");
            String time = currentTime.format(calendar.getTime());

            HashMap<String, Object> commentMap = new HashMap<>();
            commentMap.put("content", commentContent);
            commentMap.put("userEmail", userEmail);
            commentMap.put("date", date);
            commentMap.put("time", time);

            commentRef.child(date + time).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        addCommentBtn.setVisibility(View.VISIBLE);
                        commentContentEditTxt.setText("");
                        Toast.makeText(ProductDetailsActivity.this, "Comment added!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, "Fail to add comment!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(ProductDetailsActivity.this, "Comment can't be empty", Toast.LENGTH_SHORT).show();
            addCommentBtn.setVisibility(View.VISIBLE);
        }
    }
}