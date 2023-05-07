package com.example.nicode.Activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicode.Fragments.StatisticFragment;
import com.example.nicode.Fragments.HelpFragment;
import com.example.nicode.Fragments.NotificationFragment;
import com.example.nicode.Fragments.PrivacyandpolicyFragment;
import com.example.nicode.Fragments.AccountFragment;

import com.example.nicode.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.nicode.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    FirebaseAuth FAuth;

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_community, R.id.navigation_health, R.id.navigation_gift)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        preferenceManager = new PreferenceManager(getApplicationContext());
        getToken();
        updateNavHeader();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore Fdatabase = FirebaseFirestore.getInstance();
        DocumentReference documentReference = Fdatabase.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this,"Token unable to update",Toast.LENGTH_LONG).show());

    }

    private void updateNavHeader(){
        View headerView = navigationView.getHeaderView(0);
        ImageView navProfile = headerView.findViewById(R.id.nav_Profile);
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        navProfile.setImageBitmap(bitmap);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        if(item.getItemId()==R.id.navigation_account){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment_content_main,new AccountFragment());
            fragmentTransaction.commit();
        }

        if(item.getItemId()==R.id.navigation_nofitication){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment_content_main,new NotificationFragment());
            fragmentTransaction.commit();
        }

        if(item.getItemId()==R.id.navigation_privacyandpolicy){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment_content_main,new PrivacyandpolicyFragment());
            fragmentTransaction.commit();
        }

        if(item.getItemId()==R.id.navigation_statistic){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment_content_main,new StatisticFragment());
            fragmentTransaction.commit();
        }

        if(item.getItemId()==R.id.navigation_help){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment_content_main, new HelpFragment());
            fragmentTransaction.commit();
        }

        return false;
    }
}
