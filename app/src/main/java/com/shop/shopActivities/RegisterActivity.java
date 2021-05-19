package com.shop.shopActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shop.helpers.BCrypt;
import com.shop.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreatAccountButton;
    private EditText InputName, InputEmail, InputPassword, InputConfirmPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreatAccountButton = findViewById(R.id.register_btn);
        InputName = findViewById(R.id.register_username_input);
        InputEmail = findViewById(R.id.register_email_input);
        InputPassword = findViewById(R.id.register_password_input);
        InputConfirmPassword = findViewById(R.id.register_confirm_password_input);
        loadingBar = new ProgressDialog(this);

        // create account button
        CreatAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    // verify if every field is completed
    private void CreateAccount() {
        String name = InputName.getText().toString();
        String email = InputEmail.getText().toString();
        String password = InputPassword.getText().toString();
        String confirmPassword = InputConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name!", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email!", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password!", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please confirm your password!", Toast.LENGTH_SHORT).show();
        }

        else if (!password.equals(confirmPassword)){
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
        }

        else if (!email.contains("@") && !email.contains(".") ) {
            Toast.makeText(this, "Not a valid email address!", Toast.LENGTH_SHORT).show();
        }

        else if (password.length() < 4){
            Toast.makeText(this, "Password must contain at least one lowercase letter, one capital letter, one number digit, one special character and minimum length is 4!", Toast.LENGTH_LONG).show();
        }
        else {
             loadingBar.setTitle("Create Account");
             loadingBar.setMessage("Please wait, we are checking the credentials.");
             loadingBar.setCanceledOnTouchOutside(false);
             loadingBar.show();

             ValidateEmail(name, email, password);
        }
    }

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    // check if the email doesn't already exist and register the user
    private void ValidateEmail(String name, String email, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(EncodeString(email)).exists())) {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("email", email);
                    userdataMap.put("name", name);
                    String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                    userdataMap.put("password", hashed);

                    RootRef.child("Users").child(EncodeString(email)).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Your account was created successfully", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, "Network error, please try again!", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "This email address already exists!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}