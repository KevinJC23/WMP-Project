package com.example.project1752.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import com.example.project1752.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    private EditText txtEmail, txtPassword, txtConfirmPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseAuth secondaryAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.getInstance("project175-2");
        } catch (IllegalStateException e) {
            secondaryApp = FirebaseApp.initializeApp(this, new FirebaseOptions.Builder()
                    .setApplicationId("1:938964782064:android:7cf2a5da53ead3a68eab35")
                    .setApiKey("AIzaSyBNVII9y1jCLVvn5qR0TT4KSp553ZaCr48")
                    .setDatabaseUrl("https://your-secondary-project.firebaseio.com")
                    .setProjectId("game-store-project-cad4e")
                    .build(), "project175-2");
        }

        secondaryAuth = FirebaseAuth.getInstance(secondaryApp);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> registerUser());

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Password is required");
            return;
        }
        if (!password.equals(confirmPassword)) {
            txtConfirmPassword.setError("Passwords do not match");
            return;
        }

        secondaryAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = secondaryAuth.getCurrentUser();
                        Preferences.setEmail(getBaseContext(), email);  // Store email in SharedPreferences
                        Preferences.setUsername(getBaseContext(), email);  // Store username in SharedPreferences if needed
                        Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
