package com.example.project1752.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import com.example.project1752.R;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText txtEmailOrUsername, txtNewPassword, txtConfirmNewPassword;
    private Button btnResetPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

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
        String emailOrUsername = txtEmailOrUsername.getText().toString().trim();
        String newPassword = txtNewPassword.getText().toString().trim();
        String confirmNewPassword = txtConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(emailOrUsername)) {
            txtEmailOrUsername.setError("Email is required");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches()) {
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

        mAuth.sendPasswordResetEmail(emailOrUsername)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}