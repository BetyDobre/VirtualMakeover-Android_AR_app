package com.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsGoogleActivity extends AppCompatActivity {

    private EditText addressEditText;
    private CircleImageView profileImageView;
    private TextView closeTextBtn, saveTextBtn, currentPasswordTxt, currentEmailTxt, currentNameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_google);

        addressEditText = findViewById(R.id.settings_address);
        profileImageView = findViewById(R.id.settings_profile_image);
        closeTextBtn = findViewById(R.id.close_settings);
        saveTextBtn = findViewById(R.id.update_account_settings);
        currentPasswordTxt = findViewById(R.id.settings_current_password);
        currentEmailTxt = findViewById(R.id.settings_email);
        currentNameTxt = findViewById(R.id.settings_full_name);


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String name = account.getDisplayName();
        Uri picture = account.getPhotoUrl();
        String email = account.getEmail();
        String address = addressEditText.getText().toString();


        Picasso.get().load(picture).into(profileImageView);
        currentNameTxt.setText(name);
        currentEmailTxt.setText(email);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}