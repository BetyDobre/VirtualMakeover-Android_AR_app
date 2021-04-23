package com.shop.ARactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shop.R;

import java.io.IOException;

public class MakeUpActivity extends AppCompatActivity implements AugmentedFaceListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_up);

        AugmentedFaceFragment face_view = (AugmentedFaceFragment) getSupportFragmentManager().findFragmentById(R.id.face_view);
        face_view.setAugmentedFaceListener(this);
    }

    @Override
    public void onFaceAdded(AugmentedFaceNode face) {
        try {
            face.setFaceMeshTexture("models/lipstick2.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaceUpdate(AugmentedFaceNode face) {
    }
}