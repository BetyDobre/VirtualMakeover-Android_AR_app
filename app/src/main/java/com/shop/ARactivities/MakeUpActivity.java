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
    private ChipGroup chipGroup, chipGroup2;
    private Chip shade1, shade2, shade3, shade4, shade5, shade6;

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
        chipGroup2 = findViewById(R.id.contour_group2);
        shade1 = findViewById(R.id.chip_shade1);
        shade2 = findViewById(R.id.chip_shade2);
        shade3 = findViewById(R.id.chip_shade3);
        shade4 = findViewById(R.id.chip_shade4);
        shade5 = findViewById(R.id.chip_shade5);
        shade6 = findViewById(R.id.chip_shade6);

        if (productID.equals("04-05-202116:08:30 PM")) {
            textMaterial.setVisibility(View.VISIBLE);
            chipGroup.setVisibility(View.VISIBLE);
        }
        else if (productID.equals("04-05-202116:06:30 PM")){
            textMaterial.setVisibility(View.VISIBLE);
            chipGroup.setVisibility(View.VISIBLE);
            chipGroup2.setVisibility(View.VISIBLE);
        }

        // java
        shade1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] color = {0.545f, 0.196f, 0.223f, 1f};
                changeContour(color);
            }
        });

        // banana
        shade2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] color = {0.992f, 0.972f, 0.749f, 1f};
                changeContour(color);
            }
        });

        // peach
        shade3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] color = {0.7686f, 0.5411f, 0.4f, 1f};
                changeContour(color);
            }
        });

        // green
        shade4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] color = {0.035f, 0.745f, 0.145f, 1f};
                changeContour(color);
            }
        });

        // blue
        shade5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] color = {0.019f, 0f, 0.980f, 1f};
                changeContour(color);
            }
        });

        // purple
        shade6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] color = {0.6f, 0.2f, 0.756f, 1f};
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
        else if (productID.equals("18-048-202100:48:50 AM")){
            try {
                face.setRegionModel(AugmentedFaceNode.FaceLandmark.NOSE_TIP,
                        "models/sunglasses.obj",
                        "models/sunglassesTexture.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                face.setRegionModel(AugmentedFaceNode.FaceLandmark.NOSE_TIP,
                        "models/sunglasses.obj",
                        "models/sunglasses2Texture.png");
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
                Toast.makeText(activity, "App needs OpenGl Version 2.0 or later", Toast.LENGTH_SHORT).show();
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