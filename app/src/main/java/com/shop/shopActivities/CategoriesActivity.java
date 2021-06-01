package com.shop.shopActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shop.R;

public class CategoriesActivity extends AppCompatActivity {
    private RelativeLayout layoutGlasses, layoutMakeup, layoutLipsticks, layoutDecorations;
    private TextView backBtn;
    private Button allProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        layoutGlasses = findViewById(R.id.layout_category_glasses);
        layoutMakeup= findViewById(R.id.layout_category_makeup);
        layoutLipsticks = findViewById(R.id.layout_category_lipstick);
        layoutDecorations = findViewById(R.id.layout_category_decoration);
        allProducts = findViewById(R.id.all_categories_products_btn);
        backBtn = findViewById(R.id.back_to_home_from_categories_txt);

        // go to glasses category products activity
        layoutGlasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, CategoryProductsActivity.class);
                intent.putExtra("category", "glasses");
                startActivity(intent);
            }
        });

        // go to makeup category products activity
        layoutMakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, CategoryProductsActivity.class);
                intent.putExtra("category", "makeup");
                startActivity(intent);
            }
        });

        // go to lipsticks category products activity
        layoutLipsticks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, CategoryProductsActivity.class);
                intent.putExtra("category", "lipsticks");
                startActivity(intent);
            }
        });

        // go to decorations category products activity
        layoutDecorations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, CategoryProductsActivity.class);
                intent.putExtra("category", "decorations");
                startActivity(intent);
            }
        });

        // go to home activity
        allProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // click listener to go to the previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CategoriesActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}