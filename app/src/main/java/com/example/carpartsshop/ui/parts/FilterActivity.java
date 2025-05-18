package com.example.carpartsshop.ui.parts;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpartsshop.databinding.ActivityFilterBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterActivity extends AppCompatActivity {
    public static final String EXTRA_BRAND = "brand";
    public static final String EXTRA_YEAR  = "year";
    public static final String EXTRA_MODEL = "model";

    private ActivityFilterBinding binding;
    private FirebaseFirestore db;

    private static final String DUMMY_BRAND = "Válassz márkát";
    private static final String DUMMY_YEAR  = "Válassz évet";
    private static final String DUMMY_MODEL = "Válassz típust";

    private String prevBrand;
    private Integer prevYear;
    private String prevModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        Intent in = getIntent();
        prevBrand = in.getStringExtra(EXTRA_BRAND);
        if (in.hasExtra(EXTRA_YEAR)) {
            prevYear = in.getIntExtra(EXTRA_YEAR, -1);
        }
        prevModel = in.getStringExtra(EXTRA_MODEL);

        db = FirebaseFirestore.getInstance();

        initSpinners();

        loadBrands();

        binding.spinnerBrand.setOnItemSelectedListener(
                new SimpleItemSelectedListener(brandStr -> {
                    if (DUMMY_BRAND.equals(brandStr)) {
                        resetYearSpinner();
                        resetModelSpinner();
                    } else {
                        loadYears(brandStr);
                        resetModelSpinner();
                    }
                })
        );
        binding.spinnerYear.setOnItemSelectedListener(
                new SimpleItemSelectedListener(yearStr -> {
                    if (DUMMY_YEAR.equals(yearStr)) return;
                    String brand = binding.spinnerBrand.getSelectedItem().toString();
                    int year = Integer.parseInt(yearStr);
                    loadModels(brand, year);
                })
        );

        binding.buttonApply.setOnClickListener(v -> {
            String brand   = binding.spinnerBrand.getSelectedItem().toString();
            String yearStr = binding.spinnerYear.getSelectedItem().toString();
            String model   = binding.spinnerModel.getSelectedItem().toString();

            if (DUMMY_BRAND.equals(brand)
                    || DUMMY_YEAR.equals(yearStr)
                    || DUMMY_MODEL.equals(model)) {
                Toast.makeText(this,
                        "Kérlek, válassz mindhárom mezőben értéket!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent data = new Intent();
            data.putExtra(EXTRA_BRAND, brand);
            data.putExtra(EXTRA_YEAR,  Integer.parseInt(yearStr));
            data.putExtra(EXTRA_MODEL, model);
            setResult(RESULT_OK, data);
            finish();
        });

        binding.buttonClear.setOnClickListener(v -> {
            binding.spinnerBrand.setSelection(0);
            resetYearSpinner();
            resetModelSpinner();

            Intent data = new Intent();
            data.putExtra(EXTRA_BRAND, "");
            data.putExtra(EXTRA_YEAR,  0);
            data.putExtra(EXTRA_MODEL, "");
            setResult(RESULT_OK, data);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void initSpinners() {
        ArrayAdapter<String> yearDummy = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                Collections.singletonList(DUMMY_YEAR)
        );
        yearDummy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerYear.setAdapter(yearDummy);
        binding.spinnerYear.setSelection(0);

        ArrayAdapter<String> modelDummy = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                Collections.singletonList(DUMMY_MODEL)
        );
        modelDummy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerModel.setAdapter(modelDummy);
        binding.spinnerModel.setSelection(0);
    }

    private void resetYearSpinner() {
        ArrayAdapter<String> ad = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                Collections.singletonList(DUMMY_YEAR)
        );
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerYear.setAdapter(ad);
        binding.spinnerYear.setSelection(0);
    }

    private void resetModelSpinner() {
        ArrayAdapter<String> ad = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                Collections.singletonList(DUMMY_MODEL)
        );
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerModel.setAdapter(ad);
        binding.spinnerModel.setSelection(0);
    }

    private void loadBrands() {
        db.collection("parts").get().addOnSuccessListener(snaps -> {
            Set<String> set = new HashSet<>();
            for (QueryDocumentSnapshot doc : snaps) {
                String b = doc.getString("brand");
                if (b != null) set.add(b);
            }
            List<String> list = new ArrayList<>(set);
            Collections.sort(list);
            list.add(0, DUMMY_BRAND);

            ArrayAdapter<String> ad = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, list
            );
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerBrand.setAdapter(ad);

            if (prevBrand != null && list.contains(prevBrand)) {
                binding.spinnerBrand.setSelection(list.indexOf(prevBrand));
                loadYears(prevBrand);
            } else {
                binding.spinnerBrand.setSelection(0);
            }
        });
    }

    private void loadYears(String brand) {
        db.collection("parts")
                .whereEqualTo("brand", brand)
                .get()
                .addOnSuccessListener(snaps -> {
                    Set<Integer> set = new HashSet<>();
                    for (QueryDocumentSnapshot doc : snaps) {
                        Long y = doc.getLong("year");
                        if (y != null) set.add(y.intValue());
                    }
                    List<String> list = new ArrayList<>();
                    for (Integer y : set) list.add(String.valueOf(y));
                    Collections.sort(list);
                    list.add(0, DUMMY_YEAR);

                    ArrayAdapter<String> ad = new ArrayAdapter<>(
                            this, android.R.layout.simple_spinner_item, list
                    );
                    ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerYear.setAdapter(ad);

                    if (prevYear != null) {
                        String py = String.valueOf(prevYear);
                        if (list.contains(py)) {
                            binding.spinnerYear.setSelection(list.indexOf(py));
                            loadModels(brand, prevYear);
                        }
                        prevYear = null;
                    } else {
                        binding.spinnerYear.setSelection(0);
                    }
                });
    }

    private void loadModels(String brand, int year) {
        db.collection("parts")
                .whereEqualTo("brand", brand)
                .whereEqualTo("year", year)
                .get()
                .addOnSuccessListener(snaps -> {
                    Set<String> set = new HashSet<>();
                    for (QueryDocumentSnapshot doc : snaps) {
                        String m = doc.getString("model");
                        if (m != null) set.add(m);
                    }
                    List<String> list = new ArrayList<>(set);
                    Collections.sort(list);
                    list.add(0, DUMMY_MODEL);

                    ArrayAdapter<String> ad = new ArrayAdapter<>(
                            this, android.R.layout.simple_spinner_item, list
                    );
                    ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerModel.setAdapter(ad);

                    if (prevModel != null && list.contains(prevModel)) {
                        binding.spinnerModel.setSelection(list.indexOf(prevModel));
                        prevModel = null;
                    } else {
                        binding.spinnerModel.setSelection(0);
                    }
                });
    }
}
