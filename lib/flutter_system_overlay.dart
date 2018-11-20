import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter/painting.dart';

class FlutterSystemOverlay {
  static const MethodChannel _channel =
      const MethodChannel('flutter_system_overlay');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future overlay({
    String title,
    String body,
    String cancel = "Cancel",
    String ok,
    Color primaryColor
  }) async {
    Map params = {};
    if (title != null) {
      params['title'] = title;
    }
    if (body != null) {
      params['body'] = body;
    }
    if (cancel != null) {
      params['cancel'] = cancel;
    }
    if (ok != null) {
      params['ok'] = ok;
    }
    if (primaryColor != null) {
      params['color'] = primaryColor.value;
    }
    await _channel.invokeMethod('overlay', params);
    return false;
  }
}
