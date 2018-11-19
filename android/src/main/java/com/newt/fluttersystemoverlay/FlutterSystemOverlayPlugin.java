package com.newt.fluttersystemoverlay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterSystemOverlayPlugin */
public class FlutterSystemOverlayPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener {

  private Registrar mRegistrar;

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
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("overlay")) {
      if (Build.VERSION.SDK_INT >= 23) {
        if (!Settings.canDrawOverlays(mRegistrar.activity().getApplicationContext())) {
          Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mRegistrar.activity().getPackageName()));
          mRegistrar.activity().startActivityForResult(intent, 10);
        } else {
          onActivityResult(10, 0, null);
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
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= 26
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
                PixelFormat.TRANSLUCENT
        );
        WindowManager wm = (WindowManager) mRegistrar.activity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        TextView title = new TextView(mRegistrar.activity().getApplicationContext());
        title.setText("let me go");
        LinearLayout root = new LinearLayout(mRegistrar.activity().getApplicationContext());
        root.setOnClickListener((v) -> {
          wm.removeView(root);
        });
        root.setOrientation(LinearLayout.VERTICAL);
        root.addView(title);
        wm.addView(root, params);
      }
    }
    return false;
  }
}
