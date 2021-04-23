package com.shop.shopActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shop.R;
import com.shop.adminActivities.AdminHomeActivity;
import com.shop.models.Users;
import com.shop.prevalent.Prevalent;
import java.util.HashMap;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton, loginButton;
    private ProgressDialog loadingBar;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton = findViewById(R.id.main_join_now_btn);
        loginButton = findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);
        signInButton = findViewById(R.id.main_google_btn);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        Paper.init(this);

        // click listener to go to the login activity
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // click listener to go to the register activity
        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String UserEmailKey = Paper.book().read(Prevalent.UserEmailKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        // check if the user is already logged in
        if (UserEmailKey != "" && UserPasswordKey != ""){
            if (!TextUtils.isEmpty(UserEmailKey) && !TextUtils.isEmpty(UserPasswordKey)){
                AllowAccess(UserEmailKey, UserPasswordKey);

                loadingBar.setTitle("Already logged in");
                loadingBar.setMessage("Please wait, we are checking the credentials.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    //implemented SIGN IN option with Google
    private void signInWithGoogle() {
        Intent signInClient = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInClient, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(MainActivity.this, "Sign In failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    // sign in with Google account
    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        if(acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        loadingBar.setTitle("Login with Google");
                        loadingBar.setMessage("Please wait, we are checking the credentials.");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        String email = account.getEmail();
                        String name = account.getDisplayName();

                        final DatabaseReference RootRef;
                        RootRef = FirebaseDatabase.getInstance().getReference();

                        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("Users").child(EncodeString(email)).exists()) {
                                    Toast.makeText(MainActivity.this, "There is already an account with this email", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                    mGoogleSignInClient.signOut();
                                    signInWithGoogle();
                                } else if (dataSnapshot.child("Admins").child(EncodeString(email)).exists()) {
                                    Toast.makeText(MainActivity.this, "There is already an admin account with this email", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                    mGoogleSignInClient.signOut();
                                    signInWithGoogle();
                                } else {
                                    Toast.makeText(MainActivity.this, "Signed In successfully!", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();

                                    Users userData = new Users();
                                    userData.setEmail(email);
                                    userData.setName(name);
                                    userData.setImage(account.getPhotoUrl().toString());
                                    userData.setAddress(" ");
                                    userData.setPassword("-");
                                    Prevalent.currentOnlineUser = userData;

                                    HashMap<String, Object> userdataMap = new HashMap<>();
                                    userdataMap.put("email", email);
                                    userdataMap.put("name", name);
                                    userdataMap.put("image", account.getPhotoUrl().toString());
                                    FirebaseDatabase.getInstance().getReference().child("Google Users").child(EncodeString(userData.getEmail())).updateChildren(userdataMap);

                                    Paper.book().write(Prevalent.UserEmailKey, email);
                                    Paper.book().write(Prevalent.UserPasswordKey, "not needed");

                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                    } else {
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // login from Remember me option
    private void AllowAccess(final String email, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(EncodeString(email)).exists()){
                    Users userData = dataSnapshot.child("Users").child(EncodeString(email)).getValue(Users.class);

                    if (userData.getEmail().equals(email)){
                        if (userData.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "Success login!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = userData;
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                }
                else if (dataSnapshot.child("Admins").child(EncodeString(email)).exists()){
                        Users userData = dataSnapshot.child("Admins").child(EncodeString(email)).getValue(Users.class);
                        if (userData.getEmail().equals(email)){
                            if (userData.getPassword().equals(password)){
                                Toast.makeText(MainActivity.this, "Success login, admin!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    }
                else if (dataSnapshot.child("Google Users").child(EncodeString(email)).exists()){
                    Users userData = dataSnapshot.child("Google Users").child(EncodeString(email)).getValue(Users.class);
                    if (userData.getEmail().equals(email)) {
                        Toast.makeText(MainActivity.this, "Success login!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        Prevalent.currentOnlineUser = userData;
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Google session expired!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
                else {
                    Toast.makeText(MainActivity.this, "Account with this email doesn't exist!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}