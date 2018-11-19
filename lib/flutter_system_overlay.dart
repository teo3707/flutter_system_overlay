import 'dart:async';

import 'package:flutter/services.dart';

class FlutterSystemOverlay {
  static const MethodChannel _channel =
      const MethodChannel('flutter_system_overlay');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future overlay() async {
    await _channel.invokeMethod('overlay');
    return false;
  }
}
