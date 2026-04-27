package com.lndisplay.privacydisplay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQ = 1001;
    public static final String PREFS = "ln_display_prefs";
    public static final String KEY_ENABLED = "privacy_enabled";
    public static final String KEY_LEVEL = "privacy_level";

    private Switch switchPrivacy;
    private SeekBar seekBarPrivacy;
    private TextView tvStatus;
    private TextView tvLevelLabel;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        switchPrivacy = findViewById(R.id.switchPrivacy);
        seekBarPrivacy = findViewById(R.id.seekBarPrivacy);
        tvStatus = findViewById(R.id.tvStatus);
        tvLevelLabel = findViewById(R.id.tvLevelLabel);

        int savedLevel = prefs.getInt(KEY_LEVEL, 70);
        boolean savedEnabled = prefs.getBoolean(KEY_ENABLED, false);

        seekBarPrivacy.setProgress(savedLevel);
        updateLevelLabel(savedLevel);

        switchPrivacy.setChecked(savedEnabled);
        updateStatusUI(savedEnabled);

        switchPrivacy.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                if (!Settings.canDrawOverlays(this)) {
                    requestOverlayPermission();
                    switchPrivacy.setChecked(false);
                    return;
                }
                enablePrivacy();
            } else {
                disablePrivacy();
            }
        });

        seekBarPrivacy.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateLevelLabel(progress);
                prefs.edit().putInt(KEY_LEVEL, progress).apply();
                if (prefs.getBoolean(KEY_ENABLED, false)) {
                    updateOverlayLevel(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void enablePrivacy() {
        prefs.edit().putBoolean(KEY_ENABLED, true).apply();
        updateStatusUI(true);
        Intent intent = new Intent(this, PrivacyOverlayService.class);
        intent.setAction(PrivacyOverlayService.ACTION_START);
        intent.putExtra(PrivacyOverlayService.EXTRA_LEVEL, seekBarPrivacy.getProgress());
        ContextCompat.startForegroundService(this, intent);
        Toast.makeText(this, "Privacy Display ativado!", Toast.LENGTH_SHORT).show();
    }

    private void disablePrivacy() {
        prefs.edit().putBoolean(KEY_ENABLED, false).apply();
        updateStatusUI(false);
        Intent intent = new Intent(this, PrivacyOverlayService.class);
        intent.setAction(PrivacyOverlayService.ACTION_STOP);
        startService(intent);
        Toast.makeText(this, "Privacy Display desativado", Toast.LENGTH_SHORT).show();
    }

    private void updateOverlayLevel(int level) {
        Intent intent = new Intent(this, PrivacyOverlayService.class);
        intent.setAction(PrivacyOverlayService.ACTION_UPDATE_LEVEL);
        intent.putExtra(PrivacyOverlayService.EXTRA_LEVEL, level);
        startService(intent);
    }

    private void updateStatusUI(boolean enabled) {
        if (enabled) {
            tvStatus.setText("● ATIVO");
            tvStatus.setTextColor(0xFF00FF88);
        } else {
            tvStatus.setText("● INATIVO");
            tvStatus.setTextColor(0xFFAA44FF);
        }
    }

    private void updateLevelLabel(int progress) {
        String label;
        if (progress < 30) label = "Baixo";
        else if (progress < 60) label = "Médio";
        else if (progress < 85) label = "Alto";
        else label = "Máximo";
        tvLevelLabel.setText("Privacidade: " + progress + "% — " + label);
    }

    private void requestOverlayPermission() {
        Toast.makeText(this, "Permita exibição sobre outros apps", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, OVERLAY_PERMISSION_REQ);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == OVERLAY_PERMISSION_REQ) {
            if (Settings.canDrawOverlays(this)) {
                switchPrivacy.setChecked(true);
                enablePrivacy();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean enabled = prefs.getBoolean(KEY_ENABLED, false);
        switchPrivacy.setChecked(enabled);
        updateStatusUI(enabled);
    }
}
