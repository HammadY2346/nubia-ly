# Lock-screen widgets, fingerprint graphic and clock fonts — 0.1.32

Separate controls hide the weather, alarm and battery widgets below the clock. Only the inspected vendor widget classes beneath the live KeyguardStatusView are targeted. Chooser previews are excluded. Visibility restores to the latest vendor value through the common Restore/guard stop flow.

Fingerprint choices: Stock, Rings, Scan frame, Hexagon and Diamond, with a local preview. Hide fingerprint icon removes only the sensor ImageView's background graphic. Stock DEX identifies FloatWindowSensorView.sensorView and its setBackgroundResource calls; the owner is not the image's ancestor because this is a separate window. Discovery observes the vendor's initialization/status methods. The vendor feature must report keyguard UI, not AOD, and enrollment must be false. Neither window visibility, sensor touch listeners, authentication behavior, display brightness nor fingerprint animations are changed. View padding is preserved. Vendor background updates are retained for restoration. Only the static graphic is replaced; unsupported source signatures block activation.

Clock font picker: 26 named Android system font faces, grouped as Original, Modern, Compact, Classic, Technical and Expressive. Each row previews 12:34 using that face. Fonts apply only to captured TextView clock targets, including the Clip hour/minute/mask views and Default text clock. Dates and unrelated text retain their own fonts. Artword image digits retain their artwork. These are system font family/style selections, not 26 bundled font files; availability follows the device's system font mappings. Stock restores the latest vendor typeface. No font files are installed and no system settings are changed.

Draft and active configuration remain separate. Legacy 9/10/11-field surface settings migrate with stock defaults for all new controls. New settings have bounded widget/icon/font fields and use the existing identity-bound frozen snapshot.

Validation: production surface tests cover scoped widget hiding, fonts, latest vendor typeface updates through nested setters, dark-state suspension and apply/restore failures. Production fingerprint tests cover four glyph selections, background-only hiding, latest vendor background, enrollment/app/AOD exclusions, detach, replacement, padding/visibility/alpha preservation and failure fences. Preferences test frozen extra settings. Android unit tests cover migration/bounds and unique font names; full Android build and signing checks run in CI.

Phone checklist:

1. Install signed 0.1.32 and reboot to reload hooks; retain the existing recovery guard.
2. Hide each widget separately, Enable, lock/unlock and verify only that widget disappears. Restore stock and confirm all return.
3. Hide fingerprint icon, Enable, and test unlock at the same physical sensor location. Restore stock.
4. Test each fingerprint glyph; verify fingerprint unlock, app biometric prompts and AOD continue using their expected behavior. Enrollment UI should remain stock.
5. Choose several font categories, Enable, and inspect the live clock. Test Clip if using a text clock; image digits deliberately keep their art. Verify Stock and Restore stock recover vendor typography.
6. Check wake, clock-style changes, existing clock/date hides and both QS groups. Send technical status if a requested element has zero captures.

Do not merge until phone validation and explicit user authorization.


## 0.1.32 fixes awaiting phone validation

Clock fonts fit within the clock's stock content allocation captured before font application. Full font metrics and padding include lower strokes; the selected size never exceeds the latest vendor text size. Later animation bounds do not replace the captured fit area, and refresh always calculates from vendor size rather than the previous fitted result. Widest-digit advances avoid minute-to-minute sizing changes. Parent layout dimensions, widget positions and security state remain untouched. Stock/Restore returns the latest vendor typeface, text size and padding. Technical details report the captured area and fitted pixel size.

Persistent enabled sessions retain existing bounded health authority over sleep. Sleep does not renew authority: broker validation, identity checks, expiry, explicit stop and independent guard revocation remain active. Temporary rehearsals still stop at screen-off.

The stock System UI source also binds a separate com.fingerprint.sensorservice.UdfpsMessageService. The in-process backend now leaves graphics unchanged when that external renderer is connected, and reports that its appearance path is unsupported. Legacy images are restored if external ownership becomes active. There are no SensorService calls or window/sensor-state changes. The separate APK is required to inspect its rendering path before implementing support. This build does not claim to fix external-renderer fingerprint styles.

Regression checks cover legacy/direct image restoration and exclusions, external delegation without graphic writes, restoration when legacy ownership transfers to the external service, fixed-area font fitting across transient bounds, latest vendor updates, AOD restoration, failure rollback, and sleep without authority renewal.

Install signed 0.1.32 and reboot. Keep fingerprint Stock while external-renderer support is investigated. Set independent lock-screen scale to 100% and offsets to zero, choose Cursive, Enable, and check that lower strokes fit the clock area across wake. Verify Stock/Restore, AOD and guard Action. Report the Font fit dimensions and pixel sizes if clipping remains. Phone validation and explicit merge authorization are still pending.
