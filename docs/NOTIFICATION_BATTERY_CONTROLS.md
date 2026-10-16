# Notification counts and battery controls

Notifications → Counts & icon limits adds an optional overflow count and Stock / 1–8 maximum icon slots. Overflow occupies one slot. The count is the number of additional eligible notification icon entries represented by the overflow dot, including the dot entry; Android grouping still applies. This is not an unread-message count. Physical space and the vendor maximum may lower the selected ceiling. Delivery, shade entries, grouping and ranking are unchanged.

Status bar → Battery styles & controls adds Stock / Inside / Outside / Hidden percentage, Hide charging symbol, and the existing 80–120% battery scale. The scale has one control here; Icons & battery links to this page. Fixed normal/charging previews illustrate the draft. All controls save to the shared draft and require Enable. Text only always displays its number; Hidden hides the whole group. Charging can replace an inside percentage. No dashboard changes.

## Inspected NX809J Android 16 native boundaries

`NotificationIconContainer` directly extends ViewGroup. Its `calculateIconXTranslations()` uses `mMaxIcons` as the fourth argument of `shouldForceOverflow(int,int,float,int)`. The latter compares the current child index to the maximum and requires positive appearance. The customization bounds that argument for the attached top `PhoneStatusBarView` subtree only. It never writes `mMaxIcons` or notification/icon models. The stock overflow algorithm, animations and space checks remain in charge. Isolated heads-up icons are excluded.

`StatusBarIconView.onDraw(Canvas)` is substituted only for a notification in that owned container whose native visible state is DOT (1). Count uses current `mIconStates` hidden/appearance/visible-state values and excludes blocked icons. Tint follows current native `mDecorColor`. No new views, collection listeners, timers or polling. End removes hooks and runs native `updateState()` on live owned containers so current native layout returns. QS, keyguard/AOD and shelf containers are excluded by root ownership.

Battery controls use inspected `MFVBatteryViewLayout` and its current native icon, inside/outside percentage and charge views. The current icon must be the recognized vendor class and artwork, have a valid level and status-bar type, and have no charging separation or overvoltage mode 128. The container and each text/charge view must have their expected direct parent. No battery-model/settings writes. Percentage text reads the native current level, formats locally, and follows native update callbacks. Inside/outside view visibility changes are scoped to those owned views. Charging hiding also handles native ChargeIndicator updateVisibility callbacks. Unknown/warning/separated state restores current vendor layout. Existing custom battery rendering reads the resulting inside visibility and suppresses its charging glyph when requested.

Restore and independent guard stop call native `updateBatteryLayout(getLevelDisplayMode(), true)` and `updateBatteryLevelText()` with the current native setting/data. They do not restore a stale percentage. Native settings changes during customization are therefore honored on Restore. Native battery size transform/recovery remains in StatusBarBackend. Percentage controls do not force a battery visible if stock hides its container.

Four append-only fields extend SurfaceStyle from 27 to 31 values: percentage 0–3, hide charge 0–1, overflow count 0–1 and icon slots 0–8. All older supported layouts default these to stock; invalid configurations are rejected.

## Validation

Host firmware-shaped regression checks cover live level updates, percentage modes, charging callback suppression, current-setting restoration, warning/unrelated root/type/off-main exclusions; overflow threshold capping, current count, blocked/hidden filtering, isolated/unrelated roots, canvas balance and restoration. Existing backend, preferences, policy and guard checks remain. GitHub runs Android editor recreation/draft and native graphics tests and produces the APK; there is no local Android build.

Phone batch: Enable an outside percentage and hidden charge, plug/unplug; then test inside/hidden percentages and 80/120% sizes. Restore stock. With enough notifications to overflow, enable count and 2–3 slots, add/dismiss notifications and check current counts. Open/close heads-up, rotate, change tint/background, lock/unlock, then Restore and stop guard independently. Confirm ordinary shade entries and charging diagnostics stay unchanged.
