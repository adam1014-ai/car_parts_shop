package com.example.carpartsshop;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationHelper {
    public static final String CHANNEL_ID   = "order_channel";
    public static final String CHANNEL_NAME = "Rendelés értesítések";

    public static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Értesítések a rendeléssel kapcsolatban");
            android.app.NotificationManager nm =
                    ctx.getSystemService(android.app.NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    public static void showOrderNotification(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_success)
                .setContentTitle("Rendelés kész")
                .setContentText("Sikeresen leadtad a rendelésedet!")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(ctx).notify(1001, b.build());
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public static void showPickupReminderNotification(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle("Ne felejts el elmenni érte!")
                .setContentText("Vedd át a rendelésedet: 1234 Budapest, Fő utca 5.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(ctx).notify(1002, b.build());
    }
}
