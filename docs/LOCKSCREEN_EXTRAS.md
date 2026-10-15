# Lock-screen widgets, fingerprint graphic and clock fonts — 0.1.31

Separate controls hide the weather, alarm and battery widgets below the clock. Only the inspected vendor widget classes beneath the live KeyguardStatusView are targeted. Chooser previews are excluded. Visibility restores to the latest vendor value through the common Restore/guard stop flow.

Fingerprint choices: Stock, Rings, Scan frame, Hexagon and Diamond, with a local preview. Hide fingerprint icon removes only the sensor ImageView's background graphic. Stock DEX identifies FloatWindowSensorView.sensorView and its setBackgroundResource calls; the owner is not the image's ancestor because this is a separate window. Discovery observes the vendor's initialization/status methods. The vendor feature must report keyguard UI, not AOD, and enrollment must be false. Neither window visibility, sensor touch listeners, authentication behavior, display brightness nor fingerprint animations are changed. View padding is preserved. Vendor background updates are retained for restoration. Only the static graphic is replaced; unsupported source signatures block activation.

Clock font picker: 26 named Android system font faces, grouped as Original, Modern, Compact, Classic, Technical and Expressive. Each row previews 12:34 using that face. Fonts apply only to captured TextView clock targets, including the Clip hour/minute/mask views and Default text clock. Dates and unrelated text retain their own fonts. Artword image digits retain their artwork. These are system font family/style selections, not 26 bundled font files; availability follows the device's system font mappings. Stock restores the latest vendor typeface. No font files are installed and no system settings are changed.

Draft and active configuration remain separate. Legacy 9/10/11-field surface settings migrate with stock defaults for all new controls. New settings have bounded widget/icon/font fields and use the existing identity-bound frozen snapshot.

Validation: production surface tests cover scoped widget hiding, fonts, latest vendor typeface updates through nested setters, dark-state suspension and apply/restore failures. Production fingerprint tests cover four glyph selections, background-only hiding, latest vendor background, enrollment/app/AOD exclusions, detach, replacement, padding/visibility/alpha preservation and failure fences. Preferences test frozen extra settings. Android unit tests cover migration/bounds and unique font names; full Android build and signing checks run in CI.

Phone checklist:

1. Install signed 0.1.31 and reboot to reload hooks; retain the existing recovery guard.
2. Hide each widget separately, Enable, lock/unlock and verify only that widget disappears. Restore stock and confirm all return.
3. Hide fingerprint icon, Enable, and test unlock at the same physical sensor location. Restore stock.
4. Test each fingerprint glyph; verify fingerprint unlock, app biometric prompts and AOD continue using their expected behavior. Enrollment UI should remain stock.
5. Choose several font categories, Enable, and inspect the live clock. Test Clip if using a text clock; image digits deliberately keep their art. Verify Stock and Restore stock recover vendor typography.
6. Check wake, clock-style changes, existing clock/date hides and both QS groups. Send technical status if a requested element has zero captures.

Do not merge until phone validation and explicit user authorization.


## 0.1.31 fixes awaiting phone validation

The fingerprint backend now also reads existing objects from the inspected Feature.sFeatures registry and FloatWindowManager.floatWindowManager singleton. This covers objects whose initialization callbacks ran before hook installation. It reads the existing floating sensor owner and direct keyguard image; it does not call factories, construct windows, or change sensor interaction. Status reports registry feature count and whether the existing floating owner was found.

Clock font selection preserves the vendor text size and includeFontPadding. It no longer fits text to transient layout bounds, which previously shrank the clock during wake. Custom size/offset controls remain independent: choose 100% and zero offsets to keep stock geometry. Different fonts still have different glyph proportions at the same vendor text size; Cursive clipping and wake appearance need phone confirmation.

Persistent enabled sessions retain their existing bounded health authority over screen sleep rather than restoring all styles and asynchronously applying them after screen-on. Vendor dark/pulsing callbacks still restore AOD targets. Sleep does not renew authority: broker validation, identity checks, expiry, explicit stop and independent guard revocation remain active. Temporary rehearsals still stop at screen-off.

Regression checks cover discovery without initialization callbacks, source/background restoration, font size/padding preservation even at 1-pixel animation bounds, vendor text-size updates, AOD restoration, and persistent sleep without deadline renewal. The runtime check also exercises expired authority, temporary sessions, surface failure and explicit-stop suppression.

Install signed 0.1.31 and reboot, then:

1. Leave lock-screen clock size at 100% and offsets at zero. Choose Cursive and Enable. Wake several times and compare size/position to Stock. There should be no bounds-driven shrinking or delayed stock-to-custom swap while the session remains healthy.
2. Select Hide fingerprint, Enable, lock and wake. Unlock using the same sensor position. Restore, then try Hexagon and Enable. If still unchanged, send expanded technical status including the new registry/floating discovery counts.
3. Check AOD and an app biometric prompt, Restore stock, and the independent guard Action. Confirm stock returns and a stopped style does not restart on wake.
