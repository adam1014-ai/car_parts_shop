package com.example.carpartsshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpartsshop.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setSupportActionBar(binding.toolbarProfile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.profile));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            firestore.collection("Users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            User user = doc.toObject(User.class);
                            if (user != null) {
                                binding.textViewFirstName.setText(user.getFirstName());
                                binding.textViewLastName.setText(user.getLastName());
                                binding.textViewAddress.setText(user.getAddress());
                                binding.textViewEmail.setText(user.getEmail());
                            }
                        } else {
                            Toast.makeText(this, R.string.user_data_not_found, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, getString(R.string.error_loading_data) + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        } else {
            Toast.makeText(this, R.string.no_user_signed_in, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        binding.buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, R.string.logged_out_successfully, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
