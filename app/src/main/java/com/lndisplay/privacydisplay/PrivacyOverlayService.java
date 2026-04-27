package com.lndisplay.privacydisplay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.core.app.NotificationCompat;

public class PrivacyOverlayService extends Service {

    public static final String ACTION_START = "START";
    public static final String ACTION_STOP = "STOP";
    public static final String ACTION_UPDATE_LEVEL = "UPDATE_LEVEL";
    public static final String EXTRA_LEVEL = "level";

    private static final String CHANNEL_ID = "ln_display_channel";
    private static final int NOTIF_ID = 42;

    private WindowManager windowManager;
    private PrivacyFilterView filterView;
    private boolean isShowing = false;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;

        String action = intent.getAction();
        if (action == null) return START_STICKY;

        switch (action) {
            case ACTION_START:
                int level = intent.getIntExtra(EXTRA_LEVEL, 70);
                startForeground(NOTIF_ID, buildNotification(true));
                showOverlay(level);
                break;

            case ACTION_STOP:
                removeOverlay();
                stopForeground(true);
                stopSelf();
                break;

            case ACTION_UPDATE_LEVEL:
                int newLevel = intent.getIntExtra(EXTRA_LEVEL, 70);
                if (filterView != null) {
                    filterView.setPrivacyLevel(newLevel);
                }
                break;
        }

        return START_STICKY;
    }

    private void showOverlay(int level) {
        if (isShowing) return;

        filterView = new PrivacyFilterView(this);
        filterView.setPrivacyLevel(level);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;

        windowManager.addView(filterView, params);
        isShowing = true;
    }

    private void removeOverlay() {
        if (filterView != null && isShowing) {
            windowManager.removeView(filterView);
            filterView = null;
            isShowing = false;
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "LN Display",
                NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription("Privacy Display ativo");
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(channel);
    }

    private Notification buildNotification(boolean active) {
        Intent openIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("LN Display")
                .setContentText(active ? "Privacy Display ativo — toque para ajustar" : "Privacy Display")
                .setSmallIcon(R.drawable.ic_privacy_tile)
                .setContentIntent(pi)
                .setOngoing(true)
                .setSilent(true)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        removeOverlay();
        super.onDestroy();
    }
}
