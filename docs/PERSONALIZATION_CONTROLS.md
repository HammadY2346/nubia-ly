# Connectivity, power menu, notification controls and charging information

## UI and scope

- Status bar → Wi-Fi & mobile signal: independent Stock + Nothing Dot, Capsule, Round, Sharp, Wavy, Stroke, Xperia and ZigZag selections, each with five strength assets. Artwork adapted from RisingOS-Revived/android_vendor_addons, `seventeen/themes/icons/{wifi,signal}`. Original file notices retained. Tint follows the native view and strength follows native state.
- Dashboard: compact Power menu tile directly below Notifications, same artwork height (62dp) and column width. Existing Lock screen / QS / Status bar / Volume arrangement retained. Opens a separate editor overview and style page, using the same draft/save worker and explicit Enable/Restore.
- Power styles: Stock, Duoline, CyberPunk, Fluid, iOS and Layers, adapting Rising's visual treatments to REDMAGIC action containers. Only backgrounds are decorated. Native actions, alarms, emergency functions, confirmations, bouncer and security remain unchanged.
- Notifications → Appearance: background-only 50–100% opacity or Stock, plus additional corner clipping. Text/actions do not fade. Clipping can increase rounding inside the native silhouette; it cannot expand an existing native corner.
- Notifications → Controls: suppress new automatic heads-up presentations while keeping user-pinned presentations. History, per-app controls and system notification/snoozing settings open native settings pages. Native settings changes remain Android-owned and are explicitly separate from cosmetic Restore.
- Lock screen → Charging information: optional second line below native charging type/percentage. Battery voltage (broadcast millivolts → volts), signed battery current (BatteryManager microamps → milliamps) and temperature (tenths °C). Unsupported values are omitted or marked unavailable. Battery voltage is not USB input/charger voltage. No wattage is inferred, no timer, sysfs access or write.

## Inspected NX809J Android 16 owners

- `ModernStatusBarWifiView` / `ModernStatusBarMobileView` under exact `PhoneStatusBarView`, exact SystemUI IDs `wifi_signal` / `mobile_signal`. Wi-Fi resource identity recorded after native `ImageView.setImageResource`; only ordinary `(intl_)ic_wifi_signal_[0-4]` and inspected Wi-Fi 4/5/6/7 variants are substituted. No-internet/X/unrecognized resources stay stock. Inspected ordinary `sp_signal_cellular_[0-5]` / `statusbar_signal_[0-5]` resource variants normalize six native strength steps to five artwork steps; alert/no-internet/satellite variants remain native. The alternate drawable path requires exact `com.android.settingslib.graph.SignalDrawable`, ordinary packed state 0, valid level/count, and current native tint. Its low byte is level, next byte count, high state bits are preserved by leaving warnings/animations native. Native drawable, level, content description, visibility, geometry and subscriptions are never changed. Unsupported vendor render paths stay stock.
- `com.zte.feature.globalaction.MfvGlobalActionsDialog$MFVActionsDialog extends Dialog`: `mContentView: ViewGroup`, `mPowerContent/mResetContent: RelativeLayout`, `initView()`. Decorate exact current field identities attached under current content while dialog is showing, before child draw. No action hook.
- `HeadsUpManagerImpl.showNotification(NotificationEntry, boolean)`: boolean true maps to `PinnedByUser`; false maps to `PinnedBySystem`. Suppression skips only false presentations, without reading notification content. Existing alerts and full-screen behavior are not rewritten.
- `KeyguardIndicationController.computePowerIndication(): String`, `updateIndicationPublic(boolean)`, `mContext`, `mDozing`, `mPowerPluggedIn`, exact indication text fields. Preserve native result and append only while plugged, awake and nonblank. Only the exact two-line charging result permits multiline on the owned indication views. Other indications restore max-lines/transformation immediately; Restore unhooks and recomputes the native text. No biometric/error indication is replaced.

## Recovery and configuration

Six appended SurfaceStyle fields: Wi-Fi style, mobile style, power style, notification appearance, notification behavior, charging info. All old 9/10/11/15/16/17/18/19/20-field drafts decode with stock defaults. Only full 26-field new format accepted. Bounds validated before activation. Same recovery guard/session authority, main-thread checks, cleanup and failure fences as existing features; source-unavailable requested features refuse activation.

## Validation and phone batch

Host tests cover unrelated roots, resource replacement/warnings, native packed-level preservation, detached/off-main views, dialog ownership, suppression/restoration, charging unit conversion, unplug/AOD/biometric indication exclusion and text restoration. GitHub runs Android compilation, native graphics/resource tests, focused editor routes and draft-only persistence tests. Phone validation is still required.

1. Update signed APK and reboot for new observation hooks. Select Wi-Fi/mobile styles → Enable. Check both SIMs and normal light/dark status bars; test Wi-Fi loss/no internet and airplane mode remain truthful. Check technical glyph draw count if stock persists.
2. Open Power menu from the small dashboard tile. Choose style → Enable, then long-press physical power. Check decorations and normal cancel. No need to perform shutdown/restart for a visual test.
3. Notifications → Appearance, change opacity/corners → Enable. Check text/actions unchanged and native warning/group/heads-up clipping. Enable automatic heads-up suppression; test a new ordinary alert, then restore it. Open native settings buttons separately.
4. Lock screen → Charging information → Enable. Plug in, lock/wake, verify stock charging/percentage above a measured battery line. Unplug, enter fingerprint/face/error flows, enter AOD, then Restore stock: diagnostics should disappear and native messages remain.
5. Check recovery guard stop independently removes all active styling/behavior.
