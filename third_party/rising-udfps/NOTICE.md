# RisingOS UDFPS artwork

Imported unchanged from https://github.com/RisingOS-Revived/android_vendor_addons at commit 6372b6b8a0ef870150e5a0e706c3b4faccceeed9, `themes/udfps/UdfpsIcons/res/drawable`, in the order of its `res/values/arrays.xml`.

The catalog carries Copyright (C) 2024 The Evolution X Project and Apache License 2.0. The default vector carries Copyright (C) 2020 The Android Open Source Project and Apache License 2.0; its notice is retained in the resource. Apache License 2.0 is included alongside this notice. Original artwork and trademarks belong to their respective owners; no ownership or endorsement is claimed. This import does not relicense third-party artwork. The manifest records exact source names and hashes.

Changes: resource filenames are prefixed `rising_fp_` to avoid collisions. Images and vector contents are unchanged. nubia-ly uses these as static idle lock-screen graphics; vendor biometric behavior is unchanged.

## Animation collection

The complete UdfpsAnimations catalog from the same upstream commit is included as 40 animation entries (None plus 39 effects), alongside the phone's Stock choice. Animation XML notices are retained under `animations/`. These include The AospExtended Project (2021), The Evolution X Project (2019–2020 and 2024), and the notices appearing in each original XML file.

Animation frames are resampled to fit a 256 by 256 pixel tile without changing aspect ratio, centered on transparent tiles and packed into eight-column WebP atlases at quality 95. Thumbnail previews are resampled separately. The manifest records source frame names/hashes, sequence timing and packed atlas hashes. These are modified packaging derivatives, not byte-identical source images. Playback uses the vendor's native scan duration; the picker loops the source sequence timing. Artwork and trademarks remain the property of their respective owners.
