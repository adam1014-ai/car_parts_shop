package com.example.carpartsshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpartsshop.databinding.ActivityLoginBinding;
import com.example.carpartsshop.ui.parts.ProductListActivity;  // importáld be
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, ProductListActivity.class));
            finish();
            return;
        }

        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Kérlek töltsd ki az össszes mezőt", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, ProductListActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Sikertelen bejelentkezés: hibás jelszó vagy email!", Toast.LENGTH_LONG).show();
                }
            });
        });

        binding.buttonRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}