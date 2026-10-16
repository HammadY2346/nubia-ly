# Rising boot-animation source assets

Source: RisingOS-Revived/android_vendor_rising, commit `8e4650c0ba33de2dfcc1f9748bc2dcdc0d819371`, `prebuilts/bootanimation`.

`catalog.json` pins all five original ZIPs by SHA-256 and byte count. CI fetches only those immutable URLs and verifies them before copying them unchanged into the APK. No download occurs on the phone. The generated asset folder is ignored by git. Run `python3 scripts/build-boot-assets.py` to reproduce it (Pillow 11.3.0).

App previews are bounded, low-resolution WebP sequences derived from the original frames/trim data, with a single pass rather than the boot player's repeated loops. Google Monet's RGBA color masks are reconstructed using the inspected AOSP-style fragment shader and the descriptor's default palette; dynamic device colors may differ. Original ZIP bytes, frame rates and geometry are never rewritten.

Artwork/branding remains credited to the upstream Rising project and respective original authors. No blanket artwork license is inferred from the repository's makefile license header. The owner identifies as a Rising core team member and authorizes source reuse in nubia-ly.
