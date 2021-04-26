package com.shop.ARactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.shop.R;
import com.shop.shopActivities.ProductDetailsActivity;

import java.io.IOException;
import java.util.Objects;

public class MakeUpActivity extends AppCompatActivity implements AugmentedFaceListener {

    private String productID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_up);

        productID = getIntent().getStringExtra("pid");

        if(!checkSystemSupport(MakeUpActivity.this)){
            return;
        }

        AugmentedFaceFragment face_view = (AugmentedFaceFragment) getSupportFragmentManager().findFragmentById(R.id.face_view);
        face_view.setAugmentedFaceListener(this);
    }

    @Override
    public void onFaceAdded(AugmentedFaceNode face) {
        if (productID.equals("26-04-202112:20:15 PM")){
            try {
                face.setFaceMeshTexture("models/lipstick1.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (productID.equals("26-04-202112:25:37 PM")){
            try {
                face.setFaceMeshTexture("models/lipstick2.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                face.setFaceMeshTexture("models/freckles.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFaceUpdate(AugmentedFaceNode face) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MakeUpActivity.this, ProductDetailsActivity.class);
        intent.putExtra("pid", productID);
        startActivity(intent);
    }

    public static boolean checkSystemSupport(Activity activity) {
        // checking whether the API version of the running Android >= 24
        // that means Android Nougat 7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            String openGlVersion = ((ActivityManager) Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE))).getDeviceConfigurationInfo().getGlEsVersion();

            // checking whether the OpenGL version >= 3.0
            if (Double.parseDouble(openGlVersion) >= 3.0) {
                return true;
            } else {
                Toast.makeText(activity, "App needs OpenGl Version 3.0 or later", Toast.LENGTH_SHORT).show();
                activity.finish();
                return false;
            }
        } else {
            Toast.makeText(activity, "App does not support required Build Version", Toast.LENGTH_SHORT).show();
            activity.finish();
            return false;
        }
    }
}