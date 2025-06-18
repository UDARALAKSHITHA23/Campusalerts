package com.example.campusalerts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {



    private TextView profileName, profileEmail;
    private DatabaseReference reference;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // highlight current item
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_notifications:
                    startActivity(new Intent(ProfileActivity.this, NotificationActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_profile:
                    return true; // Already here
            }
            return false;
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get username passed from previous activity
        username = getIntent().getStringExtra("username");

        // Initialize UI components
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        TextView tvLevel = findViewById(R.id.tvLevel);
        TextView tvYear = findViewById(R.id.tvYear);
        TextView tvAge = findViewById(R.id.tvAge);
        TextView tvHome = findViewById(R.id.tvHome);
        LinearLayout devInfoSection = findViewById(R.id.devInfoSection);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btndelete);

        // Static sample values
        tvLevel.setText("Mid level");
        tvYear.setText("4th Year");
        tvAge.setText("22y");
        tvHome.setText("Kegalle");

        // Developer Info section click → go to DevInfro activity
        devInfoSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, DevInfro.class);
                startActivity(intent);
            }
        });
        // Edit user data
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditUserActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        // Delete user from Firebase
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username != null && !username.isEmpty()) {
                    DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("users").child(username);
                    deleteRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProfileActivity.this, SignupActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Invalid user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load user data from Firebase
        loadUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        if (username == null || username.isEmpty()) {
            profileName.setText("No user");
            profileEmail.setText("No user");
            return;
        }

        reference = FirebaseDatabase.getInstance().getReference("users").child(username);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    profileName.setText(name != null ? name : "No name");
                    profileEmail.setText(email != null ? email : "No email");
                } else {
                    profileName.setText("User not found");
                    profileEmail.setText("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                profileName.setText("Error");
                profileEmail.setText("Error");
            }
        });
    }
}
