# Quick Settings and lock-screen style batch — 0.1.25

NX809J only. Install the signed update over the current app, then reboot so Vector loads the new observers. Existing guard needs no update. The runtime stays in System UI; it does not modify lock-screen authentication or stock APKs.

## Broader style batch

Quick Settings circle tiles: **Stock**, **Square**, **Rounded square**, **Leaf**. The existing circle tile GradientDrawable is mutated in place with isolated drawable state; its colors and tile models remain stock. Wide top tiles keep their current shape. This does not change grid rows/columns or tile ordering. Stock resource updates and replacement backgrounds are tracked so Restore returns the latest stock geometry.

Four independently styled text targets each offer six controls:

| Target | Visual size | Opacity | Horizontal/vertical offset | Rotation | Hide |
|---|---|---|---|---|---|
| QS main labels | 80–120% | 25–100% | ±8 dp each | ±20° | Yes |
| QS secondary labels | 80–120% | 25–100% | ±8 dp each | ±20° | Yes |
| Normal lock-screen clock | 80–120% | 25–100% | ±8 dp each | ±20° | Yes |
| Normal lock-screen date | 80–120% | 25–100% | ±8 dp each | ±20° | Yes |

Size/opacity use 5% steps, offsets 2 dp, rotation 5°. Zero position and 100% appearance follow stock. Visual size scales the view without changing font metrics or allocating more layout space; combinations can clip or overlap. Hiding labels leaves tile actions intact. No clock text/locale/time-format or notification content is changed.

Quick Settings and Lock screen cards open the new grouped editor section. Each target's sliders are collapsed until needed. All selected settings apply together via the existing Enable button. Drafts remain separate from the active identity snapshot. Existing icon/battery reset preserves surface selections; full Restore stock, guard Action and Emergency stop release the whole style. Screen-off suspends styling; normal wake resumes it. Keyguard pulsing/nonzero dark amount restores its clock/date immediately or on the next guarded refresh; AOD uses stock.

## Inspected targets

The uploaded NX809J SystemUI_MFV.apk contains QSTileViewWide/Circle with getLabel/getSecondaryLabel, and NubiaKeyguardStatusView with mClockView (TextClock), mDateView (NubiaTextClock), mPulsing and mDarkAmount. Circle tiles place getColorBackgroundDrawable on the icon holder. Runtime verifies these signatures and scopes view writes to those exact owners/field identities. Unrelated text, wide tile backgrounds, dialogs and app UI are excluded. Notification-card styling is not part of this build.

## One combined phone test

1. Install/reboot, open Quick Settings from nubia-ly, choose Square and Enable. Expand the shade and confirm smaller circle tiles become square while the wide top tiles remain stock. Try Rounded square and Leaf. Check active/inactive tile colors, taps and long presses, page switching and brightness still behave normally.
2. Use different main/secondary label sizes, opacities and offsets. Enable after changes; check new tile states and shade reopening. Try hiding secondary labels. Avoid combinations that visually overlap.
3. Set a modest lock-screen clock size/offset and a different date style, Enable, then lock and wake the phone. Confirm the normal lock-screen text changes, time still advances, unlocking works and AOD/pulsing stays stock. Try date hiding independently.
4. Combine with your working status-bar style. Switch light/dark apps, close/reopen nubia-ly, and lock/unlock. Draft edits alone must not apply. Check the diagnostic counts after showing each surface; zero captures can indicate a different stock theme/view implementation and must be reported.
5. Restore stock while the shade or lock screen is visible. Verify shape, text position/size/opacity/visibility return to stock. Re-enable and use guard Action or Emergency stop; reboot and check persistence with the enabled configuration.

No timed session is required. Report the failing surface and displayed diagnostic if anything fails. These are new phone tests, not a claim of verified device compatibility.

## Automated scope

Production backends are exercised together for four text targets, unrelated-view exclusion, latest stock property tracking, non-compounding refresh, late attachments/root detach, pulsing/dark suspension, common restoration and apply/restore failure fences. Shape checks cover all three overrides, retained drawable/color identity, stock corner updates, replacement-background restoration and shape failure fences. Private preferences test frozen surface snapshots and malformed-selection fallback. Existing clock/status-bar/inventory/root/recovery suites remain in CI.


The initial 0.1.24 phone attempt was blocked before any view/shape captures by the surface signature check. Inspection confirmed stock QSTileViewImpl publishes covariant View bridge and TextView getter descriptors with identical names/parameters. Version 0.1.25 checks the explicit TextView descriptor instead of Android's ambiguous name-only lookup and reports the exact signature stage if another check fails. The harness now generates both bridge/getter variants and also rejects a View-only getter. Phone retesting is pending.


## 0.1.26 phone follow-up

The editor now names independent shape groups **Large QS tiles** (four wide top tiles) and **Small QS tiles** (below brightness). Existing small-tile selections migrate unchanged; the new large-tile group defaults to Stock. Both retain the original gradient, colors and actions.

Lock-screen discovery observes the container lifecycle (inflation, attachment, layout and dark/pulsing updates), including vendor overrides that bypass the base View attachment hook. Status lists clock/date captures separately from requested hiding. AOD/pulsing suspension remains in place. This addresses a reproduced discovery gap; the exact phone failure remains unconfirmed until retesting.

Install 0.1.26 and reboot to reload hooks. Enable both hide options and verify clock/date disappear on the normal lock screen; check the separate capture counts if they remain visible. Test independent large/small shapes and active/inactive tile states. Verify AOD, unlock, wake and Restore stock. Keep this PR draft until phone results and explicit merge authorization.

Local validation: all policy, production backend, preferences, inventory, recovery/root and guard shell suites pass. Added independent large/small geometry and migration coverage, plus a vendor attachment regression that bypasses the base View callback. Android compilation runs in CI.


## 0.1.27 personal-clock targeting and navigation

Phone confirms both QS groups work; clock/date remain visible in 0.1.26. Stock DEX inspection shows a second implementation: ClockContentView creates LockScreenClockDefault and LockScreenClockArtword. The art clock owns four ImageView digits and a DateView, while the default uses a vendor TextClock and DateView. These were outside the older NubiaKeyguardStatusView target.

The backend now validates and targets those exact fields under ClockContentView's SCENE_NORMAL (1); settings (2) and manager (3) previews are excluded. AOD has separate classes and is outside this target. Regression coverage includes all four images, default clock/date, stock reassertion, preview exclusion/scene changes, unrelated widgets and restore. Other personal-clock style classes are not yet supported. Phone validation is still required.

Home now leads with customization categories. Setup and recovery diagnostics are expandable. The editor has Status bar, Quick Settings and Lock screen sections; category cards open their matching section. Technical status is expandable, and shared Enable/Restore controls explain that all saved areas apply together.

Phone checklist: install/reboot, Enable both lock-screen hides and verify normal lock screen. Check capture counts if still visible. Verify Restore stock, lock-screen settings previews, wake and QS functionality. Review category navigation and expandable setup/recovery/details. Remain draft and unmerged.
