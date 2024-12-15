package com.example.project1752.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.project1752.R;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 99;

    Button btnLogout, btnSnap;
    TextView txtName;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Find the ImageView for displaying the profile image.
        ImageView profileImageView = findViewById(R.id.imageview1);
        Button btnRemoveImage = findViewById(R.id.btnRemoveImage);
        loadProfileImage(profileImageView);

        // Retrieve the image path from SharedPreferences.
        SharedPreferences sharedPreferences = getSharedPreferences("ProfileImagePrefs", MODE_PRIVATE);
        String profileImagePath = sharedPreferences.getString("profileImagePath", null);

        if (profileImagePath != null) {
            File imageFile = new File(profileImagePath);
            if (imageFile.exists()) {
                // Load the image using Glide.
                Glide.with(this)
                        .load(imageFile)
                        .into(profileImageView);
            } else {
                Toast.makeText(this, "Image file not found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No profile image set!", Toast.LENGTH_SHORT).show();
        }

        btnRemoveImage.setOnClickListener(v -> {
            // Clear the saved image path
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("profileImagePath");
            editor.apply();

            // Reset the ImageView
            profileImageView.setImageResource(R.drawable.icon_account_circle); // Use a default placeholder
            Toast.makeText(this, "Profile picture removed!", Toast.LENGTH_SHORT).show();
        });

        // Edge-to-Edge setup
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        txtName = findViewById(R.id.txtName);
        txtName.setText(Preferences.getUsername(getBaseContext()));

        btnLogout = findViewById(R.id.btnLogout);
        btnSnap = findViewById(R.id.btncamera);
        imageView = findViewById(R.id.imageview1);

        // Logout Button
        btnLogout.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Camera Button
        Button btnSnap= findViewById(R.id.btncamera);
        btnSnap.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CameraActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        // Back Button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        // Load the profile image
        loadProfileImage(profileImageView);
    }
    private void loadProfileImage(ImageView profileImageView) {
        SharedPreferences sharedPreferences = getSharedPreferences("ProfileImagePrefs", MODE_PRIVATE);
        String profileImagePath = sharedPreferences.getString("profileImagePath", null);

        if (profileImagePath != null) {
            File imageFile = new File(profileImagePath);
            if (imageFile.exists()) {
                Glide.with(this)
                        .load(imageFile)
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.icon_account_circle); // Default placeholder
            }
        } else {
            profileImageView.setImageResource(R.drawable.icon_account_circle); // Default placeholder
        }
    }
}