package com.example.project1752.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1752.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class LoginActivity extends AppCompatActivity {
    private EditText txtEmail, txtPassword;
    private Button btnLogin, btnRegister;
    private TextView btnResetPassword;
    private FirebaseAuth mAuth; // Default FirebaseAuth
    private FirebaseAuth secondaryAuth; // Secondary FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        clearSessionData();

        // Initialize FirebaseAuth instances
        mAuth = FirebaseAuth.getInstance(); // Default Firebase

        // Initialize secondary FirebaseApp if using different project
        FirebaseApp secondaryApp = FirebaseApp.initializeApp(this, new FirebaseOptions.Builder()
                .setApplicationId("1:938964782064:android:7cf2a5da53ead3a68eab35") // Replace with your secondary Firebase App's ID
                .setApiKey("AIzaSyBNVII9y1jCLVvn5qR0TT4KSp553ZaCr48") // Replace with your secondary API key
                .setDatabaseUrl("https://your-secondary-project.firebaseio.com") // Optional, if using Realtime Database
                .setProjectId("game-store-project-cad4e") // Replace with your secondary project ID
                .build(), "project175-2"); // Provide the name for the secondary app

        secondaryAuth = FirebaseAuth.getInstance(secondaryApp); // Initialize secondary FirebaseAuth instance

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        btnLogin.setOnClickListener(view -> loginUser());

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        btnResetPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void clearSessionData() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private void loginUser() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Password is required");
            return;
        }

        // Use the secondary FirebaseAuth instance for login
        secondaryAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = secondaryAuth.getCurrentUser();
                        Preferences.setEmail(getBaseContext(), email); // Store email in SharedPreferences
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
