 package com.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shop.prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

 public class OrderActivity extends AppCompatActivity {

    private EditText nameEdiText, phoneEditText, addressEditText, cityEditText;
    private Button placeOrderBtn;
    private String totalPrice = "", email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        placeOrderBtn = findViewById(R.id.place_order_btn);
        nameEdiText = findViewById(R.id.shippment_name);
        phoneEditText = findViewById(R.id.shippment_phone);
        addressEditText = findViewById(R.id.shippment_address);
        cityEditText = findViewById(R.id.shippment_city);
        totalPrice = getIntent().getStringExtra("Total price");
        email = Prevalent.currentOnlineUser.getEmail();

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDetails();
            }
        });
    }

     public String EncodeString(String string) {
         return string.replace(".", ",");
     }

     private void CheckDetails() {
        if (TextUtils.isEmpty(nameEdiText.getText().toString())){
            Toast.makeText(OrderActivity.this, "Please provide your full name!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(OrderActivity.this, "Please provide your phone number!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(OrderActivity.this, "Please provide your address!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cityEditText.getText().toString())){
            Toast.makeText(OrderActivity.this, "Please provide your city name!", Toast.LENGTH_SHORT).show();
        }
        else {
            PlaceOrder();
        }
    }

     private void PlaceOrder() {
         Calendar calForDate = Calendar.getInstance();
         final String saveCurrentTime, saveCurrentDate;

         SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
         saveCurrentDate = currentDate.format(calForDate.getTime());

         SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
         saveCurrentTime = currentDate.format(calForDate.getTime());

         final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                 .child(EncodeString(Prevalent.currentOnlineUser.getEmail()));

         HashMap<String, Object> ordersMap = new HashMap<>();
         ordersMap.put("totalAmount", totalPrice);

         ordersMap.put("name", nameEdiText.getText().toString());
         ordersMap.put("phone", phoneEditText.getText().toString());
         ordersMap.put("address", addressEditText.getText().toString());
         ordersMap.put("city", cityEditText.getText().toString());
         ordersMap.put("date", saveCurrentDate);
         ordersMap.put("time", saveCurrentTime);
         ordersMap.put("state", "not shipped");
         ordersMap.put("email", email);

         ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()){
                     FirebaseDatabase.getInstance().getReference()
                             .child("Cart List")
                             .child("User View")
                             .child(EncodeString(Prevalent.currentOnlineUser.getEmail()))
                             .removeValue()
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()){
                                         Toast.makeText(OrderActivity.this, "Your order has been placed!", Toast.LENGTH_SHORT).show();
                                         Intent intent = new Intent(OrderActivity.this, HomeActivity.class);
                                         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                         startActivity(intent);
                                     }
                                 }
                             });
                 }
             }
         });
     }
 }