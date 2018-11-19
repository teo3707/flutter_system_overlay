#import "FlutterSystemOverlayPlugin.h"
#import <flutter_system_overlay/flutter_system_overlay-Swift.h>

@implementation FlutterSystemOverlayPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterSystemOverlayPlugin registerWithRegistrar:registrar];
}
@end
