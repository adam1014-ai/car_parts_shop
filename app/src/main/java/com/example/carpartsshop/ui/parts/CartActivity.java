package com.example.carpartsshop.ui.parts;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carpartsshop.data.CartManager;
import com.example.carpartsshop.data.CartManager.CartItem;
import com.example.carpartsshop.databinding.ActivityCartBinding;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChange {
    private ActivityCartBinding binding;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarCart);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        List<CartItem> items = CartManager.getInstance().getCartItems();
        adapter = new CartAdapter(items, this);
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(adapter);

        updateTotal();

        binding.buttonCheckout.setOnClickListener(v -> {
            List<CartItem> currentItems = CartManager.getInstance().getCartItems();
            if (currentItems.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Üres a kosár")
                        .setMessage("A kosarad üres, kérlek adj hozzá termékeket a fizetés előtt.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                startActivity(new Intent(this, BillingActivity.class));
            }
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

    private void updateTotal() {
        double tot = CartManager.getInstance().getTotalPrice();
        binding.textTotal.setText(String.format("Összesen: %.2f €", tot));
    }

    @Override
    public void onItemRemoved() {
        updateTotal();
    }
}
