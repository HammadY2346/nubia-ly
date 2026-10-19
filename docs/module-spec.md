# NX789J private LSPosed module — full feature spec (v1)

Base project: your `nubia-ly` fork (classic Xposed API, `Backend.install(classLoader)` pattern, guard module, CI).
Everything below is **taken from RedMagic Helper 3.4.6** (decompiled; targets are exact strings) unless marked **NEW**.
Hooks are grouped by the scope package they run in. Default every switch to OFF; wrap every `install()` in try/catch + log.

---

## 1. Game Space (scope: `cn.nubia.gameassist`, `cn.nubia.gamelauncher`, `cn.nubia.gamehelpmodule`, `cn.nubia.gamelab`, `cn.nubia.gamehighlights`, `cn.nubia.tgk`)

| Feature | Target → hook |
|---|---|
| Remove functional restrictions (shoulder keys / combo / plugins in any game) | `cn.nubia.gamehelper.utils.PackageUtils.isBlackPackageName`→false · `cn.nubia.gameassist.plugin.config.PluginConfig.isPackageEnable`, `.isMacroEnable`→true · `Bundle.getBoolean("is_need_show_linkview")`→true · `TopService` plugin gate → allow all |
| Trigger-key linkage everywhere | `cn.nubia.tgk.TgkHelper`: `mSupportedGameKeyLink`/`gameKeyLinkSupported`→true, `getGameKeyLinkMotionState`→true, `getBlackList`→empty |
| Super Resolution + Diablo mode coexist | `plugin.tiles.SuperResolutionTile.handleClick` (`low_power`→0, perf 5→3) · `cn.nubia.plugin.superresolution.SuperResolutionViewController.i`/`checkGameMode` block auto-off · `performance.PerformanceModeController.updatePerformanceMode` block `setBiabloModeEnable(false,false)` · `plugin.tiles.BiabloTile.handleClick` `isOpenSuperResolution`→false |
| Hide Diablo / SR confirm dialogs | `PerformanceModeController` dialog paths suppressed; `game_space_prevent_collapse` flag |
| Diablo mode in Game Space panel | `gamecontrolpanel.GameFunctionAllocationView.initView/updateUI/onClick`, block `disableRedMagicTime`; prefs `redmagic_time_prefs`, `performance_mode_value`=4 |
| Active mode for any first package | `dessert.policy.ActiveModeController.onLauncherFirstPackage`; block `removeActiveModeSharedPreKey` (`acive_mode_switch`) |
| One-key combo playback speed | `manager.GameMotionHelperManager.canUseMacro/notifyChange/recoveryMotion`, `MacroSwitch.isSpeedModeEnable`→true, `utils.DeviceUtil.isUI85`→true, `bean.RecordMotionBean.getSpeed`, `travel.action.TravelAction.playScript`, `db.GameTouchProvider` |
| Combo loop-count / interval caps removed | `TopService` max-loop / max-interval setters |
| AI trigger intervals (template/click/cooldown/YOLO) | `cn.nubia.gamelab.toy.CommonToy$2`, `SGameToy$1`: `Handler.postDelayed(..., MSG_DELAY_TIME)` scaled |
| Record freely (Red Magic Moments any mode) | `ManualRecordTile.getCheckedId`; `TopService.onAbnormalHandling(type 10)` swallowed |
| Force show keys for SGame | `GameAssistDebug` "Force showing mKeys" |
| Game radio / weather plugins | `NubiaGameRadio`, `NubiaWeather` (minor) |

## 2. System UI (scope: `com.android.systemui`)

**2a. From nubia-ly (reuse code):** battery style/control (MFVBatteryMeterView, MFVBatteryViewLayout, ChargeIndicator), charging info text (KeyguardIndicationController), clock capture/color/date/seconds/style/visibility (`statusbar.policy.Clock`, `ClockAdapt`), connectivity icons (ModernStatusBarMobileView, SignalDrawable), notification background/icons/heads-up, QS grid/layout/shape/outline (MfvPagedTileLayout, QsDimenUtils, QSTileViewCircle/Wide), brightness + volume sliders, power menu, status-bar icon adapt, system icon pack, lock-screen clocks (Surface), fingerprint UI + sensor animations.

**2b. From RedMagic Helper (new):**
- Icon tuner (hide/show every status icon: mobile/wifi/BT/NFC/VPN/alarm/zen/headset/HD/VoLTE/roam/SIM1-2, swap mobile↔wifi, battery padding/size/symbol, "ignore system settings").
- Status-bar grid (zones L/C/R × rows, dual row, gap), notification/system icons two rows, max notification icons, hide cellular icon, carrier labels (SIM1/SIM2/HD), disable smart-dark.
- Readouts: battery temp / current / power / CPU temp / GPU temp (sysfs), display scene & modes, dual network speed + 1 s refresh (`StatusBarNetSpeedMFV`).
- Clock: geek format pattern, tnum, seconds/ms/week/CN-hour, margins, alignment, pull-down time/period.
- Lock screen: hide status bar, keep notifications after unlock, hide unlock tip, hide DND notif, allow volume adjust, auto flashlight, double-tap sleep, block long-press editor, lock-screen refresh rate, font-size zoom.
- Lock-screen charging: charge info at bottom (current/voltage/power/temp, refresh interval, delay), charging animation on every lock + duration, **collaboration charging animation** (`com.zte.feature.charging.ChargingFeature.startChargingAnimation`, `ChargeUtils.getChargeBitmapByIndex`, `KeyguardViewMediator.onStartedWakingUp`, `hideChargingViewDelayed`).
- Notifications: expand all/first/ungrouped, disable whitelist, open in small window, Miuix expand button, redirect notification settings, clock anim/colour fixes, advanced textures with themes.
- Media control style (full page: background cover/blur/gradient, progress style squiggly/thickness, thumb, text sizes, actions order/left-align, album/app icon, hide seamless/time).
- Monet theme colour / custom overlay colour; QS: custom rows/cols (portrait + landscape), show carrier/search, shortcut redirects (calendar/browser); battery percent always / anim fix; `CCHeaderView`, `NubiaAodTextClock`, AOD clock tweaks; QS tile for the module.

## 3. Android framework (scope: `android`)
Freeform: all apps small-window, max 255 windows, multi-instance, hide top bar, exclusion list, number limit (`ActivityTaskManagerService`, `ActivityRecord`, `Task`, `TaskDisplayAreaMifavor`, `WindowState`, `SafeActivityOptions`) · disable fixed orientation (+scope) · rotation suggestions · phantom-process limit off · untrusted touches · alert-window notification removed · screenshots allowed (FLAG_SECURE off: `disable_flag_secure_enhanced`, `DisableFlagSecureHookerZygoteInit`) · force-dark whitelist off · airplane keep WLAN/hotspot & BT (`WifiService`, `WifiNative`) · NFC screen-off recognition (`NfcService`) · volume gain per stream/route + mute-volume-detection (`AudioService`) · min font scale / font scale · wallpaper scale · long-power-key assist · 72 h lock-password requirement off · lock-screen idle timeout · **disable thermal refresh-rate/blur limiting** (`system_framework_other_disable_thermal`) · block `com.zte.analytics` telemetry · WLAN country code, fixed STA MAC / AP BSSID · double-app for any app (`com.zte.cn.doubleapp`, `rm_low_memory_limit`).

## 4. Package installer (scope: `com.android.packageinstaller`, `android` pm)
Allow signature-mismatch updates (`InstallPackageHelper`) · disable system APK signature verification · remove intent hijack to official installer · remove system-app install restriction (`others_package_update_system_app`) · show more package info · hide store install prompt · hide risk warnings · CTS test installer · permission-controller tweaks.

## 5. System theme (scope: `com.zte.beautify` = Theme Store, `com.zte.fingerprints`, `com.android.systemui`)
- **Unlock collaboration preset themes**: hook `com.zte.beautify.view.common.tools.Utilities.getThemeResourceFeature` / `getStringSystemProperties` to return the chosen variant: `nx789j_ip_mp_cn` (Phantom · Mora Pink), `nx789s_ip_mc_cn` (CAMELLYA · Wuthering Waves), `nx789s_w_ip_mk_cn` (Wukong), `nx789s_w_ip_pb_cn` (GOLDEN SAGA 789), `nx809j_w_ip_mc_cn` (Chisa), `nx809j_w_saga`, `nx729j/nx769j_ip_tf_cn` (Bumblebee). Also `DownloadManager`/`ResourceDownload`/`themeDownload`/`aodDownload` hooks so the store serves the pack.
- **Fingerprint styles unlock** (`com.zte.fingerprints`): `FingerprintAnimProcess.getAllFpAnim` list extended with `com.zte.fingerprint.theme.normal.Chisa`, `LTYIPAnimation` (Luo Tianyi); flags for Fighting Buddha, GOLDEN SAGA (reskins flag 16/20), CAMELLYA (replaces Force Matrix).
- **NEW — custom fingerprint animation**: already in nubia-ly (`SensorFingerprintBackend`, rising-udfps artwork loader on `com.fingerprint.sensorservice`) → expose a picker (frames folder) in the same UI.
- **NEW — unlock all + custom charging animations**: hook `ChargeUtils.getChargeBitmapByIndex` to serve frames from a user folder (`/data/adb/<module>/charging/<set>/NN.webp`) and `ChargingFeature.startChargingAnimation` to pick the set; stock sets (`redmagic_pb_fast_charge_%02d`, `redmagic_wireless_pb_charge_%02d`, collab sets) become selectable regardless of theme.
- Fonts: custom font file path for lock-screen clock / status bar (`NubiaFont`).

## 6. System settings (scope: `com.android.settings`)
Show developer options · auto screen-off 30 min / never · allow third-party default launcher · USB default mode + hide USB pop-up (`UsbModeUtils`) · unlock mobile network type settings · unlock VoIP assistant · disable device-name sensitive-word check · hide Healthy-use entry / Minors-mode icon / Evolution-mode toggle (root) · System Update mock page (Build.DISPLAY, fingerprint, inner version, variant, manufacturer, IMEI, locale, update-app signature — `com.zte.zdm.mo.DevInfo/DevInfoEX`).

## 7. Others
Neo Store: tab filter, simultaneous downloads · Browser (`cn.nubia.browser`): custom search engines/icons, more engines · Screenshot/recorder: PNG, standard folders, logger · Gallery: path recognition, unlimited crop · File browser MTP: hide category browse · Baidu IME clipboard limits · Joyose no cloud control · Permission controller · screen-off hide apps (root) · module QS tile with custom image.

## 8. Launcher (scope: `com.zte.mifavor.launcher`) — **to verify on your APK**
RedMagic Helper only touches recents (hide status bar, show memory). For **home/drawer in landscape**: the launcher is Launcher3-based; the standard unlock is hooking the main activity's `setRequestedOrientation` → `SCREEN_ORIENTATION_UNSPECIFIED` (or `RotationHelper.setStateFromActivityFlags` / `pref_allowRotation`) so home rotates with the phone; a horizontally-paged drawer depends on whether mifavor ships a paged `AllAppsContainerView` variant — I need the launcher APK to answer that. Game Space launcher (`cn.nubia.gamelauncher`) is already landscape.

---

## Build order
1. Base = nubia-ly; add scopes. 2. Batch 1 = Game Space §1 (top 4 rows). 3. Batch 2 = theme/fingerprint/charging §5 (highest "custom" value). 4. Batch 3 = SystemUI extras §2b. 5. Batch 4 = framework/installer/settings §3–6. 6. Launcher §8 after APK check.

## APKs needed from your phone to pin exact method names
`cn.nubia.gameassist`, `cn.nubia.gamelauncher`, `cn.nubia.gamehelpmodule`, `cn.nubia.gamelab`, `cn.nubia.gamehighlights`, `com.zte.beautify`, `com.zte.fingerprints`, `com.fingerprint.sensorservice`, `com.zte.mifavor.launcher`, `com.android.systemui`, `com.android.settings`, `com.android.packageinstaller`, and `/system/framework/services.jar` (+ `framework.jar`).
