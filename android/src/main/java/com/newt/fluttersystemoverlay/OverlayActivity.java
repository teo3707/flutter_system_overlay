package com.newt.fluttersystemoverlay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.flutter.plugin.common.MethodCall;


public class OverlayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overlay);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        View root = getWindow().getDecorView();
        MethodCall mCall = FlutterSystemOverlayPlugin.METHOD_CALL;
        if (mCall == null) {
            return;
        }
        if (mCall.argument("title") != null) {
            root.findViewById(R.id.overlay_title).setVisibility(View.VISIBLE);
            root.<TextView>findViewById(R.id.overlay_title).setText("" + mCall.argument("title"));
        }

        if (mCall.argument("body") != null) {
            root.findViewById(R.id.overlay_body).setVisibility(View.VISIBLE);
            root.<TextView>findViewById(R.id.overlay_body).setText("" + mCall.argument("body"));
        }

        if (mCall.argument("cancel") != null) {
            root.findViewById(R.id.overlay_cancel).setVisibility(View.VISIBLE);
            root.<TextView>findViewById(R.id.overlay_cancel).setText("" + mCall.argument("cancel"));
        }

        if (mCall.argument("ok") != null) {
            TextView ok = root.findViewById(R.id.overlay_ok);
            ok.setVisibility(View.VISIBLE);
            ok.setText("" + mCall.argument("ok"));
            if (mCall.argument("color") != null) {
                ok.setTextColor(mCall.<Long>argument("color").intValue());
            }
        }

        root.findViewById(R.id.overlay_cancel).setOnClickListener((v) -> {
            finish();
        });

        root.findViewById(R.id.overlay_ok).setOnClickListener((v) -> {
            Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
