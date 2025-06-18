package com.example.campusalerts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class wellcomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("is_first_time", true);

        if (!isFirstTime) {
            // Skip to LoginActivity if not first time
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_wellcome_page);

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            // Save that user has already seen the welcome screen
            prefs.edit().putBoolean("is_first_time", false).apply();

            // Go to LoginActivity
            startActivity(new Intent(wellcomePage.this, LoginActivity.class));
            finish();
        });
    }
}
