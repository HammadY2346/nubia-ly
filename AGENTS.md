# nubia-ly development

- Standalone project; do not merge its runtime into Toolbox.
- Initial support is NX809J only. Verify remote HEAD before editing; preserve dirty worktrees.
- Use test branches and draft PRs. The user tests APKs and authorizes merges explicitly.
- Device commands use Termux and su -c, never adb.
- Research baseline matches are not proof of customization safety.
- Do not ship unvalidated Quick Settings grid changes; prepare and verify recovery before any live test.
- Never disable Rescue Party, clear System UI data, replace stock APKs or edit registry files as a shortcut.
- Implement and test a scoped recovery contract before enabling the first cosmetic feature.
- Detailed area-prefixed commit messages; validation belongs in PR descriptions.

## UI standard approved by the user

- Preserve the main Personalizations dashboard introduced in 0.1.38. Add dashboard entries only when a new supported area (such as notification customization) needs one; do not redesign the existing dashboard without a request.
- Use the approved RisingOS-inspired appearance throughout current and future screens: dark neutral tonal surfaces, large rounded cards, visual previews, clear category rows, pill buttons and focused detail pages.
- Follow the 0.1.39 Lock screen, Quick Settings and Status bar editors: keep one draft and ordered save worker, return to the overview on Back, and retain explicit Enable/Restore behavior.
- Prefer visual selection cards and dedicated preview/picker pages over long inline chip/control lists. Keep technical diagnostics in expandable groups.
- Reference: docs/PERSONALIZATIONS_UI.md and RisingOS-Revived/android_packages_apps_Personalizations. The user identifies as a core Rising team member and authorizes use of Rising source for this project; retain upstream notices when reusing source or assets.
