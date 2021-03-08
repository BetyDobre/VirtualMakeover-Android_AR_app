package com.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class AboutUsActivity extends AppCompatActivity {

    private ViewFlipper v_flipper;
    private TextView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        v_flipper = findViewById(R.id.v_flipper);

        ArrayList<Integer> myImageList = new ArrayList<>();
        myImageList.add(R.drawable.eu);
        myImageList.add(R.drawable.working3);
        myImageList.add(R.drawable.ar);
        myImageList.add(R.drawable.working);
        myImageList.add(R.drawable.working2);
        for (Integer img : myImageList) {
            //call function which assigns each photo to the flipper
            flipperImages(img);
        }

        backBtn = findViewById(R.id.back_to_home_from_about_txt);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutUsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void flipperImages(Integer image) {
        //for image screats an image view in the flipper
        //sets time to display the image, in which way it should slide, if the flipper should
        // automatically start sliding photos
        ImageView imageView = new ImageView(this);

        imageView.setBackgroundResource(image);
        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(3000);
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(this,android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(this,android.R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(AboutUsActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}