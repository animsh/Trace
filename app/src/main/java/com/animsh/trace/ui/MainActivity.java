package com.animsh.trace.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.animsh.trace.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import static com.animsh.trace.util.Constants.fetchUserdata;
import static com.animsh.trace.util.Constants.fromLogin;
import static com.animsh.trace.util.Constants.restoreUserData;

public class MainActivity extends AppCompatActivity {

    TextView uName, uEmail;
    ImageView uImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //darkModeChecker();

        // Navigation Drawer
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView sideNavOpener = findViewById(R.id.imageMenu);

        sideNavOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        // to remove default black tint of navigation drawer icons
        navigationView.setItemIconTintList(null);

        View header = navigationView.getHeaderView(0);
        uName = header.findViewById(R.id.user_name);
        uEmail = header.findViewById(R.id.user_phone_number);
        ImageView editProfile = header.findViewById(R.id.edit_profile);
        uImage = header.findViewById(R.id.imageProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (fromLogin) {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loading_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            fetchUserdata(FirebaseAuth.getInstance(), MainActivity.this, dialog, uName, uEmail, uImage);
        } else {
            String[] data = restoreUserData(MainActivity.this);
            Log.e("FROM: ", data[0]);
            uName.setText(data[0]);
            uEmail.setText(data[1]);
            uImage.setImageResource(Integer.parseInt(data[4]));
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // get id of clicked item
                int id = item.getItemId();

                // set action to selected item
                if (id == R.id.menuSetting) {
                    Toast.makeText(MainActivity.this, "Open Setting Activity", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.menuHelp) {
                    Toast.makeText(MainActivity.this, "Open HElp & Feedback Activity", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        // Navigation Drawer

        // Bottom Navigation Bar

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Bottom Navigation Bar
    }

    private void darkModeChecker() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        if (isDarkModeOn) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
        } else {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fromLogin) {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loading_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            fetchUserdata(FirebaseAuth.getInstance(), MainActivity.this, dialog, uName, uEmail, uImage);
        } else {
            String[] data = restoreUserData(MainActivity.this);
            Log.e("FROM: ", data[0]);
            uName.setText(data[0]);
            uEmail.setText(data[1]);
            uImage.setImageResource(Integer.parseInt(data[4]));
        }

    }
}