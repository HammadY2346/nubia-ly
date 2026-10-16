# Status-bar battery styles

Status bar → Battery styles opens visual cards and saves into the existing shared draft. Enable applies; Restore / independent recovery stop removes the draw substitution. Dashboard layout is unchanged.

Stock plus 24 alternatives: Portrait, Circle, Dotted circle, Filled circle, Text only, Hidden, Landscape right/left, Big circle/dotted circle, Capsule, Lorn, Portrait iOS, Portrait Mx, Airoo, right/left A and B, iOS 15/16, Origami, OneUI 7 and MiUI Pill.

## Artwork provenance

Original geometry and directional fill specifications are adapted from RisingOS-Revived/android_frameworks_base, branch `sixteen`, which contains the implementations corresponding to the Personalizations labels:

- `core/res/res/values/config.xml` and `custom_config.xml`: perimeter, fill, charging and power-saving paths.
- `packages/SettingsLib/src/com/android/settingslib/graph/{Circle,FullCircle,Landscape*,RLandscape*}BatteryDrawable*.kt`: intrinsic coordinate systems, fill directions and circle treatment.
- Source: https://github.com/RisingOS-Revived/android_frameworks_base/tree/sixteen/packages/SettingsLib/src/com/android/settingslib/graph
- Configuration: https://github.com/RisingOS-Revived/android_frameworks_base/blob/sixteen/core/res/res/values/custom_config.xml

`BatteryGeometry.java` retains Apache 2.0 attribution. The common Java renderer adapts those paths to live REDMAGIC state without importing ROM-only settings, hidden resources, observers or animation loops. MiUI Pill is an app-specific rounded-pill adaptation: the reference Personalizations strings include that name, while the inspected battery switch has no corresponding standalone renderer. Previews are illustrative samples, not live telemetry. Dimensions are fitted uniformly into the stock icon footprint; large-circle styles do not enlarge the status-bar row. External percentage and charging indicators retain native positions.

## Inspected NX809J Android 16 boundary

`com.zte.mifavor.views.MFVBatteryViewLayout extends RelativeLayout` under exact `PhoneStatusBarView` owns `mBatteryContainer: FrameLayout` and exact `mBatteryIconView: MFVBatteryMeterView`. Inspected compiled layout `res/NGw.xml` has no background on the layout/container. The battery icon has a separate native frame background, fill drawable, inside percent and charge views. Consequently substitution targets the exact current container's `dispatchDraw` pass, covering all of those layers together; it does not simply overlay a new shape on the old native frame.

State comes from the current native icon fields: `mBateryLevel`, `mIsCharing`, `mIsPowerSave`, `mContentColor`, `mStatusBarColor`, `mContentResId`, `mChargingSeparation`, `mOemChargeType` and `mType.isStatusBar()`. Current content must match a nonzero member of the inspected native `ZTE_BATTARY_CONTENT_RES_IDS` table. Invalid/unknown levels, unrecognized artwork, charging separation and overvoltage state 128 stay native. Native low-level rule (≤20: `0xe5ff0000`) is retained, alongside native normal/power-saving tint. No battery/model/drawable/view-field writes, percentage-setting changes, sysfs I/O, battery receiver, timer, or layout mutation.

Normal styles substitute only the native icon container, sized to the actual icon rectangle inside it; native external percentage/charging views still draw normally. If the percentage/charging symbol belongs inside that container, the shared renderer redraws it in the selected shape. Text only substitutes the full owned group's children with percentage/charging text; Hidden skips owned group graphics but retains its layout space and content description. Unknown-state fallback remains stock for Text only. Other lock-screen, AOD, QS, widget and call-screen battery instances are excluded by ownership and type.

27th SurfaceStyle field is an append-only ID 0..24. Legacy 9/10/11/15/16/17/18/19/20/26-field formats default to stock. Main-thread, generation, attachment, current owner/container/icon identity and recovery guards fence every draw. Rendering uses a separate layer and clears an incomplete pass on revocation; Restore unhooks and invalidates native views, which retain their current data.

## Validation and phone batch

Host backend regression tests cover all IDs, current state/colors, unknown/charging-separation/overvoltage fallback, unrelated roots/types/AOD, detached/off-main views, replaced containers, revocation and stock drawing after restoration. GitHub additionally checks actual path rendering, level-dependent pixels, hidden/text behavior, clipping, balanced canvas, cancellation, migration and draft-only editor navigation/recreation. Phone validation is required.

1. Install signed test APK, reboot, open Status bar → Battery styles. Select a clearly different shape and press Enable.
2. Check circle, dotted circle, landscape, a portrait and Text only. Native external percent retains its stock position; designs must not stretch, overlap Wi-Fi, or change actual battery values.
3. Plug/unplug and check inside/outside percentage modes if used. Confirm charging and saver states remain understandable. Check light and dark backgrounds.
4. QS, lock screen/AOD and voltage diagnostics should remain native/unchanged. Hidden must hide the group graphics while retaining space.
5. Restore stock and independently stop the recovery guard; native frame, percent, charging and latest level must return. If a selection remains stock, check the expandable battery draw/fallback counts.
