# Verified hook map — NX789J firmware 11.0.11 (Android 16)

Every RedMagic Helper 3.4.6 hook target checked against jadx output of the APKs pulled from the phone on 2026-10-19.
OK = class and method exist with that name. OBFUSCATED = class exists, method name is stripped (candidates listed; resolve by signature). MISSING = not in this firmware.

Implemented in RM Tweaks v0.1 (branch `rm-tweaks`): Game Space batch (unfreeze, macros, combo speed, TGK, SR+Diablo, dialogs, RedMagic Time, AI speed, record), Launcher (paged drawer, config flags), Theme Store variant, fingerprint unlocks, System UI charging set / custom frames / replay on wake. Everything else below is verified but not yet wired.

## cn.nubia.gameassist

| class | method | params | status |
|---|---|---|---|
| `cn.nubia.gameassist.dessert.policy.ActiveModeController` | `removeActiveModeSharedPreKey` | `k.b0(new Object[]{String.class})` | OBFUSCATED? candidates: d(String str) | e(String str) | h(String str) | if(str != null)… |
| `cn.nubia.gameassist.dessert.policy.ActiveModeController` | `resetActiveModeSharedPreAllKey` | `` | OBFUSCATED? candidates: b() | c() | f() | g() | l() | p() | q() | run() | w() |
| `cn.nubia.gameassist.panel.GameAssistWindowManager` | `lambda$hideWindow$9` | `` | OBFUSCATED? candidates: A() | B0() | C0() | G0() | K() | N() | P() | Q() | R() | S() | … |
| `cn.nubia.gameassist.performance.PerformanceModeController` | `getCurrentPkg` | `` | OBFUSCATED? candidates: A() | H0() | J0() | S() | T() | X() | Y() | Z() | a0() | run() … |
| `cn.nubia.gameassist.performance.PerformanceModeController` | `getInt` | `k.b0(new Object[]{ContentResolver.class,` | OK |
| `cn.nubia.gameassist.performance.PerformanceModeController` | `getPerformanceMode` | `k.b0(new Object[]{String.class})` | OK |
| `cn.nubia.gameassist.performance.PerformanceModeController` | `isOpenSuperResolution` | `` | OBFUSCATED? candidates: A() | H0() | J0() | S() | T() | X() | Y() | Z() | a0() | run() … |
| `cn.nubia.gameassist.performance.PerformanceModeController` | `setBiabloModeEnable` | `k.b0(new Object[]{String.class, cls2, cl` | OBFUSCATED? candidates: C0(final java.lang.String r3, final boolean r4, boolean r5) | V… |
| `cn.nubia.gameassist.performance.PerformanceViewController` | `getPerformanceMode` | `t.f4873d` | OK |
| `cn.nubia.gameassist.plugin.config.PluginConfig` | `getBoolean` | `k.b0(new Object[]{String.class, cls2})` | OBFUSCATED? candidates: a(String[] strArr, String str) | b(Context context, PrintWriter… |
| `cn.nubia.gameassist.plugin.config.PluginConfig` | `getBoolean` | `k.b0(new Object[]{String.class})` | OBFUSCATED? candidates: e(Context context) | f(Context context) | for(String str2 : arr… |
| `cn.nubia.gameassist.plugin.config.PluginConfig` | `isPluginEnable` | `k.b0(new Object[]{Context.class, String.` | OBFUSCATED? candidates: l(Context context, String str, String str2) |
| `cn.nubia.gameassist.plugin.config.PluginConfig` | `isPluginEnable` | `k.b0(new Object[]{Context.class, String.` | OBFUSCATED? candidates: a(String[] strArr, String str) | b(Context context, PrintWriter… |
| `cn.nubia.gameassist.plugin.panel.PluginTilesAdapter` | `lambda$onBindViewHolder$0` | `k.b0(new Object[]{h5, View.class})` | OBFUSCATED? candidates: A(PluginTilesHolder pluginTilesHolder, int i2) | C(ViewGroup vi… |
| `cn.nubia.gameassist.utils.Utils` | `initView` | `k.b0(new Object[]{f7797e.b().loadClass("` | OBFUSCATED? candidates: A(Context context) | C(Context context) | H(Context context) | … |
| `cn.nubia.gameassist.utils.Utils` | `isChickenMode` | `k.b0(new Object[]{Context.class})` | OBFUSCATED? candidates: A(Context context) | C(Context context) | H(Context context) | … |
| `cn.nubia.plugin.gameratio.GameRatioSettingsPanel` | `setData` | `k.b0(new Object[]{f7798f.b().loadClass("` | OK |
| `cn.nubia.plugin.superresolution.SuperResolutionViewController` | `L` | `k.b0(new Object[]{String.class, cls2})` | OK |
| `cn.nubia.plugin.superresolution.SuperResolutionViewController` | `updateEnableSwitchPkg` | `k.b0(new Object[]{String.class, cls2})` | OBFUSCATED? candidates: H(String str, String str2) | L(String str, boolean z) | i(Strin… |

## cn.nubia.gamelauncher

| class | method | params | status |
|---|---|---|---|
| `cn.nubia.gamelauncher.gamecontrolpanel.GameFunctionAllocationView` | `disableRedMagicTime` | `` | OK |
| `cn.nubia.gamelauncher.gamecontrolpanel.GameFunctionAllocationView` | `enableRedMagicTime` | `` | OK |
| `cn.nubia.gamelauncher.gamecontrolpanel.GameFunctionAllocationView` | `initView` | `tVar` | OK |
| `cn.nubia.gamelauncher.gamecontrolpanel.GameFunctionAllocationView` | `setChoiceGameSettings` | `k.b0(new Object[]{cls})` | OK |
| `cn.nubia.gamelauncher.gamecontrolpanel.GameFunctionAllocationView` | `updateUI` | `k.b0(new Object[]{cls})` | OK |
| `cn.nubia.gamelauncher.gamecontrolpanel.GameFunctionAllocationView$10` | `onClick` | `k.b0(new Object[]{View.class})` | INNER-CLASS-MISSING |
| `cn.nubia.gamelauncher.gamecontrolpanel.config.PluginConfig` | `getBlackList` | `k.b0(new Object[]{Context.class, String.` | OK |
| `cn.nubia.gamelauncher.gamecontrolpanel.config.PluginConfig` | `getBoolean` | `k.b0(new Object[]{String.class, cls})` | METHOD-MISSING |
| `cn.nubia.gamelauncher.gamecontrolpanel.config.PluginConfig` | `getBoolean` | `k.b0(new Object[]{String.class})` | OBFUSCATED? candidates: for(String str2 : strArr) | if(c == '.') | if(c >= '0' && c <= … |
| `cn.nubia.neostore.model.ConfigMgrImp` | `H0` | `t.f4873d` | METHOD-MISSING |
| `cn.nubia.neostore.service.DownloadService` | `f` | `k.b0(new Object[]{cls})` | METHOD-MISSING |
| `cn.nubia.tgk.TgkHelper` | `disableTgkFunction` | `k.b0(new Object[]{String.class})` | OK |
| `cn.nubia.tgk.TgkHelper` | `getTgkDisableOpt` | `k.b0(new Object[]{ContentResolver.class,` | OK |
| `cn.nubia.tgk.TgkMapView` | `getGameKeyLinkMotionState` | `tVar` | OK |

## cn.nubia.gamehelpmodule

| class | method | params | status |
|---|---|---|---|
| `cn.nubia.gamehelper.MacroSwitch` | `isSpeedModeEnable` | `` | OK |
| `cn.nubia.gamehelper.bean.RecordMotionBean` | `getSpeed` | `` | OK |
| `cn.nubia.gamehelper.db.DbHelper` | `isPackageCanUseMacro` | `k.b0(new Object[]{String.class})` | OK |
| `cn.nubia.gamehelper.db.GameTouchProvider` | `call` | `k.b0(new Object[]{String.class, String.c` | OK |
| `cn.nubia.gamehelper.db.GameTouchProvider` | `query` | `` | OK |
| `cn.nubia.gamehelper.db.GameTouchProvider` | `recordMotionCall` | `k.b0(new Object[]{Bundle.class})` | OK |
| `cn.nubia.gamehelper.manager.GameMotionHelperManager` | `canDrawOverlays` | `k.b0(new Object[]{Context.class})` | OBFUSCATED? candidates: if(context != null) | if(hashMap == null) | if(i != 2 || this.b… |
| `cn.nubia.gamehelper.manager.GameMotionHelperManager` | `canUseMacro` | `` | OK |
| `cn.nubia.gamehelper.manager.GameMotionHelperManager` | `notifyChange` | `k.b0(new Object[]{String.class})` | OK |
| `cn.nubia.gamehelper.manager.GameMotionHelperManager` | `recoveryMotion` | `k.b0(new Object[]{String.class})` | OK |
| `cn.nubia.gamehelper.travel.action.TravelAction` | `playScript` | `t.f4873d` | OK |
| `cn.nubia.gamehelper.utils.DeviceUtil` | `isUI85` | `` | OK |
| `cn.nubia.gamehelper.utils.PackageUtils` | `findViewById` | `k.b0(new Object[]{Integer.TYPE})` | OBFUSCATED? candidates: for(Bundle bundle : list) | if(activityManager == null) | if(pa… |
| `cn.nubia.gamehelper.utils.PackageUtils` | `getBlackPackageSet` | `` | OK |
| `cn.nubia.gamehelper.utils.PackageUtils` | `getBoolean` | `k.b0(new Object[]{String.class, Boolean.` | METHOD-MISSING |
| `cn.nubia.gamehelper.utils.PackageUtils` | `getBoolean` | `k.b0(new Object[]{String.class})` | OBFUSCATED? candidates: for(Bundle bundle : list) | if(activityManager == null) | if(pa… |
| `cn.nubia.gamehelper.utils.PackageUtils` | `isBlackPackageName` | `k.b0(new Object[]{Integer.TYPE})` | OK |

## cn.nubia.gamelab

| class | method | params | status |
|---|---|---|---|
| `cn.nubia.gamelab.toy.SGameToy` | `scheduleDetectRunnable` | `tVar` | OK |
| `cn.nubia.gamelab.toy.SGameToy` | `startDetectBitmap` | `tVar` | OK |

## cn.nubia.gamehighlights

| class | method | params | status |
|---|---|---|---|
| `cn.nubia.gamehighlights.Activity.MainActivity$g$a` | `c` | `k.b0(new Object[]{Integer.TYPE})` | OK |

## com.zte.beautify

| class | method | params | status |
|---|---|---|---|
| `com.zte.beautify.model.local.data.Bean` | `getIsCharge` | `tVar` | OK |
| `com.zte.beautify.model.remote.zteaccount.ZteAccountManager` | `aodDownload` | `tVar` | OBFUSCATED? candidates: for(byte b : digest) | for(int i = 0; i < 5; i++) | if(!z) | if… |
| `com.zte.beautify.model.remote.zteaccount.ZteAccountManager` | `themeDownload` | `k.b0(new Object[]{cls})` | OBFUSCATED? candidates: for(byte b : digest) | for(int i = 0; i < 5; i++) | if(!z) | if… |
| `com.zte.beautify.view.common.preview.BeautyPreviewActivity$OnlineResourceDownloadListener` | `ResourceDownload` | `k.b0(new Object[]{cls})` | OK |
| `com.zte.beautify.view.common.tools.Utilities` | `getStringSystemProperties` | `k.b0(new Object[]{String.class})` | OK |
| `com.zte.beautify.view.common.tools.Utilities` | `getThemeResourceFeature` | `t.f4873d` | OK |

## com.zte.fingerprints

| class | method | params | status |
|---|---|---|---|
| `com.zte.fingerprint.theme.FingerprintAnimProcess` | `getAllFpAnim` | `` | OK |
| `com.zte.fingerprint.theme.FingerprintAnimProcess` | `isCustomizeForFighting` | `tVar` | OK |
| `com.zte.fingerprint.theme.FingerprintAnimProcess` | `isCustomizeForWutheringWaves` | `tVar` | OK |
| `com.zte.fingerprint.theme.FingerprintAnimProcess` | `isCustomizeForWutheringWaves809J` | `tVar` | OBFUSCATED? candidates: if(isRedMagicPhone) | if(isUnderScreenUltrasonicFingerPrint) | … |

## com.fingerprint.sensorservice

| class | method | params | status |
|---|---|---|---|
| `com.fingerprint.sensorservice.utils.Utils` | `isCustomizeForFighting` | `tVar` | OBFUSCATED? candidates: for(int i = 1; i < 128; i++) | if(mScreenWidth == 0 && mScreenH… |
| `com.fingerprint.sensorservice.utils.Utils` | `isCustomizeForWutheringWaves` | `tVar` | OBFUSCATED? candidates: for(int i = 1; i < 128; i++) | if(mScreenWidth == 0 && mScreenH… |

## launcher

| class | method | params | status |
|---|---|---|---|
| `com.android.quickstep.util.RecentsOrientedState` | `update` | `k.b0(new Object[]{cls, cls})` | OK |
| `com.android.quickstep.views.OverviewActionsView` | `onFinishInflate` | `` | OK |

## com.android.systemui

| class | method | params | status |
|---|---|---|---|
| `com.android.systemui.SystemUIApplication` | `onBatteryLevelChanged` | `k.b0(new Object[]{cls, cls2, cls2, cls2,` | METHOD-MISSING |
| `com.android.systemui.SystemUIApplication` | `updateBattery` | `` | OBFUSCATED? candidates: run() |
| `com.android.systemui.clipboardoverlay.ClipboardListener` | `forceSuppressOverlay` | `` | OK |
| `com.android.systemui.keyguard.KeyguardViewMediator` | `onStartedWakingUp` | `` | OK |
| `com.android.systemui.statusbar.KeyguardIndicationController` | `setIndicationArea` | `k.b0(new Object[]{ViewGroup.class})` | OK |
| `com.android.systemui.statusbar.StatusBarIconView` | `isIconBlocked` | `` | OK |
| `com.android.systemui.statusbar.phone.CentralSurfacesImpl` | `makeStatusBarView` | `` | OK |
| `com.android.systemui.statusbar.phone.KeyguardStatusBarView` | `onFinishInflate` | `` | OK |
| `com.android.systemui.statusbar.phone.NotificationIconContainer` | `initResources` | `M3.t.f4873d` | OK |
| `com.android.systemui.statusbar.phone.NotificationIconContainer` | `shouldForceOverflow` | `M3.k.b0(new Object[]{cls, cls, Float.TYP` | OK |
| `com.android.systemui.statusbar.phone.PhoneStatusBarView` | `onDetachedFromWindow` | `` | OK |
| `com.android.systemui.statusbar.phone.PhoneStatusBarView` | `onFinishInflate` | `` | OK |
| `com.android.systemui.statusbar.phone.PhoneStatusBarView` | `updateStatusBarHeight` | `M3.k.b0(new Object[]{Context.class})` | OK |
| `com.android.systemui.statusbar.phone.PhoneStatusBarView` | `updateSystemIconsContainerHeight` | `` | OK |
| `com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment` | `onViewCreated` | `` | OK |
| `com.android.systemui.statusbar.phone.ui.IconManager` | `addHolder` | `M3.k.b0(new Object[]{cls, String.class, ` | OK |
| `com.android.systemui.statusbar.phone.ui.IconManager` | `addView` | `M3.k.b0(new Object[]{View.class, cls, Vi` | OK |
| `com.android.systemui.statusbar.phone.ui.IconManager` | `setVisibility` | `M3.k.b0(new Object[]{cls})` | OBFUSCATED? candidates: if(childAt instanceof StatusBarNetSpeedMFV) | if(demoStatusIcon… |
| `com.android.systemui.statusbar.policy.Clock` | `updateClock` | `` | OK |
| `com.android.systemui.statusbar.policy.Clock` | `updateShowSeconds` | `` | OK |
| `com.android.systemui.usb.UsbDebuggingActivity` | `onCreate` | `M3.k.b0(new Object[]{Bundle.class})` | OK |
| `com.android.systemui.volume.VolumeDialogControllerImpl` | `updateStreamLevelW` | `` | OK |
| `com.android.systemui.volume.VolumeDialogImpl` | `showSafetyWarningH` | `` | OK |
| `com.android.systemui.volume.VolumeDialogImpl` | `updateVolumeRowH` | `` | OK |
| `com.zte.adapt.mifavor.volume.VolumeDialogImplAdapt` | `richTapVibrateForVolumeKeyLongPress` | `` | OK |
| `com.zte.controlcenter.view.ControlCenterTileLayout` | `onMeasure` | `k.b0(new Object[]{cls, cls})` | OK |
| `com.zte.controlcenter.widget.CCHeaderView` | `handleClickDate` | `` | OK |
| `com.zte.controlcenter.widget.CCHeaderView` | `handleClickSearch` | `` | OK |
| `com.zte.controlcenter.widget.CCHeaderView` | `onFinishInflate` | `` | OK |
| `com.zte.controlcenter.widget.CCHeaderView` | `postStartActivityDismissingKeyguard` | `k.b0(new Object[]{Intent.class})` | OK |
| `com.zte.controlcenter.widget.CCHeaderView` | `shouldQsCarrierVisible` | `` | OK |
| `com.zte.controlcenter.widget.CCHeaderView` | `showSearchButton` | `` | OK |
| `com.zte.feature.charging.ChargeUtils` | `getChargeBitmapByIndex` | `k.b0(new Object[]{cls2, Resources.class,` | OK |
| `com.zte.feature.charging.ChargeUtils` | `isRedmagicConfigCustom` | `t.f4873d` | OK |
| `com.zte.feature.charging.ChargingFeature` | `hideChargingViewDelayed` | `` | OK |
| `com.zte.feature.charging.ChargingFeature` | `startChargingAnimation` | `k.b0(new Object[]{Long.TYPE})` | OK |
| `com.zte.feature.doze.AodClock.AodClockStyle.AodClockStyleHorizen` | `onFinishInflate` | `tVar` | OK |
| `com.zte.feature.doze.AodClock.AodClockStyle.MyAodStyleModel$StyleData` | `isShowDateTime` | `t.f4873d` | OK |
| `com.zte.feature.fake.FakeNotchFeature` | `setVisibility` | `M3.k.b0(new Object[]{cls})` | OK |
| `com.zte.feature.fake.FakeNotchFeature` | `updateBlackVisibility` | `M3.k.b0(new Object[]{cls2, cls2, cls2})` | OK |
| `com.zte.feature.notification.NotificationUtil` | `getAppIconBackgroundDrawable` | `M3.k.b0(new Object[]{String.class, cls2}` | OK |
| `com.zte.feature.notification.NotificationUtil` | `shouldShowAppIcon` | `M3.k.b0(new Object[]{ApplicationInfo.cla` | OK |
| `com.zte.feature.notification.module.NotificationBackground` | `adjustNotificationIcon` | `M3.k.b0(new Object[]{View.class})` | OBFUSCATED? candidates: if(attachedChildren == null) | if(drawable != null) | if(this.m… |
| `com.zte.feature.qs.layout.QsCustomizerModule` | `getColumns` | `` | OK |
| `com.zte.feature.qs.layout.QsCustomizerModule` | `getRows` | `k.b0(new Object[]{cls})` | OBFUSCATED? candidates: if(cCCustomizerController != null) | if(cCCustomizerController … |
| `com.zte.feature.qs.layout.QsCustomizerModule` | `getTileColumns` | `k.b0(new Object[]{cls})` | OK |
| `com.zte.feature.qs.layout.QsCustomizerModule` | `updateMaxRows` | `k.b0(new Object[]{cls, cls})` | METHOD-MISSING |
| `com.zte.feature.signal.WifiUtils$Companion` | `getWifiSignalStrengthIconId` | `M3.k.b0(new Object[]{cls2, cls, cls, cls` | OK |
| `com.zte.feature.speed.SpeedControllerImpl` | `updateNetSpeed` | `` | OK |
| `com.zte.feature.speed.StatusBarNetSpeedMFV` | `init` | `` | OK |
| `com.zte.feature.speed.StatusBarNetSpeedPolicy$NetSpeedState` | `updateNetSpeedDisplay` | `M3.k.b0(new Object[]{h15})` | OK |
| `com.zte.feature.volume.MfvVolumeDialog` | `shouldKeyguardHandleVolumeKeys` | `` | OK |
| `com.zte.mifavor.keyguard.settings.LockScreenClockDefault` | `updateClockFont` | `k.b0(new Object[]{c1111a.b().loadClass("` | OK |
| `com.zte.mifavor.views.MFVBatteryViewLayout` | `getLevelDisplayMode` | `` | OK |
| `com.zte.mifavor.views.MFVBatteryViewLayout` | `onDarkChanged` | `k.b0(new Object[]{ArrayList.class, Float` | OK |
| `com.zte.mifavor.views.MFVBatteryViewLayout` | `onFinishInflate` | `` | OK |
| `com.zte.mifavor.views.MFVBatteryViewLayout` | `updateBatteryLayout` | `M3.k.b0(new Object[]{cls, Boolean.TYPE})` | OK |
| `com.zte.mifavor.views.MFVBatteryViewLayout` | `updateBatteryLevelText` | `` | OK |
| `com.zte.mifavor.views.MFVBatteryViewLayout` | `updateChargeViewColor` | `` | OK |
| `com.zte.mifavor.views.MFVBatteryViewLayout` | `updateVisibility` | `k.b0(new Object[]{Boolean.TYPE})` | OK |
| `com.zte.utils.QsDimenUtils$Companion` | `getTileColumns` | `` | OK |

## com.android.settings

| class | method | params | status |
|---|---|---|---|
| `com.android.settings.datetime.TimePreferenceController` | `getSummary` | `` | OK |
| `com.zte.settings.connecteddevice.UsbModeChooserActivity` | `onCreate` | `k.b0(new Object[]{Bundle.class})` | OK |
| `com.zte.settings.connecteddevice.UsbModeUtils` | `setCurrentMode` | `` | OK |
| `com.zte.settings.development.EnableAdbInstallPreferenceController` | `handlePreferenceTreeClick` | `` | OK |
| `com.zte.settings.development.EnableAdbInstallPreferenceController` | `launchAuthCompActivity` | `` | OK |
| `com.zte.settings.development.EnableAdbInstallPreferenceController` | `launchNubiaActivity` | `` | OK |
| `com.zte.settings.development.EnableAdbInstallPreferenceController` | `onActivityResult` | `` | OK |
| `com.zte.settings.notification.MfvBatteryPercentagePreferenceController` | `isAvailable` | `` | OK |

## com.android.packageinstaller

| class | method | params | status |
|---|---|---|---|
| `com.android.packageinstaller.InstallStaging$StagingAsyncTask` | `onPostExecute` | `` | OK |
| `com.android.packageinstaller.InstallStart` | `getCallingPackageNameForUid` | `k.b0(new Object[]{Integer.TYPE})` | OK |
| `com.android.packageinstaller.PackageInstallerActivity` | `getCookUI` | `k.b0(new Object[]{cls2, cls2, cls2, Bool` | OK |

## framework__services

| class | method | params | status |
|---|---|---|---|
| `com.android.server.SystemServiceManager` | `loadClassFromLoader` | `M3.k.b0(new Object[]{String.class, Class` | OK |
| `com.android.server.audio.AudioService` | `adjustStreamVolume` | `` | OK |
| `com.android.server.audio.AudioService` | `checkNoteAppOp` | `` | OK |
| `com.android.server.audio.AudioService` | `checkSafeMediaVolume` | `` | OK |
| `com.android.server.audio.AudioService` | `onPlaybackConfigChange` | `` | OK |
| `com.android.server.audio.AudioService` | `raiseVolumeDisplaySafeMediaVolume` | `` | OBFUSCATED? candidates: get() | run() |
| `com.android.server.audio.AudioService` | `setStreamVolume` | `` | OK |
| `com.android.server.audio.AudioService` | `volumeAdjustmentAllowedByDnd` | `` | OK |
| `com.android.server.audio.AudioService` | `willDisplayWarningAfterCheckVolume` | `` | OK |
| `com.android.server.datacollection.ZteTrackManager` | `init` | `M3.k.b0(new Object[]{Context.class})` | OK |
| `com.android.server.display.DisplayControl` | `createVirtualDisplay` | `` | OK |
| `com.android.server.display.VirtualDisplayAdapter` | `createVirtualDisplayLocked` | `` | OK |
| `com.android.server.locksettings.LockSettingsStrongAuth` | `rescheduleStrongAuthTimeoutAlarm` | `` | OK |
| `com.android.server.notification.NotificationAttentionHelper$Signals` | `buzzBeepBlinkLocked` | `M3.k.b0(new Object[]{h9, h10})` | OK |
| `com.android.server.notification.NotificationAttentionHelper$Signals` | `clearSoundLocked` | `` | OK |
| `com.android.server.pm.ResolveIntentHelper` | `isCtsTesting` | `` | METHOD-MISSING |
| `com.android.server.policy.PhoneWindowManager` | `powerLongPress` | `M3.k.b0(new Object[]{Long.TYPE})` | OK |
| `com.android.server.power.PowerManagerService` | `getScreenOffTimeoutLocked` | `M3.k.b0(new Object[]{cls3, cls3})` | OK |
| `com.android.server.power.ThermalManagerService` | `onTemperatureChanged` | `k.b0(new Object[]{"android.os.Temperatur` | OK |
| `com.android.server.wm.ActivityClientController` | `checkTaskSupportForCompWr` | `M3.k.b0(new Object[]{ComponentName.class` | OK |
| `com.android.server.wm.ActivityClientController` | `checkTaskSupportForPkgWr` | `M3.k.b0(new Object[]{String.class})` | OK |
| `com.android.server.wm.ActivityRecord` | `isSupportWindowReplyState` | `M3.k.b0(new Object[]{"com.android.server` | METHOD-MISSING |
| `com.android.server.wm.ActivityRecord` | `supportsFreeformInDisplayArea` | `M3.k.b0(new Object[]{h12})` | OK |
| `com.android.server.wm.ActivityRecord` | `supportsMultiWindow` | `tVar2` | OK |
| `com.android.server.wm.ActivityRecord` | `supportsMultiWindowInDisplayArea` | `M3.k.b0(new Object[]{h12})` | OK |
| `com.android.server.wm.ActivityTaskManagerService` | `getFreeformRootTasksVisibleListWrForMulti` | `tVar3` | OK |
| `com.android.server.wm.ActivityTaskManagerService` | `isReachWrMaxSizeForMulti` | `` | OK |
| `com.android.server.wm.ActivityTaskManagerService` | `registerScreenCaptureObserver` | `` | OK |
| `com.android.server.wm.ActivityTaskManagerService` | `windowReplySizeForMulti` | `tVar3` | OK |
| `com.android.server.wm.ActivityTaskSupervisor` | `startActivityFromRecents` | `M3.k.b0(new Object[]{cls, cls, cls, "com` | OK |
| `com.android.server.wm.AlertWindowNotification` | `onPostNotification` | `` | OK |
| `com.android.server.wm.AlertWindowNotification` | `post` | `` | OK |
| `com.android.server.wm.InputMonitor` | `getTouchOcclusionMode` | `` | OK |
| `com.android.server.wm.SafeActivityOptions` | `startActivityFromRecentsForWR` | `M3.k.b0(new Object[]{cls, cls, cls, cls,` | METHOD-MISSING |
| `com.android.server.wm.Task` | `isResizeable` | `M3.k.b0(new Object[]{"com.android.server` | OK |
| `com.android.server.wm.Task` | `isSupportResize` | `M3.k.b0(new Object[]{Context.class, Comp` | METHOD-MISSING |
| `com.android.server.wm.Task` | `isSupportWindowReplyState` | `M3.k.b0(new Object[]{"com.android.server` | OK |
| `com.android.server.wm.TaskDisplayAreaMifavor` | `alertMessageForReachMultiWrMaxSizeWr` | `M3.k.b0(new Object[]{"com.android.server` | OK |
| `com.android.server.wm.TaskDisplayAreaMifavor` | `checkTaskSupportForCompWr` | `M3.k.b0(new Object[]{Context.class, Comp` | OK |
| `com.android.server.wm.TaskDisplayAreaMifavor` | `checkTaskSupportWr` | `M3.k.b0(new Object[]{Context.class, "com` | OK |
| `com.android.server.wm.TaskNotSupportWindowReplyStateWr` | `isSupportWindowReplyStateForTask` | `M3.k.b0(new Object[]{"com.android.server` | METHOD-MISSING |
| `com.android.server.wm.WindowManagerService` | `registerScreenRecordingCallback` | `` | OK |
| `com.android.server.wm.WindowState` | `isSecureLocked` | `` | OK |

## framework__framework

| class | method | params | status |
|---|---|---|---|
| `android.app.Activity` | `onDestroy` | `` | OK |
| `android.app.WindowReplyUtils` | `isForceSupportWhiteListForWR` | `M3.k.b0(new Object[]{String.class})` | OK |
| `android.content.Context` | `getSmallTime` | `M3.t.f4873d` | OBFUSCATED? candidates: if(Build.IS_ENG) | if(sLastAutofillId == 1073741822) | if(syste… |
| `android.os.Handler` | `postDelayed` | `k.b0(new Object[]{Runnable.class, Long.T` | OK |
| `android.os.Handler` | `sendEmptyMessageDelayed` | `k.b0(new Object[]{cls, cls2})` | OK |
| `android.os.Handler` | `sendEmptyMessageDelayed` | `k.b0(new Object[]{cls, cls3})` | OK |
| `android.os.Handler` | `sendMessageDelayed` | `k.b0(new Object[]{Message.class, cls2})` | OK |
| `android.os.Handler` | `sendMessageDelayed` | `k.b0(new Object[]{Message.class, cls3})` | OK |
| `android.os.UpdateEngine` | `applyPayload` | `M3.k.b0(new Object[]{String.class, cls2,` | OK |
| `android.provider.Settings$System` | `putInt` | `k.b0(new Object[]{ContentResolver.class,` | OK |
| `android.util.apk.ApkSignatureVerifier` | `getMinimumSignatureSchemeVersionForTargetSdk` | `M3.k.b0(new Object[]{Integer.TYPE})` | OK |
| `android.view.SurfaceControl` | `createDisplay` | `` | OBFUSCATED? candidates: run() |
| `android.view.SurfaceControl` | `nativeSetFlags` | `M3.k.b0(new Object[]{cls, cls, cls2, cls` | OK |
| `android.window.ScreenCapture$CaptureArgs` | `nativeCaptureDisplay` | `` | OK |
| `android.window.ScreenCapture$CaptureArgs` | `nativeCaptureLayers` | `` | OK |
| `android.window.ScreenCapture$ScreenshotHardwareBuffer` | `containsSecureLayers` | `` | OK |
| `com.android.internal.policy.SystemBarUtils` | `getStatusBarHeight` | `M3.k.b0(new Object[]{Context.class})` | OK |

## (not decompiled)

| class | method | params | status |
|---|---|---|---|
| `android.app.role.ASSISTANT` | `performHapticFeedback` | `k.b0(new Object[]{cls, String.class})` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `android.app.role.ASSISTANT` | `showGlobalActions` | `tVar` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `android.window.ScreenCaptureInternal$CaptureArgs` | `nativeCaptureDisplay` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `android.window.ScreenCaptureInternal$CaptureArgs` | `nativeCaptureLayers` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `cn.nubia.filebrowser.mtpserver.mtp.MtpDatabase` | `addEmulatedStorage` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `cn.nubia.filebrowser.mtpserver.mtp.MtpServer` | `addStorage` | `k.b0(new Object[]{c0748b.b().loadClass("` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `cn.nubia.gameassist` | `getCheckedId` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `cn.nubia.gameassist` | `getPackageName` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `cn.nubia.gameassist` | `setData` | `k.b0(new Object[]{int[].class, int[].cla` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `cn.zte.gamefloat.gamekeys.entity.GameChickenModeDialog$ChickenModeDialog` | `show` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.nfc.NfcService` | `playSound` | `k.b0(new Object[]{Integer.TYPE})` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.nfc.NfcService` | `sendMessage` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.server.bluetooth.BluetoothManagerService` | `onAirplaneModeChanged` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.server.wifi.WifiCountryCode` | `setTelephonyCountryCode` | `M3.k.b0(new Object[]{String.class})` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.server.wifi.WifiNative` | `setApMacAddress` | `M3.k.b0(new Object[]{String.class, MacAd` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.server.wifi.WifiNative` | `setStaMacAddress` | `M3.k.b0(new Object[]{String.class, MacAd` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.server.wifi.WifiSettingsStore` | `isAirplaneSensitive` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.ztescreenshot.cropimage.CropImageService` | `onDestroy` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.ztescreenshot.cropimage.CropImageService` | `onStartCommand` | `k.b0(new Object[]{Intent.class, cls, cls` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.ztescreenshot.cropimage.CropImageService` | `saveAndExit` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.ztescreenshot.recordscreen.RecordScreenView` | `startRecord` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.android.ztescreenshot.recordscreen.RecordScreenView` | `stopRecord` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.baidu.input.clipboard.manager.BDClipboardManager` | `c` | `k.b0(new Object[]{String.class})` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.baidu.input.clipboard.manager.BDClipboardManager` | `filter` | `k.b0(new Object[]{CharSequence.class, cl` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.baidu.input.clipboard.panel.view.EditClipboardItemActivity` | `a` | `k.b0(new Object[]{String.class})` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.zte.cn.doubleapp.common.UpdateUtils` | `getSupportApps` | `` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.zte.cn.doubleapp.common.Utils` | `showLimitedApps` | `k.b0(new Object[]{Context.class})` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.zte.feature.doze` | `dozeTimeTick` | `tVar` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.zte.wr.WindowReplyIconManager` | `iconShowIsError` | `k.b0(new Object[]{cls})` | CLASS-NOT-DECOMPILED-OR-MISSING |
| `com.zte.wr.WindowReplyService` | `onStartCommand` | `k.b0(new Object[]{Intent.class, cls, cls` | CLASS-NOT-DECOMPILED-OR-MISSING |

## cn.nubia.fan,cn.nubia.gameassist,cn.nubia.gamelauncher,com.android.packageinstaller,com.android.settings,com.android.systemui,com.zte.beautify,com.zte.fingerprints,com.zte.game.plugintrigger

| class | method | params | status |
|---|---|---|---|
| `com.zte.mifavor.widget.TimePickerDialogZTE` | `updateTitle` | `k.b0(new Object[]{loadClass2, cls2, cls2` | OK |

## cn.nubia.fan,cn.nubia.gameassist,cn.nubia.gamelauncher,com.android.packageinstaller,com.android.settings,com.android.systemui,com.zte.beautify,com.zte.fingerprints,com.zte.game.plugintrigger,launcher

| class | method | params | status |
|---|---|---|---|
| `androidx.preference.TwoStatePreference` | `getPreferenceKey` | `` | OBFUSCATED? candidates: P0() | U0() | X() | f0() |
| `androidx.preference.TwoStatePreference` | `isChecked` | `` | OK |
| `androidx.preference.TwoStatePreference` | `setChecked` | `k.b0(new Object[]{Boolean.TYPE})` | OK |
| `com.zte.mifavor.widget.NumberPickerZTE` | `setDisplayedValues` | `k.b0(new Object[]{String[].class})` | OK |
| `com.zte.mifavor.widget.NumberPickerZTE` | `setMaxValue` | `k.b0(new Object[]{cls2})` | OK |
| `com.zte.mifavor.widget.TimePickerZTE` | `onTimeChanged` | `k.b0(new Object[]{c1006a.b().loadClass("` | OK |
| `com.zte.mifavor.widget.TimePickerZTE` | `onValueChange` | `k.b0(new Object[]{c1006a.b().loadClass("` | OK |
| `com.zte.mifavor.widget.TimePickerZTE` | `updateAmPmControl` | `t.f4873d` | OK |

## com.android.settings,com.android.systemui

| class | method | params | status |
|---|---|---|---|
| `com.android.wm.shell.onehanded.OneHandedTutorialHandler` | `getTutorialTargetLayoutParams` | `` | OK |
| `com.android.wm.shell.onehanded.OneHandedTutorialHandler` | `scheduleOffset` | `k.b0(new Object[]{cls, cls})` | METHOD-MISSING |

## com.zte.game.plugintrigger

| class | method | params | status |
|---|---|---|---|
| `com.zte.game.plugintrigger.policy.PolicyManager` | `getLegalRangeValue` | `k.b0(new Object[]{cls2, cls2, cls2, cls2` | OK |
| `com.zte.game.plugintrigger.policy.PolicyManager$PolicyIntervalHandler` | `sendMessageDelayed` | `k.b0(new Object[]{Message.class, cls2})` | OK |
