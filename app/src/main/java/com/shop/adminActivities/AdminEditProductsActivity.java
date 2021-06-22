package com.shop.adminActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.shop.shopActivities.HomeActivity;
import com.shop.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class AdminEditProductsActivity extends AppCompatActivity {

    private Button saveChangesBtn, deleteBtn;
    private EditText name, price, description, discount;
    private ImageView image;
    private TextView backBtn;
    private String productID = "";
    private DatabaseReference productsRef;

    private Uri ImageUri;
    private StorageReference ProductImagesRef;
    private String downloadImageURL;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    String cameraPermission[];
    String storagePermission[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_products);

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");

        saveChangesBtn = findViewById(R.id.apply_changes_btn);
        name = findViewById(R.id.product_name_edit);
        price = findViewById(R.id.product_price_edit);
        description = findViewById(R.id.product_description_edit);
        discount = findViewById(R.id.product_discount_edit);
        image = findViewById(R.id.product_image_edit);
        deleteBtn = findViewById(R.id.delete_product_btn);
        backBtn = findViewById(R.id.back_to_maintain_products_txt);

        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        displaySpecificProductInfo();

        // click listener to save product information changes
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        // click listener to delete the product
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSpecificProduct();
            }
        });

        // click listener to change the product image
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePicDialog();
            }
        });

        // click listener to go to the previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminEditProductsActivity.this, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminEditProductsActivity.this, HomeActivity.class);
        intent.putExtra("Admin", "Admin");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickCamera();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        //intent to take image from camera, it will also be save to storage to get high quality
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic"); //title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text"); //description
        ImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData())
                        .setAspectRatio(3, 2)
                        .start(AdminEditProductsActivity.this);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(ImageUri)
                        .setAspectRatio(3, 2)
                        .start(AdminEditProductsActivity.this);
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    ImageUri = result.getUri();
                    image.setImageURI(ImageUri);
                }
            }
            else {
                Toast.makeText(this, "Error, try again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminEditProductsActivity.this, AdminEditProductsActivity.class));
                finish();
            }
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        /*Check camera permission and return the result
         *In order to get high quality image we have to save the image into the external storage first
         *before inserting to image view, that's why storage permission will also be required*/
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
    // save information changes for the product
    private void saveChanges() {
        String newName = name.getText().toString();
        String newPrice = price.getText().toString();
        String newDescription = description.getText().toString();
        String newDiscount = discount.getText().toString();

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
                                    productMap.put("price",  Math.round(Double.parseDouble(newPrice)* 100.0) / 100.0);
                                    productMap.put("pname", newName);
                                    productMap.put("image", downloadImageURL);
                                    productMap.put("discount", Integer.parseInt(newDiscount));
                                    double newPriceDiscount = Double.parseDouble(newPrice) - Double.parseDouble(newDiscount)/100 * Double.parseDouble(newPrice);
                                    productMap.put("discountPrice", Math.round(newPriceDiscount* 100.0) / 100.0);

                                    productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(AdminEditProductsActivity.this, AdminHomeActivity.class);
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
                productMap.put("price",  Math.round(Double.parseDouble(newPrice)* 100.0) / 100.0);
                productMap.put("pname", newName);
                productMap.put("discount", Integer.parseInt(newDiscount));
                double newPriceDiscount = Double.parseDouble(newPrice) - Double.parseDouble(newDiscount)/100 * Double.parseDouble(newPrice);
                productMap.put("discountPrice", Math.round(newPriceDiscount* 100.0) / 100.0);

                productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(AdminEditProductsActivity.this, AdminHomeActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(AdminEditProductsActivity.this, "Product info updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    // delete a product from the database
    private void deleteSpecificProduct() {
        // delete from user whislist the product too
        FirebaseDatabase.getInstance().getReference().child("Whislist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot whishUser : snapshot.getChildren()){
                    for(DataSnapshot prod : whishUser.getChildren()){
                        if(prod.child("pid").getValue().equals(productID)){
                            FirebaseDatabase.getInstance().getReference().child("Whislist").child(whishUser.getKey()).child(productID).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        // delete from cart the product too
        FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot user : snapshot.getChildren()){
                    for(DataSnapshot prod : user.child("Products").getChildren()){
                        if(prod.child("pid").getValue().equals(productID)){
                            FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(user.getKey()).child("Products").child(productID).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot user : snapshot.getChildren()){
                    for(DataSnapshot prod : user.child("Products").getChildren()){
                        if(prod.child("pid").getValue().equals(productID)){
                            FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(user.getKey()).child("Products").child(productID).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(AdminEditProductsActivity.this, AdminHomeActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(AdminEditProductsActivity.this, "Product deleted successfully!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    // display in edit texts existing product information from the database
    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String pname = snapshot.child("pname").getValue().toString();
                    String pprice = snapshot.child("price").getValue().toString();
                    String pdescription = snapshot.child("description").getValue().toString();
                    String pimage = snapshot.child("image").getValue().toString();
                    String pdiscount = snapshot.child("discount").getValue().toString();

                    name.setText(pname);
                    price.setText(pprice);
                    description.setText(pdescription);
                    discount.setText(pdiscount);
                    Picasso.get().load(pimage).into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}