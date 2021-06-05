package com.shop.shopActivities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shop.R;
import com.shop.adminActivities.AdminHomeActivity;
import com.shop.adminActivities.AdminEditProductsActivity;
import com.shop.models.Products;
import com.shop.helpers.Prevalent;
import com.shop.userActivities.UserOrdersActivity;
import com.shop.viewholders.ProductViewHolder;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private Button homeSearch;
    private String type = "";
    private Spinner filtersSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // get the intent to see if the logged user is an admin
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = bundle.get("Admin").toString();
        }

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Paper.init(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        homeSearch = findViewById(R.id.home_search);
        homeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SearchProductsActivity.class);
                if(type.equals("Admin")){
                    intent.putExtra("type", "admin");
                }
                else {
                    intent.putExtra("type", "user");
                }
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton fab_back = findViewById(R.id.fab_back);
        if (type.equals("Admin")) {
            fab.setVisibility(View.GONE);
            fab_back.setVisibility(View.VISIBLE);
        }

        // click listener for the cart floating button for users
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        // click listener for the back floating button for admin
        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AdminHomeActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (type.equals("Admin")) {
            navigationView.setVisibility(View.GONE);
        }
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        if (!type.equals("Admin")) {
            FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
            try {
                String email = account.getEmail();
                if (account.getEmail() != null && Prevalent.currentOnlineUser.getEmail() != email) {
                    userNameTextView.setText(Prevalent.currentOnlineUser.getName());
                    Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
                } else {
                    String name = account.getDisplayName();
                    Uri picture = account.getPhotoUrl();
                    userNameTextView.setText(name);
                    Picasso.get().load(picture).placeholder(R.drawable.profile).into(profileImageView);
                }
            }
            catch (Exception e){
            }

        }

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        filtersSpinner = findViewById(R.id.filter_options);
        ArrayAdapter<String> filtersAdapter = new ArrayAdapter<String>(HomeActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.filter_options));
        filtersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtersSpinner.setAdapter(filtersAdapter);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // filters option
        filtersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                layoutManager.setReverseLayout(false);
                if(i == 0){
                    FirebaseRecyclerOptions<Products> options =
                            new FirebaseRecyclerOptions.Builder<Products>()
                                    .setQuery(ProductsRef, Products.class)
                                    .build();
                    recyclerBuild(options);

                }
                else if (i == 1){
                    FirebaseRecyclerOptions<Products> options =
                            new FirebaseRecyclerOptions.Builder<Products>()
                                    .setQuery(ProductsRef.orderByChild("discountPrice"), Products.class)
                                    .build();
                    recyclerBuild(options);
                }
                else if(i == 2){
                    FirebaseRecyclerOptions<Products> options =
                            new FirebaseRecyclerOptions.Builder<Products>()
                                    .setQuery(ProductsRef.orderByChild("discountPrice"), Products.class)
                                    .build();
                    recyclerBuild(options);
                    layoutManager.setReverseLayout(true);
                }
                else if(i == 3){
                    FirebaseRecyclerOptions<Products> options =
                            new FirebaseRecyclerOptions.Builder<Products>()
                                    .setQuery(ProductsRef.orderByChild("pname"), Products.class)
                                    .build();
                    recyclerBuild(options);
                }
                else if(i == 4) {
                    FirebaseRecyclerOptions<Products> options =
                            new FirebaseRecyclerOptions.Builder<Products>()
                                    .setQuery(ProductsRef.orderByChild("pname"), Products.class)
                                    .build();
                    recyclerBuild(options);
                    layoutManager.setReverseLayout(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                FirebaseRecyclerOptions<Products> options =
                        new FirebaseRecyclerOptions.Builder<Products>()
                                .setQuery(ProductsRef, Products.class)
                                .build();
                recyclerBuild(options);
            }
        });

    }

    // build the RecyclerView for filtered products
    public void recyclerBuild(FirebaseRecyclerOptions<Products> options) {
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        if(model.getDiscount() == 0){
                            holder.txtProductPrice.setText("Price: " + model.getPrice() + " lei");
                        }
                        else{
                            String txt = "Price: " + model.getPrice() + " lei " + model.getDiscountPrice() + " lei";
                            holder.txtProductPrice.setText(txt, TextView.BufferType.SPANNABLE);
                            Spannable spannable = (Spannable) holder.txtProductPrice.getText();
                            spannable.setSpan(new StrikethroughSpan(), 7, txt.length() - (model.getDiscountPrice() + " lei").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#e71826")), 7, txt.length() - (model.getDiscountPrice() + " lei").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (type.equals("Admin")) {
                                    Intent intent = new Intent(HomeActivity.this, AdminEditProductsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                else {
                                    Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if(type.equals("Admin")){
            Intent intent = new Intent(HomeActivity.this, AdminHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    // get the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handled navigation view item clicks here
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_search){
            Intent intent = new Intent(HomeActivity.this, SearchProductsActivity.class);
            if(type.equals("Admin")){
                intent.putExtra("type", "admin");
            }
            else {
                intent.putExtra("type", "user");
            }
            startActivity(intent);
        }
        else if (id == R.id.nav_orders) {
            Intent intent = new Intent(HomeActivity.this, UserOrdersActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_categories) {
            Intent intent = new Intent(HomeActivity.this, CategoriesActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_settings) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (!Prevalent.currentOnlineUser.getEmail().equals(email)) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(HomeActivity.this, SettingsGoogleActivity.class);
                startActivity(intent);
            }
        }
        else if (id == R.id.nav_logout) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.revokeAccess();

            Paper.book().destroy();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_contact){
            Intent intent = new Intent(HomeActivity.this, ContactActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_about){
            Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_whislist){
            Intent intent = new Intent(HomeActivity.this, WhislistActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}