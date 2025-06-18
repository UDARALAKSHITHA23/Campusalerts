package com.example.campusalerts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class activity_news extends AppCompatActivity {

    private LinearLayout newsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news);

        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find your views
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        newsContainer = findViewById(R.id.newsContainer); // This must exist in activity_news.xml

        // Set default content
        loadNews("Academic");

        // Bottom nav logic
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intent = new Intent(activity_news.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.nav_Sports:
                    loadNews("Sports");
                    return true;
                case R.id.nav_academic:
                    loadNews("Academic");
                    return true;
                case R.id.nav_events:
                    loadNews("Events");
                    return true;
            }
            return false;
        });

    }

    private void loadNews(String type) {
        newsContainer.removeAllViews(); // Clear old views

        // Sample text. You can later replace this with a list of cards or Firebase data
        TextView newsText = new TextView(this);
        newsText.setText(type + " news content will appear here.");
        newsText.setTextSize(18f);
        newsText.setPadding(20, 20, 20, 20);
        newsContainer.addView(newsText);
    }
}
