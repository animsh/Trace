package com.animsh.trace;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // get id of clicked item
                int id = item.getItemId();

                // set action to selected item
                if (id == R.id.menuEncryptedFiles) {
                    Toast.makeText(MainActivity.this, "Open encrypted files directory", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.menuDecryptedFiles) {
                    Toast.makeText(MainActivity.this, "Open decrypted files directory", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.menuSetting) {
                    Toast.makeText(MainActivity.this, "Open Setting Activity", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.menuAbout) {
                    Toast.makeText(MainActivity.this, "Open About Activity", Toast.LENGTH_SHORT).show();
                    return true;
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


}