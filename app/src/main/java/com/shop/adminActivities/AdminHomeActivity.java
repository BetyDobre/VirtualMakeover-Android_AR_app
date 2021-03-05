package com.shop.adminActivities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.shop.HomeActivity;
import com.shop.MainActivity;
import com.shop.R;
import io.paperdb.Paper;

public class AdminHomeActivity extends AppCompatActivity {

    private ImageView glasses, lipsticks, decorations, foundation, hats;
    private Button logoutBtn, checkOrdersBtn, maintaintProductsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        glasses = findViewById(R.id.glasses);
        lipsticks = findViewById(R.id.lipstick);
        decorations = findViewById(R.id.decoration);
        foundation = findViewById(R.id.foundation);
//        hats = findViewById(R.id.hats);
        logoutBtn = findViewById(R.id.admin_logout_btn);
        checkOrdersBtn = findViewById(R.id.check_orders_btn);
        maintaintProductsBtn = findViewById(R.id.maintain_btn);

        // click listener to logout the admin
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy();
                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent. FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // button to go to orders activity
        checkOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminOrdersActivity.class);
                startActivity(intent);
            }
        });

        // button to go to edit product activity
        maintaintProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                startActivity(intent);
            }
        });

        // click listener to add a product in glasses category
        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "glasses");
                startActivity(intent);
            }
        });

        // click listener to add a product in decorations category
        decorations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "decorations");
                startActivity(intent);
            }
        });

        // click listener to add a product in lipsticks category
        lipsticks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "lipsticks");
                startActivity(intent);
            }
        });

        // click listener to add a product in foundation category
        foundation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "foundation");
                startActivity(intent);
            }
        });

        // click listener to add a product in hats category
//        hats.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "hats");
//                startActivity(intent);
//            }
//        });
    }
}