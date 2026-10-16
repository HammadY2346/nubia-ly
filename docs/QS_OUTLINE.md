# QS styles, stage 1 — Outline (0.1.40)

Requested order: 1 Outline, 2 layered effects, 4 brightness styles. Two-tone and
A11/BetterQS layout overrides are outside this sequence. This build implements
Outline only. The main Personalizations dashboard stays unchanged.

Quick Settings → Tile style has Stock and Outline cards. Selecting a card updates
the saved editor draft and illustrative preview. Enable customization applies it;
Stock + Enable or Restore stock appearance returns native drawing. Large and small
tiles share this style while retaining their independently selected shapes.

Visual reference:
https://github.com/RisingOS-Revived/android_vendor_addons/tree/seventeen/themes/qs/QSOutline
The upstream design uses a 2.5 dp border and translucent fill. This implementation
composes the stock tile's native draw at half fill opacity with a full-strength
border, keeping its own state colors, tint, alpha, gradients and selected geometry.
No upstream XML/resources are copied. It is an adaptation to RedMagic, not an
identical Rising resource overlay.

The new hook targets only GradientDrawable.draw calls for the exact background
identity returned by inspected NX809J QSTileViewWide/QSTileViewCircle owners already
tracked by SurfaceBackend. The shape backend remains independent. Unsupported
shapes, detached/replaced owners, other threads and unrelated drawables stay stock.
The renderer saves/restores canvas state in all exits, checks authority between
native passes, and never changes drawable colors, alpha, tint, geometry, callbacks,
ripple layers, tile actions, layout, models or brightness values. No bitmap copies,
extra polling or timers are added. Two native background draws replace one while
Outline is enabled; phone smoothness/contrast still needs checking.

Restoration removes the draw hook and invalidates tracked owners so the next frame
is stock. Guard revocation ends this backend with the existing appearance session.
Drawing failures stop decoration and report failed health through SurfaceBackend.
Already posted pixels await native redraw; no forced window/surface work is added.

SurfaceStyle appends a seventeenth field qsStyle (0 Stock, 1 Outline). All older
accepted encodings migrate to Stock, and previous constructor signatures retain
that default. Unknown/malformed style IDs fail validation. Saved/frozen choices
continue through the same existing identity-bound guard contract.

Validation includes production hook isolation/lifecycle/failure host tests, native
Android pixel tests for tint/state/alpha/corners and canvas restoration, migration,
UI draft-only choice and existing guard/backend suites. Host fixtures are not a
physical-device test. CI must build and verify the signed APK before phone testing.

Phone batch: install/reboot; try Outline with each large/small shape; toggle active,
inactive and unavailable tiles; compare light/dark modes and expanded/collapsed QS;
verify ripple, labels, actions and brightness stay normal; change/reorder tiles;
check wake, scrolling, saved choices, Stock + Enable, Restore, and guard stop. Record
large vs small tile coverage and any low-contrast states. Next is layered styles
only after this base is validated, then separate RedMagic brightness inspection.
