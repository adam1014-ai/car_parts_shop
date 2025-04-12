package com.example.carpartsshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpartsshop.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Users");

        binding.registerButton.setOnClickListener(v -> handleRegistration());

        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        binding.formCard.startAnimation(slideIn);

    }

    private void handleRegistration() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();
        String firstName = binding.firstNameEditText.getText().toString().trim();
        String lastName = binding.lastNameEditText.getText().toString().trim();
        String address = binding.addressEditText.getText().toString().trim();

        if (!validateInputs(email, password, confirmPassword, firstName, lastName, address)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    String userId = firebaseUser.getUid();
                    User user = new User(firstName, lastName, address, email);

                    dbRef.child(userId).setValue(user)
                            .addOnSuccessListener(unused -> {
                                showToast("Registration successful!");
                                navigateToLogin();
                            })
                            .addOnFailureListener(e -> showToast("Failed to save user data"));
                })
                .addOnFailureListener(e -> showToast("Registration failed: " + e.getMessage()));
    }

    private boolean validateInputs(String email, String password, String confirmPassword,
                                   String firstName, String lastName, String address) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || firstName.isEmpty() || lastName.isEmpty() || address.isEmpty()) {
            showToast("Please fill in all fields");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match");
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 1000);
    }
}