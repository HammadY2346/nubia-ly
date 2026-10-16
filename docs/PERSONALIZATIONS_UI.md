# Personalizations interface — 0.1.38

RisingOS Personalizations is the visual reference:
https://github.com/RisingOS-Revived/android_packages_apps_Personalizations

This app-native implementation uses a dark neutral palette, tonal rounded surfaces,
a visual home dashboard (tall lock-screen card plus Quick Settings and status-bar
cards), consistent rounded buttons and toolbar navigation. Dashboard art is an
illustration, not a live capture of the phone. Compact screens and large text use
stacked cards. Existing setup, emergency stop and diagnostics remain reachable.

Clock font styles now opens a separate page with a large alphabet/digit/time
sample, a dropdown rendered in each existing face and an explicit Apply action.
Selecting a dropdown row previews only. Apply returns an editor draft; the existing
Enable customization action still grants appearance authority. Back cancels.
Selection survives activity recreation and the font menu dismisses on pause.

This batch uses the existing 26 Android font-style IDs. It does not add RisingOS
font files, system-wide fonts, wallpaper access or ROM-only settings APIs. No new
runtime hook, root operation, broker permission or persisted style format is added.
The source reference guides layout; no upstream source/assets are copied.

Validation: GitHub CI runs host checks, Android tests (including picker draft/apply,
cancellation and recreation), debug/release packaging and pinned signing checks.
Phone check: browse all three dashboard cards, back navigation, font dropdown and
preview, cancel then Apply, Enable and Restore. Check large text, landscape,
scrolling and access to setup/recovery. Animations from merged PR #18 remain.

## Inner appearance editors — 0.1.39

The approved main dashboard is unchanged from 0.1.38. Lock screen, Quick Settings
and Status bar now open illustrative draft previews with tonal navigation rows.
Lock screen groups clock, date, fonts, fingerprint and widgets. Quick Settings
separates shapes from label styles. Status bar groups clock typography, date,
clock color, icons/battery and individual indicator controls. Icon details use
expandable tonal groups to reduce the visible control count.

Overview/detail navigation stays in one activity with one draft and one ordered
save worker. Toolbar and Android Back return from details to the section overview;
a second Back returns to the main dashboard. The current section/page is saved
through recreation. Previews are app-only samples, not captures or exact device
layout simulations. No polling, root work or running animation is added by preview
rendering. Enable/Restore still owns real appearance changes across all sections.

Phone check: overview/detail/Back for every group, controls and draft persistence,
font/icon/animation picker return and cancellation, large text/rotation, all three
previews and Apply/Restore. UI tests cover in-place routing and draft view retention.
