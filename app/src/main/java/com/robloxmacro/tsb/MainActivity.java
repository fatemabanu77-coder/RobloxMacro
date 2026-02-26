package com.robloxmacro.tsb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int OVERLAY_PERMISSION_REQ = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView statusText = findViewById(R.id.statusText);
        Button btnPermission = findViewById(R.id.btnPermission);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);

        updateUI(statusText, btnPermission, btnStart, btnStop);

        btnPermission.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ);
        });

        btnStart.setOnClickListener(v -> {
            if (Settings.canDrawOverlays(this)) {
                startService(new Intent(this, OverlayService.class));
                Toast.makeText(this, "Macro overlay started!", Toast.LENGTH_SHORT).show();
                updateUI(statusText, btnPermission, btnStart, btnStop);
            } else {
                Toast.makeText(this, "Please grant overlay permission first!", Toast.LENGTH_SHORT).show();
            }
        });

        btnStop.setOnClickListener(v -> {
            stopService(new Intent(this, OverlayService.class));
            Toast.makeText(this, "Macro overlay stopped.", Toast.LENGTH_SHORT).show();
            updateUI(statusText, btnPermission, btnStart, btnStop);
        });
    }

    private void updateUI(TextView statusText, Button btnPermission, Button btnStart, Button btnStop) {
        boolean hasPermission = Settings.canDrawOverlays(this);
        if (hasPermission) {
            statusText.setText("✅ Overlay Permission Granted\nOpen Roblox TSB and tap Start Overlay.");
            btnPermission.setVisibility(View.GONE);
            btnStart.setEnabled(true);
        } else {
            statusText.setText("⚠️ Overlay Permission Required\nGrant permission to display the macro button over Roblox.");
            btnPermission.setVisibility(View.VISIBLE);
            btnStart.setEnabled(false);
        }
