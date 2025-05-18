package com.example.carpartsshop.ui.parts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.carpartsshop.data.CartManager;
import com.example.carpartsshop.data.Part;
import com.example.carpartsshop.databinding.ActivityProductDetailBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    public static final String EXTRA_PART_ID = "extra_part_id";
    private static final int REQ_STORAGE = 101;

    private ActivityProductDetailBinding binding;
    private FirebaseFirestore db;
    private String partId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        setSupportActionBar(binding.toolbarDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbarDetail.setNavigationOnClickListener(v -> finish());



        partId = getIntent().getStringExtra(EXTRA_PART_ID);
        if (partId == null) {
            Toast.makeText(this, "Hiba: nincs termék azonosító",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.buttonAddToCart.setOnClickListener(v -> {
            Toast.makeText(this, "Hozzáadva", Toast.LENGTH_SHORT).show();
        });

        binding.buttonSaveImage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        REQ_STORAGE);

            } else {
                saveImage();
            }
        });


        loadPartAndSetupUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPartAndSetupUi();
    }

    private void loadPartAndSetupUi() {
        db.collection("parts")
                .document(partId)
                .get()
                .addOnSuccessListener(doc -> {
                    Part part = doc.toObject(Part.class);
                    if (part == null) { finish(); return; }

                    Glide.with(this)
                            .load(part.getImageUrl())
                            .into(binding.imageDetail);

                    binding.textName.setText(part.getName());
                    binding.textPrice.setText(String.format(
                            Locale.getDefault(), "%.2f €", part.getPrice()));
                    binding.textDescription.setText(part.getDescription());
                    int stock = part.getStock();
                    binding.textStockStatus.setText(
                            stock>0? "Raktáron: "+stock : "Elfogyott"
                    );
                    binding.buttonAddToCart.setEnabled(stock>0);
                    binding.buttonAddToCart.setOnClickListener(v -> {
                        if (stock>0) {
                            CartManager.getInstance().addToCart(part);
                            Toast.makeText(this,
                                    part.getName()+" hozzáadva a kosárhoz",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                })
                .addOnFailureListener(e -> finish());
    }

    private void saveImage() {
        db.collection("parts")
                .document(partId)
                .get()
                .addOnSuccessListener(doc -> {
                    Part part = doc.toObject(Part.class);
                    if (part == null) return;

                    Glide.with(this)
                            .asBitmap()
                            .load(part.getImageUrl())
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap bmp,
                                                            @Nullable Transition<? super Bitmap> t) {
                                    String savedUri = MediaStore.Images.Media.insertImage(
                                            getContentResolver(),
                                            bmp,
                                            part.getName(),
                                            "CarPartsShop letöltött kép"
                                    );
                                    if (savedUri != null) {
                                        Toast.makeText(ProductDetailActivity.this,
                                                "Kép elmentve a galériába", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProductDetailActivity.this,
                                                "Mentés sikertelen", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_STORAGE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveImage();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
