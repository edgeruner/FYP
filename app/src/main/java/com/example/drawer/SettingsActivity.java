package com.example.drawer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Enable the back button in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get a reference to the switch_dark_mode switch
        Switch switchDarkMode = findViewById(R.id.switch_dark_mode);

        // Load the current state of dark mode from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkModeEnabled = preferences.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDarkModeEnabled);

        // Set an OnCheckedChangeListener to listen for switch state changes
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the new state of dark mode to SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("dark_mode", isChecked);
                editor.apply();

                // Apply the new theme based on the switch state
                if (isChecked) {
                    // Enable dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    // Disable dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                // Recreate the activity to apply the new theme
                recreate();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the back button in the action bar
        if (item.getItemId() == android.R.id.home) {
            // Finish the activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
