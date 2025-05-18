package com.example.carpartsshop.ui.parts;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpartsshop.databinding.ActivityBillingBinding;

public class BillingActivity extends AppCompatActivity {
    private ActivityBillingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarBilling);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbarBilling.setNavigationOnClickListener(v -> finish());

        binding.buttonConfirmOrder.setOnClickListener(v -> {
            String name    = binding.editTextName.getText().toString().trim();
            String email   = binding.editTextEmail.getText().toString().trim();
            String phone   = binding.editTextPhone.getText().toString().trim();
            String address = binding.editTextAddress.getText().toString().trim();
            String message = binding.editTextMessage.getText().toString().trim();

            if (name.isEmpty()
                    || email.isEmpty()
                    || phone.isEmpty()
                    || address.isEmpty()) {
                Toast.makeText(this,
                        "Kérlek, töltsd ki az összes mezőt (név, email, telefonszám, lakcím)!",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this,
                        "Kérlek, érvényes email címet adj meg!",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }


            Intent intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra("customer_name",    name);
            intent.putExtra("customer_email",   email);
            intent.putExtra("customer_phone",   phone);
            intent.putExtra("customer_address", address);
            intent.putExtra("customer_message", message);
            startActivity(intent);
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
