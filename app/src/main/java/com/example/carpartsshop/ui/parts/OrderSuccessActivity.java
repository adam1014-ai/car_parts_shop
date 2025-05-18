package com.example.carpartsshop.ui.parts;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.carpartsshop.NotificationHelper;
import com.example.carpartsshop.data.CartManager;
import com.example.carpartsshop.data.CartManager.CartItem;
import com.example.carpartsshop.databinding.ActivityOrderSuccessBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Locale;

public class OrderSuccessActivity extends AppCompatActivity {
    private static final String TAG = "OrderSuccessActivity";
    private static final int REQ_LOCATION = 200;
    private static final int REQ_POST_NOTIF = 5000;

    private ActivityOrderSuccessBinding binding;
    private FirebaseFirestore db;
    private CartManager cartManager;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db                  = FirebaseFirestore.getInstance();
        cartManager         = CartManager.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setSupportActionBar(binding.toolbarSuccess);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        binding.toolbarSuccess.setNavigationIcon(null);

        WriteBatch batch = db.batch();
        List<CartItem> items = cartManager.getCartItems();
        for (CartItem item : items) {
            DocumentReference ref = db.collection("parts")
                    .document(item.part.getId());
            batch.update(ref, "stock", FieldValue.increment(-item.quantity));
        }
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    cartManager.clearCart();
                    setupSuccessUi();

                    sendOrderNotification();
                    schedulePickupReminder();
                    checkLocationPermissionAndShowDistance();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Batch error", e);
                    setupErrorUi();
                });
    }

    private void setupSuccessUi() {
        binding.textSuccess.setText("Rendelésed sikeresen rögzítettük.");
        binding.textPickup.setText("Vedd át itt: 1234 Budapest, Fő utca 5.");
        binding.buttonDone.setText("Vissza a főoldalra");
        binding.buttonDone.setOnClickListener(v -> {
            startActivity(new Intent(this, ProductListActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        });
    }

    private void setupErrorUi() {
        binding.textSuccess.setText("Hiba történt a rendelés során");
        binding.textPickup.setText("");
        binding.buttonDone.setText("Vissza a főoldalra");
        binding.buttonDone.setOnClickListener(v -> finish());
    }

    private void sendOrderNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.POST_NOTIFICATIONS },
                        REQ_POST_NOTIF);
                return;
            }
        }
        NotificationHelper.showOrderNotification(this);
    }

    @RequiresPermission("android.permission.SCHEDULE_EXACT_ALARM")
    private void schedulePickupReminder() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!am.canScheduleExactAlarms()) {
                Log.w(TAG, "schedulePickupReminder(): exact alarms DISABLED in system settings!");
                return;
            }
        }

        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerAt = System.currentTimeMillis() + 1L * 60L * 1000L;
        Log.d(TAG, "schedulePickupReminder(): scheduling alarm for " + triggerAt);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        }
    }


    private void checkLocationPermissionAndShowDistance() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQ_LOCATION);
        } else {
            showDistanceToStore();
        }
    }

    private void showDistanceToStore() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location store = new Location("store");
        store.setLatitude(47.4979);
        store.setLongitude(19.0402);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(loc -> {
                    if (loc != null) {
                        float km = loc.distanceTo(store) / 1000f;
                        String text = String.format(Locale.getDefault(),
                                "Vedd át itt: 1234 Budapest, Fő utca 5.\nTávolság a boltig: %.2f km",
                                km
                        );
                        binding.textPickup.setText(text);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Location error", e));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_POST_NOTIF) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.showOrderNotification(this);
            }
        }
        else if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showDistanceToStore();
            } else {
                Toast.makeText(this, "Helyzet-engedély megtagadva", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
