package com.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class OrderActivity extends AppCompatActivity {

    private EditText nameEdiText, phoneEditText, addressEditText, cityEditText;
    private Button placeOrderBtn;
    private String totalPrice = "";
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
    }
}