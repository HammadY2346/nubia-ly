# Lock-screen widgets, fingerprint graphic and clock fonts — 0.1.30

Separate controls hide the weather, alarm and battery widgets below the clock. Only the inspected vendor widget classes beneath the live KeyguardStatusView are targeted. Chooser previews are excluded. Visibility restores to the latest vendor value through the common Restore/guard stop flow.

Fingerprint choices: Stock, Rings, Scan frame, Hexagon and Diamond, with a local preview. Hide fingerprint icon removes only the sensor ImageView's background graphic. Stock DEX identifies FloatWindowSensorView.sensorView and its setBackgroundResource calls; the owner is not the image's ancestor because this is a separate window. Discovery observes the vendor's initialization/status methods. The vendor feature must report keyguard UI, not AOD, and enrollment must be false. Neither window visibility, sensor touch listeners, authentication behavior, display brightness nor fingerprint animations are changed. View padding is preserved. Vendor background updates are retained for restoration. Only the static graphic is replaced; unsupported source signatures block activation.

Clock font picker: 26 named Android system font faces, grouped as Original, Modern, Compact, Classic, Technical and Expressive. Each row previews 12:34 using that face. Fonts apply only to captured TextView clock targets, including the Clip hour/minute/mask views and Default text clock. Dates and unrelated text retain their own fonts. Artword image digits retain their artwork. These are system font family/style selections, not 26 bundled font files; availability follows the device's system font mappings. Stock restores the latest vendor typeface. No font files are installed and no system settings are changed.

Draft and active configuration remain separate. Legacy 9/10/11-field surface settings migrate with stock defaults for all new controls. New settings have bounded widget/icon/font fields and use the existing identity-bound frozen snapshot.

Validation: production surface tests cover scoped widget hiding, fonts, latest vendor typeface updates through nested setters, dark-state suspension and apply/restore failures. Production fingerprint tests cover four glyph selections, background-only hiding, latest vendor background, enrollment/app/AOD exclusions, detach, replacement, padding/visibility/alpha preservation and failure fences. Preferences test frozen extra settings. Android unit tests cover migration/bounds and unique font names; full Android build and signing checks run in CI.

Phone checklist:

1. Install signed 0.1.30 and reboot to reload hooks; retain the existing recovery guard.
2. Hide each widget separately, Enable, lock/unlock and verify only that widget disappears. Restore stock and confirm all return.
3. Hide fingerprint icon, Enable, and test unlock at the same physical sensor location. Restore stock.
4. Test each fingerprint glyph; verify fingerprint unlock, app biometric prompts and AOD continue using their expected behavior. Enrollment UI should remain stock.
5. Choose several font categories, Enable, and inspect the live clock. Test Clip if using a text clock; image digits deliberately keep their art. Verify Stock and Restore stock recover vendor typography.
6. Check wake, clock-style changes, existing clock/date hides and both QS groups. Send technical status if a requested element has zero captures.

Do not merge until phone validation and explicit user authorization.


## Phone follow-up

0.1.29 phone status showed zero fingerprint captures and visible font clipping with Cursive on LockScreenClockDefault. The static lock-screen image is FingerprintUnderLcdFeature.mFingerprintIcon, populated by tryInflateFingerprintIcon; it is separate from FloatWindowSensorView.sensorView. Stock refreshFillLightLayoutFingerColor updates both its background and image resource.

0.1.30 observes that direct image path, tracks source and background separately, and observes both setImageDrawable and setImageResource. Only the graphic changes; source backgrounds/images are retained through nested updates for restoration. Direct targeting requires keyguard showing and an interactive device, and excludes the vendor AOD flag. The existing floating sensor path remains available. Technical status includes observed owner count.

Font overrides now enable font padding and fit the selected face to the clock's available content height/width using full font metrics and text advance. Font size never grows beyond the latest vendor size, and repeated refresh does not compound shrinking. Vendor text-size and includeFontPadding updates are tracked and restored alongside the typeface. No parent clipping, widgets or sensor hit regions are changed.

Regression tests add direct lock-screen source/background restoration, nested resource updates, interactive/keyguard gates, font-fitting across bound changes, non-compounding refresh and latest size/padding restoration. Phone validation remains required, especially Cursive's lower strokes and fingerprint hiding/replacement while unlocking.
