package com.example.campusalerts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

public class DevInfro extends AppCompatActivity {

    TextView devName, devId, devStatement, devVersion;
    ShapeableImageView devImage;
    Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_infro);

        // Connect with XML views
        devName = findViewById(R.id.devName);
        devId = findViewById(R.id.devId);
        devStatement = findViewById(R.id.devStatement);
        devVersion = findViewById(R.id.devVersion);
        devImage = findViewById(R.id.devImage);
        btnExit = findViewById(R.id.btnExit);

        // Set values
        devName.setText("Name:\nB.A.U.L. Bamunusinghe");
        devId.setText("Student No.:\n2020700857");
        devStatement.setText("I am a dedicated and enthusiastic student...");
        devVersion.setText("Release Version:\nversion 1.0");

        // Load image with Glide
        Glide.with(this)
                .load(R.drawable.profilmg)
                .circleCrop()
                .into(devImage);

        // Exit to ProfileActivity
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevInfro.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
