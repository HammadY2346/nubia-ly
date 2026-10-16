# System icon pack artwork

Source: https://github.com/RisingOS-Revived/android_vendor_addons/tree/seventeen/themes/iconpacks
Pinned commit: `6372b6b8a0ef870150e5a0e706c3b4faccceeed9`.

526 app-bundled glyphs are adapted from the Android and SystemUI overlays of Acherus, Aurora, Circular, Filled, Gradicon, Kai, Lorn, Nostalgic, OOS, Outline, Plumpy, PUI, Rounded, Sam, Victor and Xperia. `ICON_PACK_ASSETS.json` records original paths and SHA-256 values. Existing XML copyright/license comments are retained. Upstream authors retain their rights and licenses; the project owner authorizes use of Rising sources as a Rising team member.

Resource names are prefixed, local drawable/gradient references are inlined, and unresolved theme color references become white for native tinting. Geometry and per-path alpha are preserved. Only the supported static glyph subset is bundled. Original overlays, settings/launcher resources, volume artwork and behavior code are not installed.

A bounded alias table maps inspected REDMAGIC resources. Stock objects are retained and only their drawing is substituted inside recognized Status bar/QS owners. Native actions, connectivity levels and tint remain authoritative. Explicit Wi-Fi and mobile selections override pack strength artwork in their existing Status bar scope. Unsupported and warning resources remain native.

NX809J QS can preload icons through `QSTileImpl.maybeLoadResourceIcon`: the `QsInCompose` path returns `DrawableIconWithRes`, a sibling of `ResourceIcon`, with its resource ID in `mId`. The renderer reads this verified bound model as well as `ResourceIcon.mResId`; it does not infer icon identity from pixel data or change tile state. This preserves provenance after background preload, drawable cloning and mutation. Plain drawable-only models without a verified resource identity retain native artwork.
