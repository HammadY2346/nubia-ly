# Fingerprint animation batch — 0.1.37

## Delivery status

Implemented on `wip/udfps-animations-handoff`, based on merged main
`b48d4f5b8a5950b9242e5a550e8ab35bf6129690` (PR #17).
Version name `0.1.37-udfps-animations`, version code 38.
Host checks pass. Android compilation, Robolectric tests, signature verification,
and phone validation remain pending. Do not describe this batch as released or
phone verified. Stable main remains 0.1.36-udfps-selection (version code 37).

Publishing the test branch was blocked by automatic approval review; no remote
animation branch/PR or CI artifact has been established. A local Gradle attempt
failed while downloading Gradle 8.7 with Network is unreachable, before compilation.
Do not bypass the publishing rejection through another transport.

## User-facing behavior

The lock-screen editor opens a separate three-column Fingerprint animation page.
There are 41 choices: Stock plus all 40 RisingOS entries (None and 39 effects).
Tap an effect for a looping preview, then Use animation to save the editor draft.
Enable customization applies saved choices through the existing persistent flow.
Back cancels; leaving the page stops preview playback. Selecting Stock restores
native drawing. None uses the upstream empty sequence; it does not disable scanning.

Keep a normal stock fingerprint animation enabled in phone settings: the module
replaces decorative frames inside native playback, so it requires that playback.
Position and scan duration remain native. Source XML timing is used in the preview;
on-device frames map source sequence phase onto the native frame count/duration.
These can have different apparent speeds. No user-facing expiry timer is added.

## Ownership and scope

Supported device: REDMAGIC NX809J, Android 16 / SDK 36 only.
Vector scopes: com.android.systemui and com.fingerprint.sensorservice.
Sensor APK device path:
`/system/priv-app/FingerprintSensorUi_ZTE/FingerprintSensorUi_ZTE.apk`.
Service: `com.fingerprint.sensorservice.UdfpsMessageService`.
Native surface: `com.fingerprint.sensorservice.view.AnimationView` (SurfaceView),
owned by FloatWindowAnimalView.authAnimal.

SensorAnimationBackend brackets native drawView and substitutes only the matching
Canvas.drawBitmap(Bitmap, Rect, Rect, Paint) invocation. Thread-local context plus
owner, source bitmap, paint, generation and deadline checks constrain the hook.
The snapshot uses the vendor M_RESOURCE_LOCK; it releases that lock before native
holder locking. Native destination, source list/bitmaps, paint, index/count,
completion callback, timers, touch, window visibility and illumination are untouched.
Attachment, visibility, native start/destroy flags and authority are rechecked at
substitution. Failed renderer initialization or processing leaves stock drawing.

Restoration removes substitution immediately; a previously posted surface frame
can remain until the next native redraw/clear. No forced surface/window operation
is performed. Phone testing must check this boundary. Face unlock naturally hiding
the fingerprint graphic is expected; never force it visible after recognition.
Enrollment, app authentication, AOD, disabled/quick states are excluded.

## Guard and persistence

The existing protected broker authorizes a frozen animation ID alongside the idle
icon and hide setting. SensorService independently reads authenticated replies;
System UI does not relay its authority. Same nonce/version/deadline/permit checks,
2-second response budget, 3-second polling, bounded internal health deadline,
independent expiry and startup cutoff remain. Busy responses never renew authority.
Expiry, revocation, teardown and stale-worker cancellation end both backends.
The existing companion ZIP is unchanged by this batch. Full boot-loop recovery
has not been proven; do not claim it has.

SurfaceStyle adds a 16th field fingerprintAnimation. Existing 9/10/11/15-field
encodings migrate to Stock (0); old 15-argument callers retain Stock. New IDs are
append-only. Idle IDs remain 0..188; animation IDs are a separate namespace 0..40.

## Source and packaging

Upstream: https://github.com/RisingOS-Revived/android_vendor_addons/tree/seventeen/themes/udfps
Pinned commit: 6372b6b8a0ef870150e5a0e706c3b4faccceeed9.
Importer: scripts/import-udfps-animations.py (Pillow; verifies source HEAD).
Manifest and original animation XML: third_party/rising-udfps/animations/.
Notices: third_party/rising-udfps/NOTICE.md and bundled assets/licenses notice.

1,354 referenced frames become 40 atlases plus 40 thumbnails, 37,087,916 bytes.
Tiles preserve aspect ratio within 256x256 transparent squares, eight columns per
atlas. Atlases use lossy WebP quality 95; previews are separate resampled images.
This is modified packaging, not byte-identical frame artwork. Each atlas decodes
to at most 16 MiB; one selected atlas is cached per process. In-flight references
are not recycled. Selection changes may temporarily retain old and new allocations.

## Code map

- FingerprintAnimations: IDs, labels, source timing and native phase mapping.
- FingerprintAnimationArtwork: bounded atlas load/cache and square preview drawable.
- FingerprintAnimationPickerActivity: dedicated page, direct card taps and dialog.
- SensorAnimationBackend: scoped invocation substitution and revocation.
- SensorFingerprintRuntime: worker decode and independent bounded broker authority.
- SensorFingerprintBackend: eligible native animation target and lifecycle refresh.
- GuardBrokerProvider: frozen animation ID and startup claim.
- SurfaceStyle / ClockEditorActivity: migration, draft saving and picker result.
- NubiaLyModule: module resources via android.content.res.XModuleResources.

## Validation completed locally

Policy smoke suite and every tests/test_*.py host test passed, including guard shell,
recovery store, broker/runtime, idle icon/catalog, clock, date, status bar, surfaces,
saved preferences and animation fixtures. Animation tests exercise all 40 entries,
identity isolation, original native properties, state exclusions, stop/expiry during
a frame, detach and teardown. Added regression checks recheck native stop/destroy
flags during a draw; the fake vendor lock is private like the real implementation.
The complete source asset manifest/hash/timing test passes. Guard ZIP packages.
Host fixtures are not Android or physical SurfaceHolder/compositor verification.

Unrun Android tests include FingerprintAnimationArtworkTest (all atlas decode bounds,
cache ownership, preview start/stop and dimensions) and SurfaceStyle migration tests.
CI must run full host suites, testDebugUnitTest, assembleDebug and assembleRelease,
then verify the public signing certificate:
`c87b3480b469c29c146d28c463a6c69ce4037c05be7a24e82c584a752a0b42e7`.
Do not view or expose signing secrets. Do not publish proprietary sensor APKs.

## One phone batch after signed CI succeeds

1. Install the signed update and confirm both Vector scopes, then reboot.
2. Keep stock animation enabled. Open animation picker, preview several families,
   cancel once, select one, and press Enable customization.
3. Scan with Rings + OnePlus Mclaren, then another idle icon + ROG or MIUI effect.
   Cover the face camera if necessary to keep face unlock from ending the scan.
4. Confirm effect alignment, no leftover square/background, normal scan duration,
   successful unlock, and normal unsuccessful-scan behavior.
5. Check app authentication and AOD remain stock; check face unlock still naturally
   hides the graphic. Repeat lock/wake and verify the chosen effect persists.
6. Restore stock while an effect is visible and between scans; check the next native
   frame/scan is stock and no stale surface persists. Test guard Action/Emergency stop.
7. Test None and Stock separately, then restore a custom effect. Check existing
   clock, QS and status bar settings survived. No timer endurance ritual is required.

If defective, report effect name, idle icon, relevant native animation setting,
face unlock state and Check fingerprint service output. Never disable Rescue Party,
clear System UI data, replace stock APKs, edit registry files or widen SELinux policy.
If the app cannot open, disable nubia-ly in Vector and reboot.

## Continuity notes for the next chat

User prefers large coherent customization batches and a graphical Iconify-like UI.
Styles should remain enabled until restored, with no artificial customization timer.
Termux commands must use su -c; never adb. Fingerprint disappearance after face
recognition was confirmed natural, not an outstanding bug. PR #17 is already merged;
create a new test branch and draft PR for animations. 0.1.36 direct-card tap tests
passed CI, but explicit phone confirmation of that exact build was not recorded.
Preserve append-only IDs, stock clock allocation/font fitting and idle-icon behavior.
Follow AGENTS.md; verify remote HEAD and preserve worktrees. Merge only after signed
build checks and the user's phone result/authorization. Do not present this local
implementation as a finished APK or repeat earlier unverified claims.
