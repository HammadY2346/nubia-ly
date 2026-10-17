# Install nubia-ly

## Supported setup

- REDMAGIC 11 Pro **NX809J**, on the validated stock Android 16 firmware.
- Root access, **Vector**, and a compatible Zygisk environment already installed and working.
- The **nubia-ly Recovery Guard** module from this repository.

Root and framework installation depend on your existing setup. This app does not root the phone or install Vector. Boot animations additionally require the verified KernelSU setup and firmware-player checks; an unsupported boot-animation profile does not mean all other styling is unsupported. Other phones and custom ROMs are not currently supported.

## First installation

1. Open [GitHub Releases](https://github.com/austineyoung2000/nubia-ly/releases). Download `nubia-ly-v1.0.0.apk` and `nubia-ly-guard-v0.1.15.zip`. Both are required for the supported setup. GitHub's **Source code** archives are not root modules.
2. Install the APK using Android's package installer.
3. In your root manager, open **Modules**, install the downloaded guard ZIP and ensure it is enabled. Do not extract this module ZIP.
4. Open **Vector → Modules → nubia-ly**. Enable the module and retain its declared scopes: **System UI** (`com.android.systemui`) and **Fingerprint SensorService** (`com.fingerprint.sensorservice`). SensorService owns the separate fingerprint renderer on this firmware.
5. Reboot. Unlock the phone, open nubia-ly and approve its root request through your root manager.
6. Open **Setup & device** and check the runtime, root and guard status. If activation is missing, check Vector's module enablement/scopes and reboot before applying styles.
7. Select styles from the dashboard. Browsing cards saves a draft. Press **Enable customization** to apply the saved style choices across personalization areas.

For shared packs, use **Status bar → Icon packs**. Existing non-stock Wi-Fi/mobile-signal selections override the pack in the Status bar. Choosing Stock in those individual selectors lets the pack supply compatible strength icons. Unsupported resources and connectivity warnings retain native artwork.

After later reboots, saved **enabled** features resume in the background after the first unlock. You do not need to reopen the dashboard or press Enable on every boot. Editing a draft does not change the last enabled selection until you apply it again.

## Updating from a test build

Install the official signed APK over the existing installation to preserve settings. Version 1.0.0 uses version code 70 and the same persistent project signing key as the official signed test builds. Replace the guard only if its version changed or it is absent; this release uses guard 0.1.15. Reboot after updating to load the new runtime code.

If Android reports a signature conflict, check whether the installed app came from an ephemeral/unsigned fork build. Official and fork signing keys can differ; Android cannot update one with the other. Uninstalling removes local app settings, so do not uninstall simply to resolve an unknown installer error.

## Boot animations

Open **Boot animations**, check compatibility, choose artwork and press **Apply on next boot**, then reboot. A card selection alone saves a draft. The guard supplies the verified animation through a systemless file bind; it does not replace the original system archive.

Use **Restore stock on next boot** and reboot to undo the bind. The guard Action also disarms the next-boot choice. A bind already active for this boot lasts until reboot. Stock remains available when checks reject a custom animation.

## Restore and recovery

For ordinary restoration, press **Restore stock appearance**. **Emergency stop** revokes active customization. The root-manager **nubia-ly Recovery Guard → Action** offers an independent stop.

The same fixed stop command is available in Termux:

```sh
su -c 'sh /data/adb/modules/nubia_ly_guard/guardctl.sh stop'
```

If necessary, disable nubia-ly in Vector and disable its guard module in the root manager, then reboot. Boot-animation restoration requires a reboot. The guard's incomplete-boot animation rollback is conservative protection, not a guarantee of recovery from every boot failure.

## GitHub Actions test builds

Before a public release is published, or when testing a requested change, use the exact successful run linked by the developer. Download `nubia-ly-signed-test-apk` or `nubia-ly-signed-release-apk` and `nubia-ly-guard-test-module`. GitHub may require sign-in.

Extract these **outer artifact ZIPs**. Install the APK and flash only the **inner** `nubia-ly-guard-test.zip` through the root manager. Do not flash GitHub's outer artifact ZIP. Use the same scope/reboot steps as above.

## Report a problem

Open an [issue](https://github.com/austineyoung2000/nubia-ly/issues) with the app/guard versions, phone model, Android/firmware version, selected feature and steps to reproduce it. Include a screenshot of the feature and its expanded technical details when possible. Redact personal notifications and account details before sharing screenshots.
