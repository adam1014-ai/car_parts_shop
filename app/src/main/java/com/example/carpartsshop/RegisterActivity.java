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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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

                    if (firebaseUser != null) {
                        String userId = firebaseUser.getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("firstName", firstName);
                        user.put("lastName", lastName);
                        user.put("address", address);
                        user.put("email", email);

                        firestore.collection("Users")
                                .document(userId)
                                .set(user)
                                .addOnSuccessListener(unused -> {
                                    showToast("Sikeres regisztráció!");
                                    navigateToLogin();
                                })
                                .addOnFailureListener(e -> showToast("Nem sikerült elmenteni a felhasználót: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> showToast("Sikertelen regisztráció" + e.getMessage()));
    }

    private boolean validateInputs(String email, String password, String confirmPassword,
                                   String firstName, String lastName, String address) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || firstName.isEmpty() || lastName.isEmpty() || address.isEmpty()) {
            showToast("Kérlek töltsd ki az összes mezőt");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Nem egyezik a jelszó");
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