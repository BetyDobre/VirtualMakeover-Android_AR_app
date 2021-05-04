package com.shop.ARactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textview.MaterialTextView;
import com.shop.R;
import com.shop.shopActivities.ProductDetailsActivity;

import java.io.IOException;
import java.util.Objects;

public class MakeUpActivity extends AppCompatActivity implements AugmentedFaceListener {

    private String productID = "";
    private boolean contourChanged = false;
    private float[] contourColor = {0.686f, 0.5f, 0.38f, 1f};
    private MaterialTextView textMaterial;
    private ChipGroup chipGroup;
    private Chip shade1, shade2, shade3;

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

        textMaterial = findViewById((R.id.contour_text));
        chipGroup = findViewById(R.id.contour_group);
        shade1 = findViewById(R.id.chip_shade1);
        shade2 = findViewById(R.id.chip_shade2);
        shade3 = findViewById(R.id.chip_shade3);

        if (productID.equals("04-05-202116:08:30 PM") || productID.equals("04-05-202116:06:30 PM")) {
            textMaterial.setVisibility(View.VISIBLE);
            chipGroup.setVisibility(View.VISIBLE);
        }

        shade1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] color = {0.2f, 0.1f, 0.5f, 1f};
                changeContour(color);
            }
        });

        shade2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] color = {0.95f, 0.85f, 0.66f, 1f};
                changeContour(color);
            }
        });

        shade3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] color = {0.7686f, 0.5411f, 0.4f, 1f};
                changeContour(color);
            }
        });

    }

    private void changeContour(float[] color) {
        contourColor = color;
        contourChanged = true;
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
        else if (productID.equals("04-05-202116:06:30 PM")){
            try {
                face.setFaceMeshTexture("models/eyeShadow.png");
                face.setContourColor(contourColor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (productID.equals("04-05-202116:08:30 PM")){
            try {
                face.setFaceMeshTexture("models/contour.png");
                face.setContourColor(contourColor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                face.setRegionModel(AugmentedFaceNode.FaceLandmark.NOSE_TIP,
                        "models/untitled.obj",
                        "models/glasses.png ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFaceUpdate(AugmentedFaceNode face) {
        if (contourChanged) {
            contourChanged = false;
            face.setContourColor(contourColor);
        }
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