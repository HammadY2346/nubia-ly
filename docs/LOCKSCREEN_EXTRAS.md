# Lock-screen widgets, fingerprint graphic and clock fonts — 0.1.33

Separate controls hide the weather, alarm and battery widgets below the clock. Only the inspected vendor widget classes beneath the live KeyguardStatusView are targeted. Chooser previews are excluded. Visibility restores to the latest vendor value through the common Restore/guard stop flow.

Fingerprint choices: Stock, Rings, Scan frame, Hexagon and Diamond, with a local preview. Hide fingerprint icon removes only the sensor ImageView's background graphic. Stock DEX identifies FloatWindowSensorView.sensorView and its setBackgroundResource calls; the owner is not the image's ancestor because this is a separate window. Discovery observes the vendor's initialization/status methods. The vendor feature must report keyguard UI, not AOD, and enrollment must be false. Neither window visibility, sensor touch listeners, authentication behavior, display brightness nor fingerprint animations are changed. View padding is preserved. Vendor background updates are retained for restoration. Only the static graphic is replaced; unsupported source signatures block activation.

Clock font picker: 26 named Android system font faces, grouped as Original, Modern, Compact, Classic, Technical and Expressive. Each row previews 12:34 using that face. Fonts apply only to captured TextView clock targets, including the Clip hour/minute/mask views and Default text clock. Dates and unrelated text retain their own fonts. Artword image digits retain their artwork. These are system font family/style selections, not 26 bundled font files; availability follows the device's system font mappings. Stock restores the latest vendor typeface. No font files are installed and no system settings are changed.

Draft and active configuration remain separate. Legacy 9/10/11-field surface settings migrate with stock defaults for all new controls. New settings have bounded widget/icon/font fields and use the existing identity-bound frozen snapshot.

Validation: production surface tests cover scoped widget hiding, fonts, latest vendor typeface updates through nested setters, dark-state suspension and apply/restore failures. Production fingerprint tests cover four glyph selections, background-only hiding, latest vendor background, enrollment/app/AOD exclusions, detach, replacement, padding/visibility/alpha preservation and failure fences. Preferences test frozen extra settings. Android unit tests cover migration/bounds and unique font names; full Android build and signing checks run in CI.

Phone checklist:

1. Install signed 0.1.33 and reboot to reload hooks; retain the existing recovery guard.
2. Hide each widget separately, Enable, lock/unlock and verify only that widget disappears. Restore stock and confirm all return.
3. Hide fingerprint icon, Enable, and test unlock at the same physical sensor location. Restore stock.
4. Test each fingerprint glyph; verify fingerprint unlock, app biometric prompts and AOD continue using their expected behavior. Enrollment UI should remain stock.
5. Choose several font categories, Enable, and inspect the live clock. Test Clip if using a text clock; image digits deliberately keep their art. Verify Stock and Restore stock recover vendor typography.
6. Check wake, clock-style changes, existing clock/date hides and both QS groups. Send technical status if a requested element has zero captures.

Do not merge until phone validation and explicit user authorization.


## 0.1.33 SensorService support awaiting phone validation

The uploaded FingerprintSensorUi_ZTE APK owns the normal Redmagic fingerprint graphic in com.fingerprint.sensorservice.view.FloatWindowSensorView.mSensorView. It draws the idle graphic as an ImageView background; press animations, illumination and sensor windows are separate. The new backend runs inside com.fingerprint.sensorservice, while the existing System UI backend leaves externally owned images untouched.

Scope is the existing manager's System UI-created sensor window while keyguard is showing, power is interactive, doze is false, quick-app mode is false, fingerprint-disabled is false, and the vendor state machine is INITIALIZED. Stock source transitions show that INITIALIZED is the idle visible state; READY is part of AOD press preparation and is excluded. Before the state machine processes a press, any custom background is restored, and customization stays suspended through authentication and success. Only the mSensorView background changes: no image/window visibility, touch listener, hit region, driver, illumination, biometric result or vendor binder command is modified. Latest stock background and padding are retained for restoration; null stock backgrounds never receive an invented replacement.

SensorService independently calls a fixed fingerprint-only broker method. The provider checks Binder UID and verified package attribution for com.fingerprint.sensorservice and returns only frozen fingerprint choices, permit identity and a bounded health deadline. SensorService cannot call System UI's broker methods. Independent per-process startup counts retain the restart cutoff without consuming System UI's counter. Explicit Enable resets both counters. Root-guard stop, app emergency stop, broker loss, stale/version-invalid replies and deadline expiry restore stock. A busy broker retains only the existing deadline; late workers cannot reapply after revocation.

Vector scope now includes both System UI and FingerprintSensorUi. The editor has a signature-protected Check fingerprint service diagnostic; broadcasts do not carry appearance authority. The service obtains saved active choices from the broker, not from broadcast extras. It rechecks every three seconds and on wake. Diagnostic checks also request a fresh read.

The 0.1.32 stable clock-area fitting is retained: full font metrics and padding fit within the content allocation captured before font application, without chasing later animation bounds. Parent layout dimensions and widget positions remain unchanged. Stock/Restore recovers latest vendor size, typeface and padding.

Local regression tests cover the production sensor backend, runtime, broker and diagnostic client, including hide/replacement, all four glyphs, nested stock writes, pre-press restoration, AOD/app/quick-app/disabled exclusions, detach/replacement, expiry, service teardown, failure rollback, shared-UID package attribution, method isolation, narrow responses, independent startup counts, late-worker fencing and cancelled diagnostics. Android build/signature checks run in CI. Device validation is pending.

Phone checklist:

1. Install signed 0.1.33. In Vector, select System UI and FingerprintSensorUi / com.fingerprint.sensorservice for nubia-ly. Reboot once after scope selection. Keep the existing recovery guard.
2. Leave Hide fingerprint off. Choose Rings, press Enable customization, then Check fingerprint service. It should report a readable guard and observed service; captures may remain zero while unlocked.
3. Lock and wake the phone: Rings should replace the idle graphic. Unlock at the same physical location. Return and Check fingerprint service to inspect captured/restored counts.
4. Try Hexagon and another shape. Then enable Hide, Enable customization, and verify the graphic disappears while sensor unlock still works. Uncheck Hide, choose a shape and Enable again: the shape must return.
5. Verify stock press animation, AOD, an app biometric prompt and existing widgets/QS/clock controls. Restore stock and test guard Action or emergency stop once. No stopped style should resume on wake.
6. Check Cursive lower strokes and stable fit with independent lock-screen scale at 100% and offsets zero. Send the diagnostic text if either clock or fingerprint graphics differ from the expected result.

Keep the PR draft and unmerged until phone validation and explicit authorization.
