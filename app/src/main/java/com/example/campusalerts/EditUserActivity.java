package com.example.campusalerts;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class EditUserActivity extends AppCompatActivity {

    private EditText etUsername, etName, etEmail, etPassword;
    Button btnSave;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        etUsername = findViewById(R.id.etUsername);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnSave);

        // Get passed username
        String username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "No user data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etUsername.setText(username);

        reference = FirebaseDatabase.getInstance().getReference("users").child(username);

        // Load data from Firebase
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etName.setText(snapshot.child("name").getValue(String.class));
                    etEmail.setText(snapshot.child("email").getValue(String.class));
                    etPassword.setText(snapshot.child("password").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditUserActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        // Save changes to Firebase
        btnSave.setOnClickListener(v -> {
            reference.child("name").setValue(etName.getText().toString());
            reference.child("email").setValue(etEmail.getText().toString());
            reference.child("password").setValue(etPassword.getText().toString());
            Toast.makeText(EditUserActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close edit screen and go back
        });
    }
}
