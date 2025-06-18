package com.example.campusalerts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * LoginActivity handles user authentication for the Campus Alerts application.
 */
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private EditText loginUsername, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;

    // Firebase Database Reference
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignup();
            }
        });
    }

    private void handleLogin() {
        String username = loginUsername.getText().toString().trim();
        String password = loginPassword.getText().toString();

        if (!validateInput(username, password)) {
            return;
        }

        setLoadingState(true);
        authenticateUser(username, password);
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showToast("Please enter all fields");
            return false;
        }

        if (username.length() < 3) {
            showToast("Username must be at least 3 characters long");
            return false;
        }

        if (password.length() < 6) {
            showToast("Password must be at least 6 characters long");
            return false;
        }

        return true;
    }

    private void authenticateUser(String username, String password) {
        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setLoadingState(false);

                if (snapshot.exists()) {
                    verifyPassword(snapshot, password);
                } else {
                    showToast("User not found. Please check your username or sign up.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setLoadingState(false);
                showToast("Database error: " + error.getMessage());
            }
        });
    }

    private void verifyPassword(DataSnapshot snapshot, String enteredPassword) {
        String dbPassword = snapshot.child("password").getValue(String.class);

        if (dbPassword != null && dbPassword.equals(enteredPassword)) {
            handleSuccessfulLogin(snapshot);
        } else {
            showToast("Incorrect password. Please try again.");
        }
    }

    private void handleSuccessfulLogin(DataSnapshot userSnapshot) {
        String name = userSnapshot.child("name").getValue(String.class);
        String username = userSnapshot.getKey(); // Get the key as the username

        showToast("Welcome back, " + (name != null ? name : username) + "!");

        navigateToDashboard(username);
    }

    private void navigateToDashboard(String username) {
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        intent.putExtra("username", username);
        startActivity(intent); // ← this should start ProfileActivity
        finish(); // ← this finishes LoginActivity
    }

    private void navigateToSignup() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish(); // Prevent going back to login
    }

    private void setLoadingState(boolean isLoading) {
        loginButton.setEnabled(!isLoading);
        loginButton.setText(isLoading ? "Logging in..." : "Login");
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup logic if needed
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Optional: Add exit confirmation dialog
    }
}
