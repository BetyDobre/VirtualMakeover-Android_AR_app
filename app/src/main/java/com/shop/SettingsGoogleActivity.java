package com.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shop.adminActivities.AdminEditProductsActivity;
import com.shop.prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsGoogleActivity extends AppCompatActivity {

    private EditText addressEditText;
    private CircleImageView profileImageView;
    private TextView closeTextBtn, saveTextBtn, currentPasswordTxt, currentEmailTxt, currentNameTxt;

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

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

        Picasso.get().load(picture).into(profileImageView);
        currentNameTxt.setText(name);
        currentEmailTxt.setText(email);

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Google Users").child(EncodeString(Prevalent.currentOnlineUser.getEmail()));
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("address").exists()) {
                        String address = dataSnapshot.child("address").getValue().toString();
                        addressEditText.setText(address);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = addressEditText.getText().toString();
                HashMap<String, Object> userdataMap = new HashMap<>();
                userdataMap.put("address", address);
                FirebaseDatabase.getInstance().getReference().child("Google Users").child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(SettingsGoogleActivity.this, HomeActivity.class);
                            startActivity(intent);
                            Toast.makeText(SettingsGoogleActivity.this, "Information updated succesfully!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SettingsGoogleActivity.this, "Error on updating information!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}