package com.newt.fluttersystemoverlay;


import android.content.Intent;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;


/** FlutterSystemOverlayPlugin */
public class FlutterSystemOverlayPlugin implements MethodCallHandler{

  private Registrar mRegistrar;

  public static MethodCall METHOD_CALL;

  FlutterSystemOverlayPlugin(Registrar registrar) {
    mRegistrar = registrar;
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_system_overlay");
    channel.setMethodCallHandler(new FlutterSystemOverlayPlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    METHOD_CALL = call;
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("overlay")) {
      mRegistrar.activity().startActivity(new Intent(mRegistrar.context(), OverlayActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
      mRegistrar.activity().finish();
    } else {
      result.notImplemented();
    }
  }
}
