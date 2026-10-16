# Public release procedure

1. Complete on-device testing of the proposed 1.0.0 APK, including shared icon packs on QS and Status bar, explicit connectivity overrides and restoration. Resolve failures before merging.
2. Obtain the owner's merge approval, then merge the tested PR. Main CI builds and verifies the persistent signatures again.
3. After successful **main** CI, **Prepare public release draft** downloads the signed release APK and companion guard from that exact run. It creates a **draft** `v1.0.0` release with readable filenames, the release notes and SHA-256 checksums. It does not publish automatically or rebuild binaries in the privileged release job.
4. Check that the draft targets the approved main commit, that its three assets are present, and that the release APK/guard correspond to the successful CI run. Review the notes and install guide before publishing with the owner's authorization.

A matching existing release is never overwritten. Publishing is a separate action. The workflow requires the signed release artifact; a secretless/ephemeral CI build cannot silently become an official release. Only successful runs from this repository's main branch qualify.
