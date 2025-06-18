package com.example.campusalerts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * SignupActivity handles user registration for the Campus Alerts application.
 * Features include input validation, duplicate checking, and Firebase database integration.
 */
public class SignupActivity extends AppCompatActivity {

    // UI Components
    private EditText signupName, signupUsername, signupEmail, signupPassword;
    private TextView loginRedirectText;
    private Button signupButton;

    // Firebase Database Components
    private FirebaseDatabase database;
    private DatabaseReference reference;

    /**
     * Called when the activity is first created.
     * Sets up the layout and initializes all components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeViews();
        setupClickListeners();
    }

    /**
     * Initializes all UI components by finding them from the layout.
     * Links EditText fields, buttons, and text views to their respective variables.
     */
    private void initializeViews() {
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);
    }

    /**
     * Sets up click listeners for interactive UI elements.
     * Handles signup button clicks and login redirect text clicks.
     */
    private void setupClickListeners() {
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSignup();
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToLogin();
            }
        });
    }

    /**
     * Main signup handler that processes user registration.
     * Gets input values, validates them, and initiates the registration process.
     */
    private void handleSignup() {
        // Get input values and trim whitespace
        String name = signupName.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String username = signupUsername.getText().toString().trim();
        String password = signupPassword.getText().toString();

        // Validate input
        if (!validateInput(name, email, username, password)) {
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Check if username or email already exists
        checkUserExists(name, email, username, password);
    }

    /**
     * Validates all user input fields according to specified criteria.
     * @param name User's full name
     * @param email User's email address
     * @param username User's chosen username
     * @param password User's chosen password
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInput(String name, String email, String username, String password) {
        // Check if fields are empty
        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields");
            return false;
        }

        // Validate name - must be at least 2 characters
        if (name.length() < 2) {
            showToast("Name must be at least 2 characters long");
            return false;
        }

        // Validate email format using Android's built-in pattern matcher
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address");
            return false;
        }

        // Validate username - must be at least 3 characters
        if (username.length() < 3) {
            showToast("Username must be at least 3 characters long");
            return false;
        }

        // Validate username format - only letters, numbers, and underscores allowed
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showToast("Username can only contain letters, numbers, and underscores");
            return false;
        }

        // Validate password - must be at least 6 characters
        if (password.length() < 6) {
            showToast("Password must be at least 6 characters long");
            return false;
        }

        return true;
    }

    /**
     * Checks if the provided username already exists in the database.
     * If username is unique, proceeds to check email uniqueness.
     * @param name User's full name
     * @param email User's email address
     * @param username User's chosen username
     * @param password User's chosen password
     */
    private void checkUserExists(String name, String email, String username, String password) {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        // Check if username already exists
        reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Username already taken
                    setLoadingState(false);
                    showToast("Username already exists. Please choose a different one.");
                } else {
                    // Username is available, check if email already exists
                    checkEmailExists(name, email, username, password);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                setLoadingState(false);
                showToast("Database error: " + error.getMessage());
            }
        });
    }

    /**
     * Checks if the provided email is already registered in the database.
     * If email is unique, proceeds to create the new user account.
     * @param name User's full name
     * @param email User's email address
     * @param username User's chosen username
     * @param password User's chosen password
     */
    private void checkEmailExists(String name, String email, String username, String password) {
        reference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Email already registered
                            setLoadingState(false);
                            showToast("Email already registered. Please use a different email.");
                        } else {
                            // Email is available, create new user
                            createUser(name, email, username, password);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        setLoadingState(false);
                        showToast("Database error: " + error.getMessage());
                    }
                });
    }

    /**
     * Creates a new user account in the Firebase database.
     * Uses HelperClass to structure user data and saves it to the database.
     * @param name User's full name
     * @param email User's email address
     * @param username User's chosen username
     * @param password User's chosen password
     */
    private void createUser(String name, String email, String username, String password) {
        // Create user object using HelperClass
        HelperClass helperClass = new HelperClass(name, email, username, password);

        // Save user data to Firebase database
        reference.child(username).setValue(helperClass)
                .addOnSuccessListener(aVoid -> {
                    // Registration successful
                    setLoadingState(false);
                    showToast("Account created successfully!");

                    // Clear form fields
                    clearForm();

                    // Navigate to login screen
                    navigateToLogin();
                })
                .addOnFailureListener(e -> {
                    // Registration failed
                    setLoadingState(false);
                    showToast("Registration failed: " + e.getMessage());
                });
    }

    /**
     * Controls the loading state of the signup process.
     * Disables/enables the signup button and changes its text to show progress.
     * @param isLoading true to show loading state, false to hide it
     */
    private void setLoadingState(boolean isLoading) {
        signupButton.setEnabled(!isLoading);
        signupButton.setText(isLoading ? "Creating Account..." : "Sign Up");
    }

    /**
     * Clears all input fields in the signup form.
     * Used after successful registration to reset the form.
     */
    private void clearForm() {
        signupName.setText("");
        signupEmail.setText("");
        signupUsername.setText("");
        signupPassword.setText("");
    }

    /**
     * Navigates to the LoginActivity and closes the current activity.
     * Used both for the redirect link and after successful registration.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Displays a short toast message to the user.
     * Centralizes toast creation for consistent messaging.
     * @param message The message to display to the user
     */
    private void showToast(String message) {
        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the activity is being destroyed.
     * Clean up any resources or listeners if needed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any listeners if needed
    }
}