# QS styles, stage 2 — layered effects (0.1.41)

Order remains Outline → layered effects → brightness styles. The user reported
that 0.1.40 Outline works and supplied a screenshot showing active/inactive outlines
on large and small tiles. Other detailed stage-1 phone checks were not reported.

This batch appends six choices: CyberPunk, Neumorph, Reflected, Shaded, Surround and
Thin. Stock=0 and Outline=1 retain their existing meanings and renderer. IDs 2..7
are append-only; unknown IDs fail validation. There is no two-tone style or
brightness implementation in this batch. The approved dashboard is unchanged.

Reference source: RisingOS-Revived/android_vendor_addons, seventeen/themes/qs.
The six upstream qs_tile_background_shape.xml files and their night variants were
inspected. The implementation adapts their facets, bevels, reflection, shade,
leading capsule and thin-rim treatments to native RedMagic colors and independent
shapes. It does not claim pixel-identical AOSP overlays, private resource dimensions,
or exact Rising accent palettes. No upstream source/assets are copied.

The existing owner/identity/authority-gated draw hook now selects the frozen effect
ID. Each layered effect draws the current native background once in a bounded
canvas layer and applies small static light/dark overlays with SRC_ATOP. Native
alpha and the tile's silhouette remain authoritative. The source drawable's colors,
tint, alpha, corner arrays, bounds, callbacks, tile state and actions are untouched.
Tinted active and neutral inactive backgrounds remain the basis of the effect;
artistic shading changes their visible RGB values. Outline still uses its tested
separate interior/ring composition. No animation/polling loop or bitmap cache is
added. Layered effects add one temporary GPU layer and a few vector draws per tile;
physical-device scrolling and label contrast still require testing.

Revocation/failure discards the temporary layer before returning to stock drawing.
Existing detach/replacement/thread checks, guard cleanup and failure fencing remain.
The style picker and QS overview use the same renderer as production. Two-column
cards stack for large text. Saved drafts still require Enable; Restore/Stock remain.

Validation: host fixtures verify every new ID reaches the scoped hook with existing
lifecycle/failure gates. Native graphics tests verify six distinct outputs, all-pixel
alpha preservation, tinted-color dominance, unchanged geometry, canvas balance,
revocation/failure discard and unsupported fallback. Existing Outline pixel tests
remain unchanged. Android CI also covers migration, draft-only choice, UI routing,
all host suites and pinned-signature debug/release packaging.

Phone batch: install/reboot; apply each effect across large/small Stock/Rounded/Leaf
shapes; inspect active/inactive/unavailable tiles, light/dark themes and label contrast;
check toggles/ripple, scrolling, replacement/reorder, wake/persistence, Outline and
Stock selection, Restore and guard stop. Screenshot effect names when reporting.
Next stage is brightness styling after separate vendor-slider inspection.
