package com.example.peakform;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.peakform.ui.entry.LogFragment;
import com.example.peakform.ui.user.LoginActivity;
import com.example.peakform.ui.dashboard.DashboardFragment;
import com.example.peakform.ui.feedback.FeedbackFragment;
import com.example.peakform.ui.settings.SettingsFragment;
import com.example.peakform.logic.settings.SettingsManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use SettingsManager to check if the user is registered.
        // This ensures the logic stays consistent with LoginActivity.
        SettingsManager sm = new SettingsManager(this);

        if (!sm.hasPinSet()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            boolean shouldOpenLog = getIntent().getBooleanExtra("OPEN_LOG_SCREEN", false);

            if (shouldOpenLog) {
                navView.setSelectedItemId(R.id.nav_log);
                loadFragment(new LogFragment());
            } else {
                loadFragment(new DashboardFragment());
            }
        }

        navView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            }
            else if (id == R.id.nav_log) {
                selectedFragment = new LogFragment();
            }
            else if (id == R.id.nav_feedback) {
                selectedFragment = new FeedbackFragment();
            }
            else if (id == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            return loadFragment(selectedFragment);
        });
    }

    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public void navigateToDashboard() {
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setSelectedItemId(R.id.nav_dashboard);
    }
}