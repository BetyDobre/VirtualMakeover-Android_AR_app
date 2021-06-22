package com.shop.shopActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.shop.R;
import com.shop.helpers.BCrypt;
import com.shop.helpers.Prevalent;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullnameEditText, addressEditText, passwordEditText, confirmNewPasswordEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextBtn, userEmailEditText;
    private Uri imageUri;
    private String myURL = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureReference;
    private String checker = "";
    private Button deleteBtn;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    String cameraPermission[];
    String storagePermission[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureReference = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        profileImageView = findViewById(R.id.settings_profile_image);
        fullnameEditText = findViewById(R.id.settings_full_name);
        userEmailEditText = findViewById(R.id.settings_email);
        addressEditText = findViewById(R.id.settings_address);
        passwordEditText = findViewById(R.id.settings_password);
        confirmNewPasswordEditText = findViewById(R.id.settings_confirm_password);
        profileChangeTextBtn = findViewById(R.id.profile_image_change_btn);
        closeTextBtn = findViewById(R.id.close_settings);
        saveTextBtn = findViewById(R.id.update_account_settings);
        deleteBtn = findViewById(R.id.settings_delete_account);

        userInfoDisplay(profileImageView, fullnameEditText, userEmailEditText, addressEditText);

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
                showImagePicDialog();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CharSequence options[] = new CharSequence[]{
                        "Yes",
                        "No"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Are you sure?");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //edit
                        if (i == 0){
                            FirebaseDatabase.getInstance().getReference().child("Users").child(EncodeString(Prevalent.currentOnlineUser.getEmail())).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(EncodeString(Prevalent.currentOnlineUser.getEmail())).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(EncodeString(Prevalent.currentOnlineUser.getEmail())).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Orders").child(EncodeString(Prevalent.currentOnlineUser.getEmail())).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Orders History").child(EncodeString(Prevalent.currentOnlineUser.getEmail())).removeValue();

                            Paper.book().destroy();
                            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                            Toast.makeText(SettingsActivity.this, "Account deleted!", Toast.LENGTH_SHORT).show();
                        }
                        //delete
                        if(i == 1){
                            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                        }
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickCamera();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        //intent to take image from camera, it will also be save to storage to get high quality
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic"); //title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text"); //description
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(SettingsActivity.this);
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    imageUri = result.getUri();
                    profileImageView.setImageURI(imageUri);
                }
            }
            else {
                Toast.makeText(this, "Error, try again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                finish();
            }
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        /*Check camera permission and return the result
         *In order to get high quality image we have to save the image into the external storage first
         *before inserting to image view, that's why storage permission will also be required*/
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private static boolean checkPassword(String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        boolean specialFlag = false;
        for(int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if(Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
        }
        int i = 0;
        StringBuilder sb = new StringBuilder(str);
        while (i != sb.length()){
            ch = sb.charAt(i);
            if (Character.isDigit(ch) || Character.isUpperCase(ch) || Character.isLowerCase(ch)){
                sb.deleteCharAt(i);
            }
            else {
                i++;
            }
        }
        // regex for special characters
        String regex = "[^a-zA-Z0-9]+";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(sb);
        if (m.matches()){
            specialFlag = true;
        }

        if(numberFlag && capitalFlag && lowerCaseFlag && specialFlag)
            return true;

        return false;
    }

    // update user info expect profile picture
    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullnameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("email", userEmailEditText.getText().toString());
        Prevalent.currentOnlineUser.setName(fullnameEditText.getText().toString());

        if((passwordEditText.getText().toString()).equals(confirmNewPasswordEditText.getText().toString()) && passwordEditText.getText().toString().trim().length() > 3 && checkPassword(passwordEditText.getText().toString())){
            String hashed = BCrypt.hashpw(passwordEditText.getText().toString(), BCrypt.gensalt());
            userMap.put("password", hashed);
            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            Toast.makeText(SettingsActivity.this, "Profile info updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
         if (passwordEditText.getText().toString().trim().length() == 0){
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
        else if (!checkPassword(passwordEditText.getText().toString())){
            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
            Toast.makeText(SettingsActivity.this, "Password must contain at least one lowercase letter, one capital letter, one number digit and one special character!", Toast.LENGTH_LONG).show();
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

    // check if the fields are empty
    private void userInfoSaved() {
        if (TextUtils.isEmpty(fullnameEditText.getText().toString())){
            Toast.makeText(this, "Name is mandatory!", Toast.LENGTH_SHORT).show();
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
                        userMap.put("image", myURL);
                        Prevalent.currentOnlineUser.setName(fullnameEditText.getText().toString());
                        Prevalent.currentOnlineUser.setImage(myURL);

                        if((passwordEditText.getText().toString()).equals(confirmNewPasswordEditText.getText().toString()) && passwordEditText.getText().toString().trim().length() > 3 && checkPassword(passwordEditText.getText().toString())){
                            String hashed = BCrypt.hashpw(passwordEditText.getText().toString(), BCrypt.gensalt());
                            userMap.put("password", hashed);
                            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
                            Picasso.get().load(myURL).into(profileImageView);
                            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                            Toast.makeText(SettingsActivity.this, "Profile info updated successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else if (passwordEditText.getText().toString().trim().length() == 0){
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
                        else if (!checkPassword(passwordEditText.getText().toString())){
                            ref.child(EncodeString(Prevalent.currentOnlineUser.getEmail())).updateChildren(userMap);
                            Toast.makeText(SettingsActivity.this, "Password must contain at least one lowercase letter, one capital letter, one number digit and one special character!", Toast.LENGTH_LONG).show();
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
    private void userInfoDisplay(CircleImageView profileImageView, EditText fullnameEditText, TextView userEmailEditText, EditText addressEditText) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(EncodeString(Prevalent.currentOnlineUser.getEmail()));

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                    }
                    if (dataSnapshot.child("address").exists()){
                        String address = dataSnapshot.child("address").getValue().toString();
                        addressEditText.setText(address);
                    }
                    String name = dataSnapshot.child("name").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    fullnameEditText.setText(name);
                    userEmailEditText.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}