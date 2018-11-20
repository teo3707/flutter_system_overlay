package com.newt.fluttersystemoverlay;


import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.provider.Settings;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.graphics.PixelFormat;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;


/** FlutterSystemOverlayPlugin */
public class FlutterSystemOverlayPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener {

  private Registrar mRegistrar;
  private MethodCall mCall;

  FlutterSystemOverlayPlugin(Registrar registrar) {
    mRegistrar = registrar;
    mRegistrar.addActivityResultListener(this);
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_system_overlay");
    channel.setMethodCallHandler(new FlutterSystemOverlayPlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    mCall = call;
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("overlay")) {
      if (Build.VERSION.SDK_INT >= 23) {
        if (!Settings.canDrawOverlays(mRegistrar.activity().getApplicationContext())) {
          Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mRegistrar.activity().getPackageName()));
          mRegistrar.activity().startActivityForResult(intent, 10);
        } else {
            makeOverlay();
        }
      }
    } else {
      result.notImplemented();
    }
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (Build.VERSION.SDK_INT >= 23 && requestCode == 10) {
      System.out.println("[*********]" + Settings.canDrawOverlays(mRegistrar.activity().getApplicationContext()));
      if (!Settings.canDrawOverlays(mRegistrar.activity().getApplicationContext())) {
        Toast.makeText(mRegistrar.activity().getApplicationContext(), "not granted",Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(mRegistrar.activity().getApplicationContext(), "granted",Toast.LENGTH_SHORT).show();
          makeOverlay();
      }
    }
    return false;
  }

  private void makeOverlay() {
    if (mCall == null) {
      return;
    }

    WindowManager wm = (WindowManager) mRegistrar.activity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics metrics = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(metrics);
    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            (int)(metrics.widthPixels * 0.7),
            WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT >= 26
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            0,
            PixelFormat.TRANSLUCENT
    );
    View root = View.inflate(mRegistrar.activity().getApplicationContext(), R.layout.overlay, null);
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
      wm.removeView(root);
    });

    root.findViewById(R.id.overlay_ok).setOnClickListener((v) -> {
        wm.removeView(root);
        Intent intent = mRegistrar.activity().getApplicationContext().getPackageManager().getLaunchIntentForPackage(mRegistrar.activity().getApplicationContext().getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mRegistrar.activity().getApplicationContext().startActivity(intent);
    });
    wm.addView(root, params);
  }
}
