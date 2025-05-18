package com.example.carpartsshop.ui.parts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carpartsshop.NotificationHelper;
import com.example.carpartsshop.ProfileActivity;
import com.example.carpartsshop.R;
import com.example.carpartsshop.data.Part;
import com.example.carpartsshop.databinding.ActivityProductListBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity implements PartAdapter.OnClickListener {
    private static final int REQ_FILTER = 100;

    private ActivityProductListBinding binding;
    private PartAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String currentBrand;
    private int    currentYear;
    private String currentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationHelper.createNotificationChannel(this);
        binding = ActivityProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent in = getIntent();
        currentBrand = in.getStringExtra(FilterActivity.EXTRA_BRAND);
        currentYear  = in.getIntExtra(FilterActivity.EXTRA_YEAR, 0);
        currentModel = in.getStringExtra(FilterActivity.EXTRA_MODEL);

        loadProducts(currentBrand, currentYear, currentModel);
    }

    private void loadProducts(@Nullable String brand, int year, @Nullable String model) {
        Query q = db.collection("parts");
        if (brand != null && !brand.isEmpty()) {
            q = q.whereEqualTo("brand", brand);
        }
        if (year != 0) {
            q = q.whereEqualTo("year", year);
        }
        if (model != null && !model.isEmpty()) {
            q = q.whereEqualTo("model", model);
        }

        q.get()
                .addOnSuccessListener(snaps -> {
                    List<Part> parts = new ArrayList<>();
                    for (var doc : snaps) {
                        Part p = doc.toObject(Part.class);
                        p.setId(doc.getId());
                        parts.add(p);
                    }
                    adapter = new PartAdapter(parts, this);
                    binding.recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                });
    }

    @Override
    public void onPartClick(Part part) {
        Intent i = new Intent(this, ProductDetailActivity.class);
        i.putExtra(ProductDetailActivity.EXTRA_PART_ID, part.getId());
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            Intent filterIntent = new Intent(this, FilterActivity.class);
            filterIntent.putExtra(FilterActivity.EXTRA_BRAND, currentBrand);
            filterIntent.putExtra(FilterActivity.EXTRA_YEAR,  currentYear);
            filterIntent.putExtra(FilterActivity.EXTRA_MODEL, currentModel);
            startActivityForResult(filterIntent, REQ_FILTER);
            return true;
        } else if (id == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_FILTER && resultCode == RESULT_OK && data != null) {
            currentBrand = data.getStringExtra(FilterActivity.EXTRA_BRAND);
            currentYear  = data.getIntExtra(FilterActivity.EXTRA_YEAR, 0);
            currentModel = data.getStringExtra(FilterActivity.EXTRA_MODEL);
            loadProducts(currentBrand, currentYear, currentModel);
        }
    }
}
