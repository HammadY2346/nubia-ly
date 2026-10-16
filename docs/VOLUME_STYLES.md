# Volume styles

The approved main dashboard retains its three original cards and gains one
separate Volume bubble. It opens a focused Volume editor using the same tonal
cards, previews, Back navigation, ordered save worker and explicit Enable/Restore.
Stock plus six code-native designs adapt Rising's VolumeOutline, VolumeGradient,
VolumeNeumorph, VolumeNeumorphOutline, VolumeDoubleLayer and VolumeShadedLayer.

Reference: https://github.com/RisingOS-Revived/android_vendor_addons/tree/seventeen/themes/Volume
Rising's overlay manifests/build files carry Copyright (c) 2023, the RisingOS
Android Project, Apache-2.0. No upstream XML or assets are copied here. The supplied
folder mainly themes the ringer drawer selection drawable, colors and dimensions.
This implementation adapts those visual motifs to the vendor slider track, not
an unmodified overlay or a full replacement of Rising's volume layout.

## Actual vendor path and scope

Owner-provided SystemUI DEX identifies MfvVolumeDialogImpl extending
VolumeDialogImpl, its initRow(VolumeRow,int,int,int,boolean,boolean) and
showH(int,boolean,int), and its base mRows list. VolumeRow carries view and slider.
initRow explicitly handles com.zte.mifavor.widget.ColorSeekBarZTE. That slider
extends SeekBar and declares onDraw(Canvas), mLeftPadding/mTopPadding/mLength/
mTrackSize and eight mRadii values. It paints its own track and progress rather
than using the brightness drawable structure.

Discovery observes the exact vendor dialog's row creation and show lifecycle,
including before customization is enabled. Activation rescans known rows and
invalidates them. Each draw requires attached exact vendor slider, exact dialog,
current row membership, current slider identity and ancestry within the row view.
No arbitrary seekbars, QS volume/brightness controls or alternate dialogs qualify.
The declared vendor onDraw is hooked directly, avoiding the inherited framework
onDraw dependency discovered during brightness testing.

Each successful decoration invokes the original vendor draw exactly once. A
bounded canvas clip layer receives static shading/bevels; Outline lowers interior
alpha. On cancellation/failure the temporary drawing is discarded. No native
fields, volume values, stream/mute/ringer state, audio APIs, listeners, gestures,
layout dimensions or resource mappings are changed. Native safety warnings and
accessibility remain authoritative. Restore removes the draw hook and invalidates
known sliders. No polling, disk I/O, bitmap caches or animation loops are added.

SurfaceStyle adds a nineteenth volume field; older encodings default to Stock.
Volume is applied under the existing shared customization guard. Restore uses the
existing common stock restoration action across personalization areas.

## Tests and phone checklist

Host tests execute production hooks and verify exact row/dialog identity,
unrelated sliders, one original draw, membership changes, replacement, detach,
thread, revocation/failure and cleanup. Native Android pixels verify six distinct
outputs, native alpha/bounds and radii preservation except intended Outline alpha,
canvas balance and no partial composition after cancellation/failure. UI tests
cover Volume navigation and selection saving a draft without enabling recovery.

Phone: install/reboot, open dashboard Volume → Volume styles, choose a style and
Enable. Press a physical volume key. Check collapsed and expanded media/ring/alarm
rows, low/middle/max levels, mute, safe-volume warning behavior, dark/light theme,
panel dismiss/reopen and Restore stock. Check diagnostics for rows and decorated
draws if the panel remains stock. Main dashboard otherwise stays as approved.
