package com.shop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.shop.prevalent.Prevalent;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullnameEditText, userEmailEditText, addressEditText, passwordEditText, confirmNewPasswordEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextBtn, currentPasswordTxt;
    private Uri imageUri;
    private String myURL = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureReference;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureReference = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = findViewById(R.id.settings_profile_image);
        fullnameEditText = findViewById(R.id.settings_full_name);
        userEmailEditText = findViewById(R.id.settings_email);
        addressEditText = findViewById(R.id.settings_address);
        passwordEditText = findViewById(R.id.settings_password);
        confirmNewPasswordEditText = findViewById(R.id.settings_confirm_password);
        profileChangeTextBtn = findViewById(R.id.profile_image_change_btn);
        closeTextBtn = findViewById(R.id.close_settings);
        saveTextBtn = findViewById(R.id.update_account_settings);
        currentPasswordTxt = findViewById(R.id.settings_current_password);

        userInfoDisplay(profileImageView, fullnameEditText, userEmailEditText, addressEditText, passwordEditText, confirmNewPasswordEditText);

        // close the settings options button
        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // save information button
        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")) {
                    userInfoSaved();
                }
                else {
                    updateOnlyUserInfo();
                }
            }
        });

        // change the profile picture button
        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // update user info expect profile picture
    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullnameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("email", userEmailEditText.getText().toString());
        Prevalent.currentOnlineUser.setName(fullnameEditText.getText().toString());

        if((passwordEditText.getText().toString()).equals(confirmNewPasswordEditText.getText().toString()) && passwordEditText.getText().toString().trim().length() > 3){
            userMap.put("password", passwordEditText.getText().toString());
            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            Toast.makeText(SettingsActivity.this, "Profile info updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if (passwordEditText.getText().toString().trim().length() == 0){
            userMap.put("password", currentPasswordTxt.getText().toString());
            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
            Toast.makeText(SettingsActivity.this, "Profile info updated successfully!", Toast.LENGTH_SHORT).show();
        }
        else if (passwordEditText.getText().toString().trim().length() < 4){
            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
            Toast.makeText(SettingsActivity.this, "Password too short(minimum 4 characters)", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
        else if(!(passwordEditText.getText().toString()).equals(confirmNewPasswordEditText.getText().toString())){
            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
            Toast.makeText(SettingsActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Error, try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    // check if the fields are empty
    private void userInfoSaved() {
        if (TextUtils.isEmpty(fullnameEditText.getText().toString())){
            Toast.makeText(this, "Name is mandatory!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Address is mandatory!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userEmailEditText.getText().toString())){
            Toast.makeText(this, "Email is mandatory!", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked")){
            uploadImage();
        }
    }

    // upload profile picture into the database
    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update profile image");
        progressDialog.setMessage("Please wait, we are checking your account information");
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileRef = storageProfilePictureReference
                    .child(Prevalent.currentOnlineUser.getEmail() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadURL = task.getResult();
                        myURL = downloadURL.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", fullnameEditText.getText().toString());
                        userMap.put("address", addressEditText.getText().toString());
                        userMap.put("email", userEmailEditText.getText().toString());
                        userMap.put("image", myURL);
                        Prevalent.currentOnlineUser.setName(fullnameEditText.getText().toString());
                        Prevalent.currentOnlineUser.setImage(myURL);

                        if((passwordEditText.getText().toString()).equals(confirmNewPasswordEditText.getText().toString()) && passwordEditText.getText().toString().trim().length() > 3){
                            userMap.put("password", passwordEditText.getText().toString());
                            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
                            Picasso.get().load(myURL).into(profileImageView);
                            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                            Toast.makeText(SettingsActivity.this, "Profile info updated successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else if (passwordEditText.getText().toString().trim().length() == 0){
                            userMap.put("password", currentPasswordTxt.getText().toString());
                            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
                            Picasso.get().load(myURL).into(profileImageView);
                            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                            Toast.makeText(SettingsActivity.this, "Profile info updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                        else if (passwordEditText.getText().toString().trim().length() < 4){
                            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
                            Toast.makeText(SettingsActivity.this, "Password too short(minimum 4 characters)", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                            finish();
                        }
                        else if(!(passwordEditText.getText().toString()).equals(confirmNewPasswordEditText.getText().toString())){
                            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
                            Toast.makeText(SettingsActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                            finish();
                        }
                    }
                    else {
                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
        else{
            Toast.makeText(SettingsActivity.this, "Image is not selected!", Toast.LENGTH_SHORT).show();
        }
    }

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    // display the already existing user info
    private void userInfoDisplay(CircleImageView profileImageView, EditText fullnameEditText, EditText userEmailEditText, EditText addressEditText, EditText passwordEditText, EditText confirmNewPasswordEditText) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(EncodeString(Prevalent.currentOnlineUser.getEmail()));

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        String password = dataSnapshot.child("password").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullnameEditText.setText(name);
                        userEmailEditText.setText(email);
                        addressEditText.setText(address);
                        currentPasswordTxt.setText(password);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}