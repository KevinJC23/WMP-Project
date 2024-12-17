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

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText txtEmailOrUsername, txtNewPassword, txtConfirmNewPassword;
    private Button btnResetPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth secondaryAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

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

        txtEmailOrUsername = findViewById(R.id.txtEmailOrUsername);
        txtNewPassword = findViewById(R.id.txtNewPassword);
        txtConfirmNewPassword = findViewById(R.id.txtConfirmNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(view -> resetPassword());

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void resetPassword() {
        String email = txtEmailOrUsername.getText().toString().trim();
        String newPassword = txtNewPassword.getText().toString().trim();
        String confirmNewPassword = txtConfirmNewPassword.getText().toString().trim();

        // Input validations
        if (TextUtils.isEmpty(email)) {
            txtEmailOrUsername.setError("Email is required");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmailOrUsername.setError("Invalid email format");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            txtNewPassword.setError("New Password is required");
            return;
        }
        if (TextUtils.isEmpty(confirmNewPassword)) {
            txtConfirmNewPassword.setError("Confirm Password is required");
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            txtConfirmNewPassword.setError("Passwords do not match");
            return;
        }

        // Attempt to reset password
        secondaryAuth.signInWithEmailAndPassword(email, newPassword)
                .addOnCompleteListener(signInTask -> {
                    if (signInTask.isSuccessful()) {
                        // User successfully authenticated
                        FirebaseUser currentUser = secondaryAuth.getCurrentUser();
                        if (currentUser != null) {
                            currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(ResetPasswordActivity.this,
                                                    "Password reset successfully",
                                                    Toast.LENGTH_SHORT).show();

                                            // Sign out and return to login
                                            secondaryAuth.signOut();
                                            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(ResetPasswordActivity.this,
                                                    "Password reset failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Authentication failed
                        Toast.makeText(ResetPasswordActivity.this,
                                "Authentication failed. Check your email and current password.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
