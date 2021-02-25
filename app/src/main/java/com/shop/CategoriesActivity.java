package com.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoriesActivity extends AppCompatActivity {

    RelativeLayout layoutGlasses, layoutFoundation, layoutLipsticks, layoutDecorations;
    private TextView backBtn;
    Button allProducts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        layoutGlasses = findViewById(R.id.layout_category_glasses);
        layoutFoundation = findViewById(R.id.layout_category_foundation);
        layoutLipsticks = findViewById(R.id.layout_category_lipstick);
        layoutDecorations = findViewById(R.id.layout_category_decoration);
        allProducts = findViewById(R.id.all_categories_products_btn);
        backBtn = findViewById(R.id.back_to_home_from_categories_txt);

        layoutGlasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, CategoryProductsActivity.class);
                intent.putExtra("category", "glasses");
                startActivity(intent);
            }
        });

        layoutFoundation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, CategoryProductsActivity.class);
                intent.putExtra("category", "foundation");
                startActivity(intent);
            }
        });

        layoutLipsticks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, CategoryProductsActivity.class);
                intent.putExtra("category", "lipsticks");
                startActivity(intent);
            }
        });

        layoutDecorations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, CategoryProductsActivity.class);
                intent.putExtra("category", "decorations");
                startActivity(intent);
            }
        });

        allProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}