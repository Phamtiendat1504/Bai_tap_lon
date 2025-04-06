package com.example.thue_tro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private String currentUsername;
    private String userRole;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Lấy username và role từ Intent
        currentUsername = getIntent().getStringExtra("username");
        userRole = getIntent().getStringExtra("role");
        Log.d("HomeActivity", "User role: " + userRole); // Log để kiểm tra role

        // Ánh xạ BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Thiết lập sự kiện chọn mục trong BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.navigation_post) {
                selectedFragment = new PostFragment();
            } else if (itemId == R.id.navigation_account) {
                selectedFragment = new AccountFragment();
            }
            if (selectedFragment != null) {
                Bundle bundle = new Bundle();
                bundle.putString("username", currentUsername);
                bundle.putString("role", userRole);
                selectedFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
                        .commit();
            }
            return true;
        });

        // Xử lý Intent khi khởi động
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent); // Xử lý Intent mới
    }

    private void handleIntent(Intent intent) {
        String fragmentToShow = intent.getStringExtra("fragment_to_show");
        Fragment selectedFragment = null;
        if ("home".equals(fragmentToShow)) {
            selectedFragment = new HomeFragment();
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        } else if ("search".equals(fragmentToShow)) {
            selectedFragment = new SearchFragment();
            bottomNavigationView.setSelectedItemId(R.id.navigation_search);
        } else {
            selectedFragment = new HomeFragment();
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
        if (selectedFragment != null) {
            Bundle bundle = new Bundle();
            bundle.putString("username", currentUsername);
            bundle.putString("role", userRole);
            selectedFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public String getUserRole() {
        return userRole;
    }
}