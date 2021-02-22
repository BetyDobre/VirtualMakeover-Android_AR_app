package com.shop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminEditProductsActivity extends AppCompatActivity {

    private Button saveChangesBtn, deleteBtn;
    private EditText name, price, description;
    private ImageView image;
    private String productID = "";
    private DatabaseReference productsRef;

    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private StorageReference ProductImagesRef;
    private String downloadImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_products);

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");

        saveChangesBtn = findViewById(R.id.apply_changes_btn);
        name = findViewById(R.id.product_name_edit);
        price = findViewById(R.id.product_price_edit);
        description = findViewById(R.id.product_description_edit);
        image = findViewById(R.id.product_image_edit);
        deleteBtn = findViewById(R.id.delete_product_btn);

        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
        displaySpecificProductInfo();

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSpecificProduct();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            image.setImageURI(ImageUri);
        }
    }

    private void saveChanges() {
        String newName = name.getText().toString();
        String newPrice = price.getText().toString();
        String newDescription = description.getText().toString();

        if(newName.equals("")){
            Toast.makeText(this, "Please write the product name!", Toast.LENGTH_SHORT).show();
        }
        else if(newPrice.equals("")){
            Toast.makeText(this, "Please write the product price!", Toast.LENGTH_SHORT).show();
        }
        else if(newDescription.equals("")){
            Toast.makeText(this, "Please write the product description!", Toast.LENGTH_SHORT).show();
        }
        else {
            //store the image url to the firebase storage
            if(ImageUri != null) {
                StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productID + ".jpg");
                final UploadTask uploadTask = filePath.putFile(ImageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = e.toString();
                        Toast.makeText(AdminEditProductsActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                downloadImageURL = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadImageURL = task.getResult().toString();
                                    HashMap<String, Object> productMap = new HashMap<>();
                                    productMap.put("pid", productID);
                                    productMap.put("description", newDescription);
                                    productMap.put("price", newPrice);
                                    productMap.put("pname", newName);
                                    productMap.put("image", downloadImageURL);

                                    productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(AdminEditProductsActivity.this, AdminCategoryActivity.class);
                                                startActivity(intent);
                                                finish();

                                                Toast.makeText(AdminEditProductsActivity.this, "Product info updated successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
            else {
                HashMap<String, Object> productMap = new HashMap<>();
                productMap.put("pid", productID);
                productMap.put("description", newDescription);
                productMap.put("price", newPrice);
                productMap.put("pname", newName);

                productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(AdminEditProductsActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(AdminEditProductsActivity.this, "Product info updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void deleteSpecificProduct() {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(AdminEditProductsActivity.this, AdminCategoryActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(AdminEditProductsActivity.this, "Product deleted successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String pname = snapshot.child("pname").getValue().toString();
                    String pprice = snapshot.child("price").getValue().toString();
                    String pdescription = snapshot.child("description").getValue().toString();
                    String pimage = snapshot.child("image").getValue().toString();

                    name.setText(pname);
                    price.setText(pprice);
                    description.setText(pdescription);
                    Picasso.get().load(pimage).into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}