# NX809J brightness appearance

Stage 4 follows the tested Outline and layered tile work. Keep all three changes
unmerged until the user tests brightness and explicitly authorizes merging.

## Inspected vendor path

The original owner-provided SystemUI_MFV.apk contains
`com.zte.controlcenter.view.ToggleSliderView.getSlider(): ToggleSeekBar` and a
mirror of the same ToggleSliderView type. ToggleSeekBar directly extends SeekBar
and inherits its drawing implementation. BrightnessThemeModule.updateSliderResources
sets the native progress drawable and a null thumb. Native resources
`toggle_slider_progress` (0x7f082c05), `toggle_slider_progress_mirror`
(0x7f082c06), and `brightness_progress` (0x7f080c53) contain exactly two layers:
background GradientDrawable and progress ClipDrawable wrapping GradientDrawable.
Both native progress clipping and theme colors remain authoritative.

The draw observer accepts only the exact ToggleSeekBar class, attached, underneath
an exact ToggleSliderView whose getSlider() returns that same view. The leaf draw
hook additionally checks the current progress root and its inspected two-layer
structure each time. Replaced, detached, unrelated, shared and unknown drawables
are excluded. No global drawable/resource replacement is performed.

## Choices

- Stock: no brightness drawing hooks.
- Outline: native color border and half-alpha interior, using the tested outline
  compositor on the native track and progress leaves.
- Neumorph: opposing inset bevel gradients using the tested layered compositor.
- Thin: native drawing clipped to a centered 4 dp rounded track; the full original
  view size, touch area, progress clip and accessibility remain stock.

These are code-native adaptations of RisingOS vendor-addons brightness styles,
not unmodified overlays. Native corner geometry and progress behavior are kept;
no ROM-specific dimensions, custom thumbs or icon assets are installed. Source:
https://github.com/RisingOS-Revived/android_vendor_addons/tree/seventeen/themes/qs/brightness
Outline reference carries Copyright (C) 2022 CorvusOS, Apache-2.0; Thin reference
carries Copyright (C) 2023 The LeafOS Project, Apache-2.0. No XML/assets are copied.

The slider's values, automatic brightness toggle, callbacks, animations, padding,
height, display settings, listeners and brightness mirror plumbing are untouched.
There are no bitmap caches, polling loops, disk writes or new animations during
drawing. The existing SurfaceBackend guard owns activation/cleanup. On revocation
or native draw failure, incomplete temporary layers are cleared; hooks are removed
on restoration and known sliders invalidated to repaint stock.

Selection is a new eighteenth SurfaceStyle field; legacy encodings default to
Stock. The focused brightness page uses the approved visual cards and ordered
save worker. Selection only saves a draft; Enable applies and Restore removes it.
Main dashboard remains unchanged.

## Validation and device test

Host doubles exercise both normal and mirror owners, unrelated seekbars,
replacement roots, unknown layers, detach, stale owner identity, off-thread draws,
revocation, failure and hook cleanup. Native Android pixel tests cover progress
levels 0/1/2500/5000/9999/10000, Thin clipping, original drawable bounds/levels,
revocation/failure and balanced canvas state. UI tests cover navigation and draft
selection without changing recovery authority.

After installing and rebooting, open Quick Settings → Brightness slider. Test
each style with Enable. Drag through minimum, middle and maximum; confirm mirror
appearance, auto-brightness toggle, normal response and tile styles. Test light/
dark themes, shade close/reopen, rotation, wake, then Restore stock. Recovery is
the existing guard Action or disabling nubia-ly in Vector and rebooting.

## 0.1.43 Control Center detection correction

Device feedback on 0.1.42 showed brightness style 2 enabled with zero tracked
layers. The initial exact-class filter excluded the vendor Control Center path.
The inspected APK also contains CCToggleSeekBar, a direct ToggleSeekBar subclass,
owned by CCBrightnessToggleSliderView, itself a CCToggleSliderView subclass.
The same getSlider() identity contract and native drawable layers apply.
Both standard and exact Control Center brightness owners are now accepted; the
generic CCToggleSliderView and other control sliders remain excluded. Regression
host tests exercise the Control Center brightness slider and mirror and reject a
generic Control Center slider. Diagnostics now distinguish observed sliders,
draw callbacks and the last progress-root class to make detection failures clear.

## 0.1.44 discovery without inherited draw callbacks

0.1.43 phone diagnostics reported two observed sliders but zero inherited draw
callbacks and no registered layers. Activation now resolves each observed,
attached slider's current native drawable layers immediately. Late lifecycle
observations also register layers before invalidation, and existing guarded
surface refresh revalidates current roots. Styling no longer depends on inherited
onDraw dispatch. The original draw observer remains a fallback. A regression
executes native leaf drawing without calling the inherited draw hook and verifies
both Control Center slider/mirror styling, late discovery, replacement-root
refresh, stale-root exclusion, release and restoration. Diagnostics additionally
count successful decorated draws, separately from inherited draw callbacks.

## 0.1.45 mixed stock owner/child pairing

0.1.44 device diagnostics showed observed CCToggleSeekBar rejected by owner
validation. Full compiled XML inspection resolves the actual horizontal path:
ToggleSliderView's constructor inflates 0x7f0e061f, layout
zte_quick_qs_toggle_slider (res/8Z0.xml). That merge contains CCToggleSeekBar at
id slider, with native brightness_progress. Thus exact ToggleSliderView can own
either the base ToggleSeekBar or CCToggleSeekBar. The getSlider() identity contract
remains required. This mixed pairing is now accepted; generic CCToggleSliderView
and volume owners remain excluded. The zero-onDraw regression now reproduces
that precise XML pairing for slider, mirror and late discovery.
